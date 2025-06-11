package net.postchain.rellide.jetbrains.toolwindow.execution

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
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
            val manager = TerminalToolWindowManager.getInstance(project)

            val existing = manager.terminalWidgets.firstOrNull { it.terminalTitle.defaultTitle == TAB_NAME }
            val widget = existing ?: manager.createShellWidget(workingDirectory ?: project.basePath, TAB_NAME, true, false)

            widget.apply {
                requestFocus()
                sendCommandToExecute("cd ${workingDirectory ?: project.basePath}")
                sendCommandToExecute(command)
            }

        } catch (e: IOException) {
            logger.error("Cannot run command in terminal. Error:$e")
        }
    }
}