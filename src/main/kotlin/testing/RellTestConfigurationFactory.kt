package net.postchain.rellide.jetbrains.testing

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project

/**
 * Factory for creating Rell test run configurations.
 */
class RellTestConfigurationFactory(type: RellTestConfigurationType) : ConfigurationFactory(type) {

    override fun getId(): String = "RellTestConfigurationFactory"

    override fun createTemplateConfiguration(project: Project): RunConfiguration =
        RellTestRunConfiguration(project, this, "Rell Test")

    override fun getOptionsClass(): Class<out BaseState> = RellTestRunConfigurationOptions::class.java

    companion object {
        fun getInstance(): RellTestConfigurationFactory {
            return ConfigurationTypeUtil.findConfigurationType(RellTestConfigurationType::class.java)
                .configurationFactories[0] as RellTestConfigurationFactory
        }
    }
} 