package net.postchain.rellide.jetbrains.toolwindow.execution

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.diagnostic.Logger
import org.jetbrains.plugins.terminal.TerminalToolWindowFactory
import org.jetbrains.plugins.terminal.TerminalToolWindowManager
import java.io.IOException

/**
 * Executes Chromia CLI commands in the terminal.
 * Handles running chr commands with parameters in the project's context.
 */
class ChromiaCommandExecutor(private val project: Project) {

    companion object {
        val logger = Logger.getInstance(ChromiaCommandExecutor::class.java)
        private const val TAB_NAME = "Chromia"
    }

    fun executeCommand(command: String, workingDirectory: String? = null) {
        ApplicationManager.getApplication().invokeLater {
            try {
                executeInTerminal(command, workingDirectory)
            } catch (e: Exception) {
                logger.error(e)
            }
        }
    }

    private fun executeInTerminal(command: String, workingDirectory: String? = null) {
        try {
            val terminalView = TerminalToolWindowManager.getInstance(project)
            val window = ToolWindowManager.getInstance(project).getToolWindow(TerminalToolWindowFactory.TOOL_WINDOW_ID)
            val contentManager = window?.contentManager

            val widget = when (val content = contentManager?.findContent(TAB_NAME)) {
                null -> terminalView.createShellWidget(workingDirectory
                        ?: project.basePath, TAB_NAME, true, true)
                else -> TerminalToolWindowManager.findWidgetByContent(content)
            }
            widget?.sendCommandToExecute(command)

        } catch (e: IOException) {
            logger.error("Cannot run command in local terminal. Error:$e")
        }
    }
}