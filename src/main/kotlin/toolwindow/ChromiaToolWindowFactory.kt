package net.postchain.rellide.jetbrains.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ex.ToolWindowEx
import com.intellij.ui.content.ContentFactory
import net.postchain.rellide.jetbrains.settings.openRellSettings
import net.postchain.rellide.jetbrains.toolwindow.project.ChromiaProjectDiscovery

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
        chromiaToolWindow.subscribeToSettingsChanges(content)

        // Gear menu entry, as the Maven and Gradle tool windows this one mirrors have.
        (toolWindow as? ToolWindowEx)?.setAdditionalGearActions(
            DefaultActionGroup(OpenRellSettingsAction()),
        )
    }

    private class OpenRellSettingsAction :
        AnAction("Rell Settings…", "Open Settings | Tools | Rell", AllIcons.General.Settings), DumbAware {

        override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

        override fun actionPerformed(e: AnActionEvent) {
            openRellSettings(e.project)
        }
    }

    override suspend fun isApplicableAsync(project: Project): Boolean =
        ChromiaProjectDiscovery.discoverProjects(project).isNotEmpty()
}
