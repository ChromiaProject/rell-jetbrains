package net.postchain.rellide.jetbrains.testing.ui

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

/**
 * Factory for creating the Rell test runner tool window.
 */
class RellTestRunnerToolWindowFactory : ToolWindowFactory, DumbAware {
    
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val testRunnerWindow = RellTestRunnerToolWindow(project)
        val content = ContentFactory.getInstance().createContent(
            testRunnerWindow,
            "",
            false
        )
        toolWindow.contentManager.addContent(content)
    }
    
    override fun isApplicable(project: Project): Boolean {
        // Check if this is a project that contains Rell files
        return hasRellFiles(project)
    }
    
    private fun hasRellFiles(project: Project): Boolean {
        val projectBasePath = project.basePath ?: return false
        val baseDir = com.intellij.openapi.vfs.LocalFileSystem.getInstance().findFileByPath(projectBasePath)
        
        return baseDir?.let { dir ->
            hasRellFilesRecursively(dir)
        } ?: false
    }
    
    private fun hasRellFilesRecursively(dir: com.intellij.openapi.vfs.VirtualFile): Boolean {
        for (child in dir.children) {
            if (child.isDirectory) {
                if (hasRellFilesRecursively(child)) {
                    return true
                }
            } else if (child.extension == "rell") {
                return true
            }
        }
        return false
    }
} 