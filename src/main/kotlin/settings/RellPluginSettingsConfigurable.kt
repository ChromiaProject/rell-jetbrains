package net.postchain.rellide.jetbrains.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import net.postchain.rellide.jetbrains.chromia.RellVersionResolver
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 * Opens Settings | Tools | Rell. Goes through the configurable class rather than the
 * `jetbrains://idea/settings?name=Tools--Rell` URL, which depends on the page's display name and
 * on the OS URL handler.
 */
fun openRellSettings(project: Project?) {
    ShowSettingsUtil.getInstance().showSettingsDialog(project, RellPluginSettingsConfigurable::class.java)
}

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
        val cliCommandChanged = settings.chromiaCliCommand != component.chromiaCliCommandState
        settings.chromiaCliCommand = component.chromiaCliCommandState
        if (cliCommandChanged) {
            // A different CLI may support a different Rell version; the Chromia tool window
            // warning derives from the tested command matching the effective one.
            ProjectManager.getInstance().openProjects.forEach { project ->
                project.messageBus.syncPublisher(RellVersionResolver.TOPIC).chromiaConfigChanged()
            }
        }
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
