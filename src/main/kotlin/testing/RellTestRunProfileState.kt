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
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageTypes
import net.postchain.rellide.jetbrains.settings.RellPluginSettingsState
import java.io.File

/**
 * Profile state for executing Rell tests.
 * Handles the actual process execution and test result parsing.
 */
class RellTestRunProfileState(
    environment: ExecutionEnvironment,
    private val configuration: RellTestRunConfiguration,
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

        // Order of attaching listeners matter!! First the test results listener, then the console
        processHandler.addProcessListener(RellTestResultsListener(consoleProperties, processHandler))
        val console = SMTestRunnerConnectionUtil.createAndAttachConsole("Rell Test", processHandler, consoleProperties)

        return RellTestExecutionResult(console, processHandler, createActions(console, processHandler, executor))
    }

    private fun createCommandLine(): GeneralCommandLine {
        val options = configuration.options

        val testArgs = buildList {
            add("test")
            when (options.getTestScope()) {
                TestScope.MODULE -> options.getTestModule()?.let { add("--modules"); add(it) }
                TestScope.BLOCKCHAIN -> options.getTestBlockchain()?.let { add("--blockchain"); add(it) }
                TestScope.TEST_PATTERN -> options.getTestPattern()?.let { add("--tests"); add(it) }
                TestScope.ALL_IN_PROJECT -> Unit
            }
            options.getAdditionalArguments()?.takeIf { it.isNotBlank() }?.let {
                addAll(it.split(Regex("\\s+")))
            }
            if (none { it == "--hide-lib-warnings" }) add("--hide-lib-warnings")
        }

        val commandLine = resolveExecutable(options.getChrExecutable(), testArgs)

        val workingDir = options.getWorkingDirectory()
        if (!workingDir.isNullOrBlank()) {
            commandLine.workDirectory = File(workingDir)
        } else {
            // TODO: use subproject directory for multi-module projects
            commandLine.workDirectory = File(environment.project.basePath ?: ".")
        }

        return commandLine
    }

    private fun resolveExecutable(perRunExecutable: String?, testArgs: List<String>): GeneralCommandLine =
        RellPluginSettingsState.instance.buildChromiaCliCommandLine(testArgs, perRunExecutable)
            ?: GeneralCommandLine("chr").apply { addParameters(testArgs) }
}

/**
 * Console properties for Rell test execution.
 */
private class RellTestConsoleProperties(
    configuration: RellTestRunConfiguration,
    executor: Executor,
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
    private val processHandler: ProcessHandler,
) : ProcessListener {

    override fun startNotified(event: ProcessEvent) {
        val started = ServiceMessageBuilder.testStarted(consoleProperties.configuration.name).toString()
        sendMessage(started, event.processHandler)
    }

    override fun processTerminated(event: ProcessEvent) {
        if (event.exitCode != 0) {
            sendMessage(
                ServiceMessageBuilder.testFailed(consoleProperties.configuration.name)
                    .addAttribute(ServiceMessageTypes.MESSAGE, "")
                    .toString(), event.processHandler
            )
        } else {
            sendMessage(
                ServiceMessageBuilder.testFinished(consoleProperties.configuration.name)
                    .toString(), event.processHandler
            )
        }
    }

    private fun sendMessage(message: String, processHandler: ProcessHandler) {
        processHandler.notifyTextAvailable("$message\n", ProcessOutputTypes.STDOUT)
    }
}
