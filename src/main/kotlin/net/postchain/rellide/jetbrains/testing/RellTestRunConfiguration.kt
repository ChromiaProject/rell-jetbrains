package net.postchain.rellide.jetbrains.testing

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.WriteExternalException
import org.jdom.Element

/**
 * Run configuration for Rell tests.
 * This class handles the execution of Rell test files and integrates with
 * IntelliJ's run configuration system.
 */
class RellTestRunConfiguration(
    project: Project,
    factory: ConfigurationFactory,
    name: String
) : RunConfigurationBase<RellTestRunConfigurationOptions>(project, factory, name) {

    public override fun getOptions(): RellTestRunConfigurationOptions {
        return super.getOptions() as RellTestRunConfigurationOptions
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return RellTestRunConfigurationEditor(project)
    }

    override fun checkConfiguration() {
        super.checkConfiguration()
        
        val options = options
        when (options.getTestScope()) {
            TestScope.MODULE -> {
                if (options.getTestModule().isNullOrBlank()) {
                    throw RuntimeConfigurationError("Test file path is required")
                }
            }
            TestScope.BLOCKCHAIN -> {
                if (options.getTestBlockchain().isNullOrBlank()) {
                    throw RuntimeConfigurationError("Test directory path is required")
                }
            }
            TestScope.TEST_PATTERN -> {
                if (options.getTestPattern().isNullOrBlank()) {
                    throw RuntimeConfigurationError("Test pattern is required")
                }
            }
            TestScope.ALL_IN_PROJECT -> {
                // No specific validation needed for this scope
            }
        }
        
//        if (options.getChrExecutable().isNullOrBlank()) {
//            throw RuntimeConfigurationError("Chromia CLI executable path is required")
//        }
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return RellTestRunProfileState(environment, this)
    }

//    @Throws(InvalidDataException::class)
//    override fun readExternal(element: Element) {
//        super.readExternal(element)
//    }
//
//    @Throws(WriteExternalException::class)
//    override fun writeExternal(element: Element) {
//        super.writeExternal(element)
//    }

//    /**
//     * Get a human readable description of the test configuration
//     */
//    fun getTestDescription(): String {
//        return when (options.getTestScope()) {
//            TestScope.MODULE -> "Module: ${options.getTestModule()}"
//            TestScope.BLOCKCHAIN -> "Blockchain: ${options.getTestBlockchain()}"
//            TestScope.TEST_PATTERN -> "Test pattern: ${options.getTestPattern()}"
//            TestScope.ALL_IN_PROJECT -> "All tests in project"
//        }
//    }
} 