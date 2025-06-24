package net.postchain.rellide.jetbrains.testing.actions

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import net.postchain.rellide.jetbrains.testing.*

/**
 * Action to run Rell tests from the editor or project view.
 */
class RunRellTestAction : AnAction("Run Rell Test", "Run the selected Rell test file", null) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        
        if (!isRellTestFile(virtualFile)) {
            return
        }
        
        val runManager = RunManager.getInstance(project)
        val configurationSettings = createTestConfiguration(project, virtualFile, runManager)
        
        runManager.selectedConfiguration = configurationSettings
        ExecutionUtil.runConfiguration(configurationSettings, DefaultRunExecutor.getRunExecutorInstance())
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        
        val isVisible = project != null && virtualFile != null && isRellTestFile(virtualFile)
        e.presentation.isEnabledAndVisible = isVisible
    }

    private fun isRellTestFile(virtualFile: VirtualFile): Boolean {
        if (virtualFile.extension != "rell") {
            return false
        }
        
        // Additional logic to check if file contains test functions
        // This could be enhanced to parse the file content and look for test functions
        val fileName = virtualFile.nameWithoutExtension.lowercase()
        return fileName.contains("test") || fileName.endsWith("_test") || fileName.startsWith("test_")
    }

    private fun createTestConfiguration(
        project: Project,
        testFile: VirtualFile,
        runManager: RunManager
    ): RunnerAndConfigurationSettings {
        val factory = RellTestConfigurationFactory.getInstance()
        val configurationName = testFile.nameWithoutExtension
        
        val configurationSettings = runManager.createConfiguration(configurationName, factory)
        val configuration = configurationSettings.configuration as RellTestRunConfiguration
        
        val options = configuration.options
        options.setTestScope(TestScope.MODULE)
        options.setTestModule(testFile.path)
        options.setWorkingDirectory(project.basePath)
        
        // Try to auto-detect Chromia CLI
        val chrExecutable = findChrExecutable(project)
        if (chrExecutable != null) {
            options.setChrExecutable(chrExecutable)
        }
        
        return configurationSettings
    }

    private fun findChrExecutable(project: Project): String? {
        // Try to find Chromia CLI executable in common locations
        val commonPaths = listOf("chr")
        
        for (path in commonPaths) {
            val process = try {
                ProcessBuilder(path, "--version").start()
            } catch (e: Exception) {
                continue
            }
            
            try {
                val exitCode = process.waitFor()
                if (exitCode == 0) {
                    return path
                }
            } catch (e: Exception) {
                // Continue searching
            }
        }
        
        return null
    }
} 