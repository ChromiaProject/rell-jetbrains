package net.postchain.rellide.jetbrains.testing

import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.testframework.TestConsoleProperties
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import java.io.File

/**
 * Profile state for executing Rell tests.
 * Handles the actual process execution and test result parsing.
 */
class RellTestRunProfileState(
    environment: ExecutionEnvironment,
    private val configuration: RellTestRunConfiguration
) : CommandLineState(environment) {

    override fun startProcess(): ProcessHandler {
        val commandLine = createCommandLine()
        val processHandler = KillableColoredProcessHandler(commandLine)
        ProcessTerminatedListener.attach(processHandler)
        processHandler.addProcessListener(RellTestProcessListener())

        return processHandler
    }

    override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
        val processHandler = startProcess()
        
        // Create test console with test framework integration
        val consoleProperties = RellTestConsoleProperties(configuration, executor)
        val console = SMTestRunnerConnectionUtil.createAndAttachConsole("Rell Test", processHandler, consoleProperties)
        
        return RellTestExecutionResult(console, processHandler, createActions(console, processHandler, executor))
    }

    private fun createCommandLine(): GeneralCommandLine {
        val options = configuration.options
        val commandLine = GeneralCommandLine()

        // Set the executable
        val executable = options.getChrExecutable()
        commandLine.exePath = if (executable.isNullOrBlank()) "chr" else executable

        val workingDir = options.getWorkingDirectory()
        if (!workingDir.isNullOrBlank()) {
            commandLine.setWorkDirectory(File(workingDir))
        } else {
            // TODO: use subproject directory for multi-module projects
            commandLine.setWorkDirectory(File(environment.project.basePath ?: "."))
        }

        // Add test command and parameters based on scope
        commandLine.addParameter("test")
        when (options.getTestScope()) {
            TestScope.MODULE -> {
                options.getTestModule()?.let {
                    commandLine.addParameter("--modules")
                    commandLine.addParameter(it)
                }
            }
            TestScope.BLOCKCHAIN -> {
                options.getTestBlockchain()?.let {
                    commandLine.addParameter("--blockchain")
                    commandLine.addParameter(it)
                }
            }
            TestScope.TEST_PATTERN -> {
                options.getTestPattern()?.let {
                    commandLine.addParameter("--tests")
                    commandLine.addParameter(it)
                }
            }
            TestScope.ALL_IN_PROJECT -> {
                // No additional parameters needed for this scope
            }
        }

        // Generate XML test report
        commandLine.addParameter("--test-report")

        // Add additional arguments
        options.getAdditionalArguments()?.let { args ->
            if (args.isNotBlank()) {
                commandLine.addParameters(args.split("\\s+".toRegex()))
            }
        }

        return commandLine
    }
}

/**
 * Console properties for Rell test execution.
 */
private class RellTestConsoleProperties(
    private val configuration: RellTestRunConfiguration,
    executor: Executor
) : SMTRunnerConsoleProperties(configuration, "Rell Test", executor) {

    init {
        isUsePredefinedMessageFilter = false
        setIfUndefined(HIDE_PASSED_TESTS, false)
        setIfUndefined(HIDE_IGNORED_TEST, false)
        setIfUndefined(SCROLL_TO_SOURCE, true)
        setIfUndefined(SELECT_FIRST_DEFECT, true)
        setIfUndefined(TRACK_RUNNING_TEST, true)
    }

    override fun getTestLocator() = RellTestLocator()
}


class RellTestProcessListener : ProcessListener {
    override fun processTerminated(event: ProcessEvent) {
        println(">>Process terminated with exit code: ${event.exitCode}")
    }
}