package net.postchain.rellide.jetbrains.testing

import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.openapi.components.StoredProperty

/**
 * Options for Rell test run configuration.
 * Stores the configuration parameters for test execution.
 */
class RellTestRunConfigurationOptions : RunConfigurationOptions() {
    
    private val testModule: StoredProperty<String?> = string("").provideDelegate(this, "testModule")
    private val testBlockchain: StoredProperty<String?> = string("").provideDelegate(this, "testBlockchain")
    private val testPattern: StoredProperty<String?> = string("").provideDelegate(this, "testPattern")
    private val testScope: StoredProperty<TestScope> = enum(TestScope.MODULE).provideDelegate(this, "testScope")
    private val workingDirectory: StoredProperty<String?> = string("").provideDelegate(this, "workingDirectory")
    private val chrExecutable: StoredProperty<String?> = string("").provideDelegate(this, "chrExecutable")
    private val additionalArguments: StoredProperty<String?> = string("").provideDelegate(this, "additionalArguments")
    
    fun getTestModule(): String? = testModule.getValue(this)
    fun setTestModule(value: String?) = testModule.setValue(this, value)
    
    fun getTestBlockchain(): String? = testBlockchain.getValue(this)
    fun setTestBlockchain(value: String?) = testBlockchain.setValue(this, value)
    
    fun getTestPattern(): String? = testPattern.getValue(this)
    fun setTestPattern(value: String?) = testPattern.setValue(this, value)
    
    fun getTestScope(): TestScope = testScope.getValue(this)
    fun setTestScope(value: TestScope) = testScope.setValue(this, value)
    
    fun getWorkingDirectory(): String? = workingDirectory.getValue(this)
    fun setWorkingDirectory(value: String?) = workingDirectory.setValue(this, value)
    
    fun getChrExecutable(): String? = chrExecutable.getValue(this)
    fun setChrExecutable(value: String?) = chrExecutable.setValue(this, value)
    
    fun getAdditionalArguments(): String? = additionalArguments.getValue(this)
    fun setAdditionalArguments(value: String?) = additionalArguments.setValue(this, value)
}

enum class TestScope {
    MODULE,
    BLOCKCHAIN,
    TEST_PATTERN,
    ALL_IN_PROJECT
}
