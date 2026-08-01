package net.postchain.rellide.jetbrains.testing.actions

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import net.postchain.rellide.jetbrains.language.RellFileType.Companion.RELL_EXTENSION
import net.postchain.rellide.jetbrains.lsp.RellTestFile
import net.postchain.rellide.jetbrains.lsp.rellLanguageServerIsRunning
import net.postchain.rellide.jetbrains.services.RellProjectService
import net.postchain.rellide.jetbrains.testing.RellTestConfigurationFactory
import net.postchain.rellide.jetbrains.testing.RellTestRunConfiguration
import net.postchain.rellide.jetbrains.testing.TestScope

/**
 * Action to run Rell tests from the editor or project view.
 */
class RunRellTestAction : AnAction("Run Rell Test", "Run the selected Rell test file", null) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val rellTestFile = getRellTestFile(project, virtualFile) ?: return

        val runManager = RunManager.getInstance(project)
        runTestConfiguration(runManager, createTestConfiguration(project, virtualFile, runManager, rellTestFile))
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)

        val isVisible = project != null &&
                virtualFile != null &&
                rellLanguageServerIsRunning(project) &&
                getRellTestFile(project, virtualFile) != null
        e.presentation.isEnabledAndVisible = isVisible
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    private fun getRellTestFile(project: Project, virtualFile: VirtualFile): RellTestFile? {
        return if (virtualFile.extension == RELL_EXTENSION) {
            project.service<RellProjectService>().getTestFile(virtualFile)
        } else {
            null
        }
    }
}

fun createTestConfiguration(
    project: Project,
    testFile: VirtualFile,
    runManager: RunManager,
    rellTestFile: RellTestFile,
): RunnerAndConfigurationSettings {
    val factory = RellTestConfigurationFactory.getInstance()
    val configurationName = testFile.nameWithoutExtension

    val configurationSettings = runManager.createConfiguration(configurationName, factory)
    val configuration = configurationSettings.configuration as RellTestRunConfiguration

    val options = configuration.options
    options.setTestScope(TestScope.MODULE)
    options.setTestModule(rellTestFile.moduleName)
    options.setWorkingDirectory(project.basePath)

    return configurationSettings
}

/** Registers the configuration, makes it the selected one, and runs it. */
fun runTestConfiguration(runManager: RunManager, settings: RunnerAndConfigurationSettings) {
    runManager.addConfiguration(settings)
    runManager.selectedConfiguration = settings
    ExecutionUtil.runConfiguration(settings, DefaultRunExecutor.getRunExecutorInstance())
}
