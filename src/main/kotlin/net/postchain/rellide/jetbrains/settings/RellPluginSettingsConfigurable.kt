package net.postchain.rellide.jetbrains.settings

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 * Provides controller functionality for Rell plugin settings.
 */
class RellPluginSettingsConfigurable : Configurable {
    private var settingsComponent: RellPluginSettingsComponent? = null

    // A default constructor with no arguments is required because this implementation
    // is registered in an applicationConfigurable EP
    override fun getDisplayName(): @Nls(capitalization = Nls.Capitalization.Title) String = "Rell:"

    override fun getPreferredFocusedComponent(): JComponent? {
        return settingsComponent?.getPreferredFocusedComponent()
    }

    override fun createComponent(): JComponent? {
        settingsComponent = RellPluginSettingsComponent()
        return settingsComponent?.getPanel()
    }

    override fun isModified(): Boolean {
        val settings = RellPluginSettingsState.instance
        return settings.indexCaching != settingsComponent?.indexCachingState
    }

    override fun apply() {
        val settings = RellPluginSettingsState.instance
        settings.indexCaching = settingsComponent?.indexCachingState ?: false
    }

    override fun reset() {
        val settings = RellPluginSettingsState.instance
        settingsComponent?.indexCachingState = settings.indexCaching
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}