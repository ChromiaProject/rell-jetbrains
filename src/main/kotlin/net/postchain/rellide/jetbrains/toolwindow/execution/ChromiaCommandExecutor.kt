package net.postchain.rellide.jetbrains.toolwindow.execution

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.terminal.ui.TerminalWidget
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
            val widget = getTerminalWidget(command, workingDirectory)
            widget.apply {
                requestFocus()
                sendCommandToExecute("cd ${workingDirectory ?: project.basePath}")
                sendCommandToExecute(command)
            }

        } catch (e: IOException) {
            logger.error("Cannot run command in terminal. Error:$e")
        }
    }

    private fun getTerminalWidget(command: String, workingDirectory: String?): TerminalWidget {
        val manager = TerminalToolWindowManager.getInstance(project)
        val projectPath = workingDirectory ?: project.basePath

        return if (requiresNewTerminal(command)) {
            manager.createShellWidget(projectPath, "${TAB_NAME}: $command" , true, false)
        } else {
            val existing = manager.terminalWidgets.firstOrNull { it.terminalTitle.defaultTitle == TAB_NAME }
            // focus on existing terminal if it matches the name
            existing?.let {
                val existingContent = manager.toolWindow.contentManager.findContent(TAB_NAME)
                manager.toolWindow.contentManager.setSelectedContent(existingContent, true)
            }
            val widget = existing ?: manager.createShellWidget(projectPath, TAB_NAME, true, false)
            widget
        }
    }

    private fun requiresNewTerminal(command: String): Boolean {
        return command.startsWith("chr repl") || command.startsWith("chr node start")
    }
}