package net.postchain.rellide.jetbrains.projectwizard

import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VfsUtil
import net.postchain.rellide.jetbrains.settings.RellPluginSettingsState
import java.nio.file.Path
import kotlin.io.path.*

/**
 * Generates a dapp for the New Project wizard by running `chr create-rell-dapp`. The CLI refuses
 * to generate into an existing directory while the wizard pre-creates the project root (with
 * `.idea` inside), so the dapp is generated into a temporary directory inside the project root
 * and its contents are then moved up.
 */
object ChromiaDappGenerator {
    private val logger = Logger.getInstance(ChromiaDappGenerator::class.java)
    private const val TIMEOUT_MS = 120_000

    fun generate(
        project: Project,
        projectRoot: Path,
        template: ChromiaProjectTemplate,
        devcontainer: Boolean,
    ) {
        val error = ProgressManager.getInstance().runProcessWithProgressSynchronously<String?, RuntimeException>(
            {
                val result = runCatching { doGenerate(projectRoot, template, devcontainer) }
                    .getOrElse { it.message ?: it.toString() }
                // Synchronous refresh is only legal off the EDT; on the EDT even the dirty-marking
                // walk trips the SlowOperations assertion.
                VfsUtil.markDirtyAndRefresh(false, true, true, projectRoot.toFile())
                result
            },
            "Generating Chromia Dapp",
            false,
            project,
        )
        if (error != null) {
            logger.warn("chr create-rell-dapp failed: $error")
            Messages.showErrorDialog(project, error, "Create Chromia Project")
        }
    }

    /** Returns an error message, or null on success. */
    @OptIn(ExperimentalPathApi::class)
    private fun doGenerate(projectRoot: Path, template: ChromiaProjectTemplate, devcontainer: Boolean): String? {
        val dappName = projectRoot.name
        val tempDir = createTempDirectory(projectRoot, ".chromia-gen")
        try {
            val args = buildList {
                add("create-rell-dapp")
                add(dappName)
                add("--template=${template.cliName}")
                if (devcontainer) add("--devcontainer")
                add("-d")
                add(tempDir.toString())
            }
            val commandLine = RellPluginSettingsState.instance.buildChromiaCliCommandLine(args)
                ?: return "Chromia CLI is not configured and none of the standard locations were found."
            val output = CapturingProcessHandler(commandLine).runProcess(TIMEOUT_MS)
            if (output.isTimeout) return "Chromia CLI did not finish within ${TIMEOUT_MS / 1000} seconds."
            if (output.exitCode != 0) {
                return buildString {
                    appendLine("chr create-rell-dapp failed with exit code ${output.exitCode}.")
                    append(output.stderr.trim().ifBlank { output.stdout.trim() })
                }.trim()
            }
            val generated = tempDir.resolve(dappName)
            if (!generated.isDirectory()) {
                return "Chromia CLI reported success but produced no project directory."
            }
            for (child in generated.listDirectoryEntries()) {
                child.moveTo(projectRoot / child.name)
            }
            return null
        } finally {
            tempDir.deleteRecursively()
        }
    }
}
