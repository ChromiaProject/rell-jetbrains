package net.postchain.rellide.jetbrains.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil


/**
 * Supports storing the plugin settings in a persistent way.
 * The [State] and [Storage] annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(
    name = "net.postchain.rellide.jetbrains.settings.RellPluginSettingsState",
    storages = [Storage("RellPluginSettings.xml")]
)
class RellPluginSettingsState : PersistentStateComponent<RellPluginSettingsState> {
    var indexCaching: Boolean = true
    var chromiaCliExecutable: String = ""

    override fun getState(): RellPluginSettingsState {
        return this
    }

    override fun loadState(state: RellPluginSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        val instance: RellPluginSettingsState
            get() = ApplicationManager.getApplication().getService(RellPluginSettingsState::class.java)
    }
}
