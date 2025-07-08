package net.postchain.rellide.jetbrains.lsp4ij

import com.intellij.codeInsight.hints.InlayHintsSettings
import com.intellij.codeInsight.hints.InlayHintsSettings.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.util.messages.MessageBusConnection
import com.redhat.devtools.lsp4ij.LanguageServerManager
import net.postchain.rellide.jetbrains.services.RellProjectService.Companion.RELL_LANGUAGE_SERVER_ID
import net.postchain.rellide.jetbrains.settings.RellPluginSettingsState
import org.eclipse.lsp4j.DidChangeConfigurationParams

@Service(Service.Level.APP)
@Suppress("UnstableApiUsage")
class RellInlayHintsConfigurationListener {
    
    private var connection: MessageBusConnection? = null

    fun startListening() {
        if (connection != null) return

        connection = ApplicationManager.getApplication().messageBus.connect()
        connection?.subscribe(
                InlayHintsSettings.INLAY_SETTINGS_CHANGED,
                handler = object: SettingsListener {
                    override fun settingsChanged() {
                        onInlayHintsSettingsChanged()
                    }
                }
        )
    }

    private fun onInlayHintsSettingsChanged() {
        ProjectManager.getInstance().openProjects.forEach { project ->
            sendConfigurationToLsp(project)
        }
    }
    
    private fun sendConfigurationToLsp(project: Project) {
        val inlayHintsEnabled = isRellInlayHintsEnabled()
        val pluginSettings = RellPluginSettingsState.instance
        
        val configurationSettings = mapOf(
            "indexCaching" to pluginSettings.indexCaching,
            "inlayHints" to mapOf(
                "parameterHints" to inlayHintsEnabled,
                "variableTypeHints" to inlayHintsEnabled,
                "returnTypeHints" to inlayHintsEnabled
            )
        )
        
        try {
            LanguageServerManager.getInstance(project)
                .getLanguageServer(RELL_LANGUAGE_SERVER_ID)
                .get()
                ?.let { server ->
                    val params = DidChangeConfigurationParams(configurationSettings)
                    server.workspaceService.didChangeConfiguration(params)
                }
        } catch (e: Exception) {
            println("Failed to send hints configuration to lsp server: ${e.message}")
        }
    }

    companion object {
        fun getInstance(): RellInlayHintsConfigurationListener {
            return ApplicationManager.getApplication().getService(RellInlayHintsConfigurationListener::class.java)
        }

        private fun isRellInlayHintsEnabled() = runCatching {
            val hintsSettings = InlayHintsSettings.instance()
            hintsSettings.state.disabledHintProviderIds.none { it == "Rell.LSP.hints" }
        }.onFailure {
            println("Error checking Rell->hints settings: ${it.message}")
        }.getOrDefault(false)

        fun getInlayHintsSettings(): Map<String, Boolean> {
            return try {
                val isEnabled = isRellInlayHintsEnabled()
                mapOf(
                        "parameterHints" to isEnabled,
                        "variableTypeHints" to isEnabled,
                        "returnTypeHints" to isEnabled
                )
            } catch (e: Exception) {
                println("Error getting inlay hints settings: ${e.message}")
                mapOf()
            }
        }

    }
}

class RellInlayHintsStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        RellInlayHintsConfigurationListener.getInstance().startListening()
    }
} 