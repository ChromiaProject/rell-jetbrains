package net.postchain.rellide.jetbrains.testing

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.actionSystem.AnAction

/**
 * Execution result for Rell test runs.
 * Wraps the console view and process handler for test execution.
 */
class RellTestExecutionResult(
    console: ConsoleView,
    processHandler: ProcessHandler,
    actions: Array<AnAction>
) : DefaultExecutionResult(console, processHandler, *actions), ExecutionResult {
    
    init {
        // Add any custom initialization if needed
    }
} 