@file:Suppress("UnstableApiUsage")

package net.postchain.rellide.jetbrains.settings

import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.ComponentWithBrowseButton
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import net.postchain.rellide.jetbrains.chromia.RellVersionResolver
import javax.swing.JComponent
import javax.swing.JPanel

private const val TEST_TIMEOUT_MS = 60_000

/**
 * Supports creating and managing a [JPanel] for the Settings Dialog.
 */
class RellPluginSettingsComponent {
    private val indexCaching = JBCheckBox("Enable/disable caching of Rell project index. (Restart required)")

    private val commandTextField = ExpandableTextField().apply {
        emptyText.text = RellPluginSettingsState.detectChromiaCliPath() ?: "chr"
    }

    private val commandField = ComponentWithBrowseButton(commandTextField, null).apply {
        addActionListener {
            val descriptor = FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor()
                .withTitle("Select Chromia CLI Executable")
            FileChooser.chooseFile(descriptor, null, null)?.let { file ->
                commandTextField.text = file.presentableUrl
            }
        }
    }

    private val settingsPanel: JPanel = panel {
        row { cell(indexCaching) }
        row("Chromia CLI:") {
            cell(commandField)
                .align(AlignX.FILL)
                .resizableColumn()
                .comment(
                    "Path to the Chromia CLI or a shell command. Leave blank to use the " +
                            "auto-detected path. Executed via the system shell."
                )
            button("Test") { executeTest() }
        }
        if (RellPluginSettingsState.isDockerAvailable()) {
            row("") {
                link("Use Docker image for CLI") {
                    commandTextField.text = RellPluginSettingsState.dockerCliCommand()
                }
            }
        }
    }

    fun getPanel(): JPanel = settingsPanel

    fun getPreferredFocusedComponent(): JComponent = indexCaching

    var indexCachingState: Boolean
        get() = indexCaching.isSelected
        set(newValue) {
            indexCaching.isSelected = newValue
        }

    var chromiaCliCommandState: String
        get() = commandTextField.text
        set(newValue) {
            commandTextField.text = newValue
        }

    private fun executeTest() {
        val userCmd = commandTextField.text.trim().takeIf { it.isNotBlank() }
        val commandLine = RellPluginSettingsState.instance.buildChromiaCliCommandLine(
            args = listOf("--version"),
            overrideCommand = userCmd,
        ) ?: run {
            Messages.showErrorDialog(
                "Chromia CLI is not configured and none of the standard locations were found.",
                "Test Chromia CLI"
            )
            return
        }

        ProgressManager.getInstance().run(object : Task.Modal(null, "Testing Chromia CLI", true) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = true
                indicator.text = "Running ${userCmd ?: "auto-detected chr"} --version"

                val handler = CapturingProcessHandler(commandLine)
                val result = runCatching { handler.runProcessWithProgressIndicator(indicator, TEST_TIMEOUT_MS) }

                ApplicationManager.getApplication().invokeLater {
                    result.onSuccess { output ->
                        when {
                            output.isCancelled -> Unit
                            output.isTimeout -> Messages.showErrorDialog(
                                "Process did not respond within ${TEST_TIMEOUT_MS / 1000} seconds.",
                                "Test Chromia CLI"
                            )

                            output.exitCode == 0 -> {
                                RellPluginSettingsState.instance.recordChrVersionOutput(userCmd, output.stdout)
                                // The Chromia tool window compares the tested CLI's Rell version
                                // against each project's settings file — let it recompute.
                                ProjectManager.getInstance().openProjects.forEach { project ->
                                    project.messageBus.syncPublisher(RellVersionResolver.TOPIC).chromiaConfigChanged()
                                }
                                Messages.showInfoMessage(
                                    output.stdout.trim().ifBlank { "Executable responded successfully." },
                                    "Test Chromia CLI"
                                )
                            }

                            else -> {
                                val message = buildString {
                                    appendLine("Exit code: ${output.exitCode}")
                                    when {
                                        output.stderr.isNotBlank() -> append(output.stderr.trim())
                                        output.stdout.isNotBlank() -> append(output.stdout.trim())
                                    }
                                }
                                Messages.showErrorDialog(message, "Test Chromia CLI")
                            }
                        }
                    }.onFailure { e ->
                        Messages.showErrorDialog(
                            e.message ?: e.javaClass.simpleName,
                            "Test Chromia CLI"
                        )
                    }
                }
            }
        })
    }
}
