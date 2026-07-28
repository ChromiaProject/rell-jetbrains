package net.postchain.rellide.jetbrains.testing

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

/**
 * Configuration type for Rell test runs.
 * This integrates with IntelliJ's run configuration system to provide
 * a dedicated test runner for Rell test files.
 */
class RellTestConfigurationType : ConfigurationType {
    
    companion object {
        const val ID = "RellTestConfiguration"
        const val DISPLAY_NAME = "Rell Test"
        const val DESCRIPTION = "Run Rell tests"
    }
    
    override fun getDisplayName(): String = DISPLAY_NAME
    
    override fun getConfigurationTypeDescription(): String = DESCRIPTION
    
    override fun getIcon(): Icon = IconLoader.getIcon("/icons/rell.png", javaClass)
    
    override fun getId(): String = ID
    
    override fun getConfigurationFactories(): Array<ConfigurationFactory> {
        return arrayOf(RellTestConfigurationFactory(this))
    }
} 