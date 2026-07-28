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
import com.redhat.devtools.lsp4ij.ServerStatus
import net.postchain.rellide.jetbrains.lsp4ij.RellTestFile
import net.postchain.rellide.jetbrains.lsp4ij.getRellLanguageServerStatus
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
        val configurationSettings = createTestConfiguration(project, virtualFile, runManager, rellTestFile)
        runManager.addConfiguration(configurationSettings)
        runManager.selectedConfiguration = configurationSettings
        ExecutionUtil.runConfiguration(configurationSettings, DefaultRunExecutor.getRunExecutorInstance())
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)

        val isVisible = project != null &&
                virtualFile != null &&
                getRellLanguageServerStatus(project) == ServerStatus.started &&
                getRellTestFile(project, virtualFile) != null
        e.presentation.isEnabledAndVisible = isVisible
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    private fun getRellTestFile(project: Project, virtualFile: VirtualFile): RellTestFile? {
        return if (virtualFile.extension == "rell") {
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
