package net.postchain.rellide.jetbrains.testing

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project

/**
 * Run configuration for Rell tests.
 * This class handles the execution of Rell test files and integrates with
 * Jetbrains's run configuration system.
 */
class RellTestRunConfiguration(
    project: Project,
    factory: ConfigurationFactory,
    name: String,
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
                    throw RuntimeConfigurationError("Test module name is required")
                }
            }

            TestScope.BLOCKCHAIN -> {
                if (options.getTestBlockchain().isNullOrBlank()) {
                    throw RuntimeConfigurationError("Test blockchain name is required")
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
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return RellTestRunProfileState(environment, this)
    }
}
