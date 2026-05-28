package net.postchain.rellide.jetbrains.toolwindow.execution

import com.intellij.execution.RunContentExecutor
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import net.postchain.rellide.jetbrains.settings.RellPluginSettingsState
import java.io.File


/**
 * Executes Chromia CLI commands in a Run tool window tab.
 * Each command runs as its own process with output shown in a dedicated console, mirroring how
 * IDEA launches Java/Gradle/shell run configurations rather than typing into a user-owned terminal.
 */
class ChromiaCommandExecutor(private val project: Project) {
    companion object {
        val logger = Logger.getInstance(ChromiaCommandExecutor::class.java)
        private const val TAB_NAME = "Chromia"
    }

    fun executeCommand(command: String, workingDirectory: String? = null) {
        ApplicationManager.getApplication().invokeLater {
            try {
                executeInRunTab(command, workingDirectory)
            } catch (e: Exception) {
                logger.error(e)
            }
        }
    }

    private fun executeInRunTab(command: String, workingDirectory: String?) {
        val commandLine = RellPluginSettingsState.instance.buildChromiaCliCommandLineFromString(command)

        if (commandLine == null) {
            logger.warn("No Chromia CLI command configured; cannot run: $command")
            return
        }

        commandLine.withWorkDirectory(File(workingDirectory ?: project.basePath ?: "."))

        val processHandler = KillableColoredProcessHandler(commandLine)
        ProcessTerminatedListener.attach(processHandler)

        RunContentExecutor(project, processHandler)
            .withTitle("$TAB_NAME: $command")
            .withActivateToolWindow(true)
            .withStop({ processHandler.destroyProcess() }, { !processHandler.isProcessTerminated })
            .withRerun { executeCommand(command, workingDirectory) }
            .run()
    }
}
