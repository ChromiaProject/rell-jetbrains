package net.postchain.rellide.jetbrains.toolWindow

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import net.postchain.rellide.jetbrains.RellBundle
import net.postchain.rellide.jetbrains.services.RellProjectService
import javax.swing.JButton


class RellToolWindowFactory : ToolWindowFactory {

    init {
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    private val contentFactory = ContentFactory.SERVICE.getInstance()

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val rellToolWindow = RellToolWindow(toolWindow)
        val content = contentFactory.createContent(rellToolWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class RellToolWindow(toolWindow: ToolWindow) {

        private val service = toolWindow.project.service<RellProjectService>()

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            val label = JBLabel(RellBundle.message("randomLabel", "?"))

            add(label)
            add(JButton(RellBundle.message("shuffle")).apply {
                addActionListener {
                    label.text = RellBundle.message("randomLabel", service.getRandomNumber())
                }
            })
        }
    }
}
