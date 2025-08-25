package net.postchain.rellide.jetbrains.testing

import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.execution.testframework.sm.ServiceMessageBuilder
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.openapi.util.Key
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageTypes
import net.postchain.rellide.jetbrains.settings.RellPluginSettingsState
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
        return processHandler
    }

    override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
        val processHandler = startProcess()

        val consoleProperties = RellTestConsoleProperties(configuration, executor)
        val console = SMTestRunnerConnectionUtil.createAndAttachConsole("Rell Test", processHandler, consoleProperties)

        processHandler.addProcessListener(RellTestResultsListener(consoleProperties, processHandler))

        return RellTestExecutionResult(console, processHandler, createActions(console, processHandler, executor))
    }

    private fun createCommandLine(): GeneralCommandLine {
        val options = configuration.options
        val commandLine = GeneralCommandLine()

        // Set the executable
        val executable = options.getChrExecutable()
        val globalSettings = RellPluginSettingsState.instance
        val finalExecutable = when {
            !executable.isNullOrBlank() -> executable
            globalSettings.chromiaCliExecutable.isNotBlank() -> globalSettings.chromiaCliExecutable
            else -> "chr"
        }
        
        if (finalExecutable.isNotBlank()) {
            val (command, parameters) = parseCommand(finalExecutable)
            commandLine.exePath = command
            commandLine.addParameters(parameters)
        } else {
            commandLine.exePath = "chr"
        }

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

        options.getAdditionalArguments()?.let { args ->
            if (args.isNotBlank()) {
                commandLine.addParameters(args.split("\\s+".toRegex()))
            }
        }

        val hideLibWarningsParam = "--hide-lib-warnings"
        if (!commandLine.parametersList.hasParameter(hideLibWarningsParam)) {
            commandLine.addParameter(hideLibWarningsParam)
        }

        return commandLine
    }

    private data class CommandParseResult(
            val command: String,
            val parameters: List<String>
    )

    private fun parseCommand(command: String): CommandParseResult {
        val trimmed = command.trim()
        if (trimmed.isBlank()) {
            return CommandParseResult("", emptyList())
        }

        val parts = trimmed.split(Regex("\\s+"))
        return CommandParseResult(parts.first(), parts.drop(1))
    }
}

/**
 * Console properties for Rell test execution.
 */
private class RellTestConsoleProperties(
    configuration: RellTestRunConfiguration,
    executor: Executor
) : SMTRunnerConsoleProperties(configuration, "Rell Test", executor) {

    init {
        isUsePredefinedMessageFilter = false
        setIfUndefined(HIDE_PASSED_TESTS, false)
        setIfUndefined(HIDE_IGNORED_TEST, false)
        setIfUndefined(SCROLL_TO_SOURCE, true)
        setIfUndefined(SELECT_FIRST_DEFECT, true)
        setIfUndefined(TRACK_RUNNING_TEST, false)
    }

    override fun getTestLocator(): SMTestLocator = RellTestLocator()
    override fun isIdBasedTestTree() = false
}

/**
 * Listener for Rell test results.
 * Handles the output from the test process and sends messages to the console.
 */
class RellTestResultsListener(
    private val consoleProperties: SMTRunnerConsoleProperties,
    private val processHandler: ProcessHandler
) : ProcessListener {
    private var failed = false
    private val failedTestMarkerText = "***** FAILED *****"

    override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
        if (event.text.contains(failedTestMarkerText)) {
            failed = true
        }
    }

    override fun startNotified(event: ProcessEvent) {
        val started = ServiceMessageBuilder.testStarted(consoleProperties.configuration.name).toString()
        sendMessage(started, event.processHandler)
    }

    override fun processWillTerminate(event: ProcessEvent, willBeDestroyed: Boolean) {
        if (failed) {
            sendMessage(ServiceMessageBuilder.testFailed(consoleProperties.configuration.name)
                    .addAttribute(ServiceMessageTypes.MESSAGE, "")
                    .toString(), processHandler)
        } else {
            sendMessage(ServiceMessageBuilder.testFinished(consoleProperties.configuration.name)
                    .toString(), processHandler)
        }
    }

    private fun sendMessage(message: String, processHandler: ProcessHandler) {
        processHandler.notifyTextAvailable("$message\n", ProcessOutputTypes.STDOUT)
    }
}
