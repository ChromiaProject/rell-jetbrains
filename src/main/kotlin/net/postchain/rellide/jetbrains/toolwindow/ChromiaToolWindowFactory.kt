package net.postchain.rellide.jetbrains.toolwindow

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

/**
 * Factory for creating the Chromia tool window.
 * Similar to Maven and Gradle tool windows, provides a sidebar with Chromia specific commands.
 */
class ChromiaToolWindowFactory : ToolWindowFactory, DumbAware {
    
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val chromiaToolWindow = ChromiaToolWindow(project)
        val content = ContentFactory.getInstance().createContent(
                chromiaToolWindow.getContent(),
                "",
                false
        )
        toolWindow.contentManager.addContent(content)
    }
}
