package net.postchain.rellide.jetbrains.testing

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.filters.Filter
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.testframework.TestConsoleProperties
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.execution.testframework.sm.runner.TestProxyFilterProvider
import com.intellij.execution.testframework.sm.runner.ui.SMTestRunnerResultsForm
import com.intellij.execution.testframework.sm.runner.GeneralTestEventsProcessor
import com.intellij.execution.testframework.sm.runner.events.*
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView
import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindowManager
import net.postchain.rellide.jetbrains.testing.ui.RellTestRunnerToolWindow
import net.postchain.rellide.jetbrains.testing.ui.TestState
import java.io.File
import com.intellij.util.messages.MessageBus
import kotlin.text.Regex
import com.intellij.openapi.util.Key

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
        //val processHandler = KillableColoredProcessHandler(commandLine)
        val processHandler = ProcessHandlerFactory.getInstance()
                .createColoredProcessHandler(commandLine);
        ProcessTerminatedListener.attach(processHandler)
       // processHandler.addProcessListener(RellTestProcessListener(environment.project))

        return processHandler
    }

    override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
        val processHandler = startProcess()

        // Create test console with test framework integration
        val consoleProperties = RellTestConsoleProperties(configuration, executor)
        val console = SMTestRunnerConnectionUtil.createAndAttachConsole("Rell Test", processHandler, consoleProperties)

        if (console is SMTRunnerConsoleView) {
            processHandler.addProcessListener(RellTestResultsListener(console, consoleProperties))
        }


        return RellTestExecutionResult(console, processHandler, createActions(console, processHandler, executor))
    }

//    override fun createConsole(executor: Executor): ConsoleView {
//        val properties = RellTestConsoleProperties(configuration, executor)
//        return SMTestRunnerConnectionUtil.createConsole("Rell Test", properties)
//    }

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
        setIfUndefined(SELECT_FIRST_DEFECT, false)
        setIfUndefined(TRACK_RUNNING_TEST, false)
    }

    override fun getTestLocator(): SMTestLocator = RellTestLocator()
    override fun isIdBasedTestTree() = false
    override fun isPreservePresentableName(): Boolean {
        return true
    }



}

class RellFilterProvider : TestProxyFilterProvider {
    override fun getFilter(nodeType: String, nodeName: String, nodeArguments: String?): Filter? {
        TODO("Not yet implemented")
    }

}


class RellTestResultsListener(
    private val console: SMTRunnerConsoleView,
    private val consoleProperties: SMTRunnerConsoleProperties
) : ProcessListener {
    
    override fun processTerminated(event: ProcessEvent) {
        if (event.exitCode == 0) {
            // Look for JUnit XML report files in the working directory
            ApplicationManager.getApplication().executeOnPooledThread {
                findAndParseTestReports()
            }
        }
    }
    
    private fun findAndParseTestReports() {
        val config = consoleProperties.configuration as? RellTestRunConfiguration
        val workingDir = config?.options?.getWorkingDirectory()
        val reportDir = File(workingDir ?: ".", "build/reports/")
        
        if (!reportDir.exists()) return
        
        // Look for XML files in test results directory
        val xmlFiles = reportDir.walkTopDown()
            .filter { it.isFile && it.extension == "xml" }
            .toList()
        
        for (xmlFile in xmlFiles) {
            try {
                parseAndReportResults(xmlFile)
            } catch (e: Exception) {
                // Log error but continue with other files
                println("Error parsing test report ${xmlFile.name}: ${e.message}")
            }
        }
    }
    
    private fun parseAndReportResults(xmlFile: File) {
        val parser = JunitXmlParser()
        val testReports = parser.parseTestReport(xmlFile)
        
        ApplicationManager.getApplication().invokeLater {
            reportTestResults(console.resultsViewer, testReports)
        }
    }
    
    private fun reportTestResults(resultsForm: SMTestRunnerResultsForm, testReports: JunitTestReports) {
        try {
//            val processorField = resultsForm.javaClass.getDeclaredField("myEventsProcessor")
//            processorField.isAccessible = true
//            val processor = processorField.get(resultsForm) as? GeneralTestEventsProcessor

//                proc.onTestingStarted(TreeNodeEvent(null, null))
//
//                for (testSuite in testReports.testSuites) {
//                    proc.onSuiteStarted(TestSuiteStartedEvent(testSuite.name, null))
//
//                    for (testCase in testSuite.testCases) {
//                        val testId = "${testCase.classname}.${testCase.name}"
//                        proc.onTestStarted(TestStartedEvent(testId, testCase.name))
//
//                        when (val result = testCase.result) {
//                            is JunitTestResult.Success -> {
//                                proc.onTestFinished(TestFinishedEvent(testId, testCase.time.toMillis()))
//                            }
//                            is JunitTestResult.Failure -> {
//                                proc.onTestFailure(TestFailedEvent(
//                                    testCase.name,
//                                    result.message ?: "Test failed",
//                                    result.content ?: "",
//                                    false,
//                                    null,
//                                    null
//                                ))
//                                proc.onTestFinished(TestFinishedEvent(testId, testCase.time.toMillis()))
//                            }
//                            is JunitTestResult.Error -> {
//                                proc.onTestFailure(TestFailedEvent(
//                                    testCase.name,
//                                    result.message ?: "Test error",
//                                    result.content ?: "",
//                                    true,
//                                    null,
//                                    null
//                                ))
//                                proc.onTestFinished(TestFinishedEvent(testId, testCase.time.toMillis()))
//                            }
//                            is JunitTestResult.Skipped -> {
//                                proc.onTestIgnored(TestIgnoredEvent(testId, result.message ?: "Test skipped", ""))
//                                proc.onTestFinished(TestFinishedEvent(testId, testCase.time.toMillis()))
//                            }
//                        }
//                    }
//
//                    proc.onSuiteFinished(TestSuiteFinishedEvent(testSuite.name))
//                }
//
//                proc.onTestingFinished(TreeNodeEvent(null, null))

        } catch (e: Exception) {
            println("Error reporting test results: ${e.message}")
        }
    }
}
