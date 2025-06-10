package net.postchain.rellide.jetbrains.toolwindow.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * Persistent settings for the Chromia tool window.
 * Stores CLI command parameters per project.
 */
@State(
        name = "ChromiaToolWindowSettings",
        storages = [Storage(StoragePathMacros.WORKSPACE_FILE)]
)
@Service(Service.Level.PROJECT)
class ChromiaToolWindowSettings : PersistentStateComponent<ChromiaToolWindowSettings> {

    /**
     * Map of command -> parameters
     * e.g., "chr build" -> "--verbose --output target/"
     */
    var commandParameters: MutableMap<String, String> = mutableMapOf()

    override fun getState(): ChromiaToolWindowSettings = this

    override fun loadState(state: ChromiaToolWindowSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    fun getParameters(command: String): String {
        return commandParameters[command] ?: ""
    }

    fun setParameters(command: String, parameters: String) {
        commandParameters[command] = parameters
    }

    fun clearParameters(command: String) {
        commandParameters.remove(command)
    }

    fun clearAllParameters() {
        commandParameters.clear()
    }

    companion object {
        fun getInstance(project: Project): ChromiaToolWindowSettings {
            return project.service<ChromiaToolWindowSettings>()
        }
    }
}