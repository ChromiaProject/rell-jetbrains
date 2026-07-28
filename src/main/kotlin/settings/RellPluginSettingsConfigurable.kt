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
        val component = settingsComponent ?: return false
        return settings.indexCaching != component.indexCachingState ||
                settings.chromiaCliCommand != component.chromiaCliCommandState
    }

    override fun apply() {
        val settings = RellPluginSettingsState.instance
        val component = settingsComponent ?: return
        settings.indexCaching = component.indexCachingState
        settings.chromiaCliCommand = component.chromiaCliCommandState
    }

    override fun reset() {
        val settings = RellPluginSettingsState.instance
        val component = settingsComponent ?: return
        component.indexCachingState = settings.indexCaching
        component.chromiaCliCommandState = settings.chromiaCliCommand
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}
