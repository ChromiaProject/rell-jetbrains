package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import net.postchain.rellide.jetbrains.toolwindow.execution.ChromiaCommandExecutor

/**
 * Runs `chr install` for the library dependencies of [file]. `chr` reads `chromia.yml` by default
 * and fails outright when the project names its settings file something else, so the governing
 * file is passed via `--settings` exactly as the tool window does for project commands.
 */
object ChromiaInstallCommand {
    fun run(project: Project, file: VirtualFile) {
        val configFile = RellVersionResolver.getInstance(project).governingConfigFile(file)
        ChromiaCommandExecutor(project).executeCommand(commandFor(configFile?.name), configFile?.parent?.path)
    }

    fun commandFor(settingsFileName: String?): String =
        if (settingsFileName == null || ChromiaSettingsFiles.isDefaultName(settingsFileName)) "chr install"
        else "chr install --settings $settingsFileName"
}
