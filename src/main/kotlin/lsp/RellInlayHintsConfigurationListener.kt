package net.postchain.rellide.jetbrains.lsp

import com.intellij.codeInsight.hints.InlayHintsSettings
import com.intellij.codeInsight.hints.InlayHintsSettings.SettingsListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.util.messages.MessageBusConnection
import org.eclipse.lsp4j.DidChangeConfigurationParams

@Service(Service.Level.PROJECT)
@Suppress("UnstableApiUsage")
class RellInlayHintsConfigurationListener : Disposable {
    private var connection: MessageBusConnection? = null

    fun startListening() {
        if (connection != null) return

        connection = ApplicationManager.getApplication().messageBus.connect()
        connection?.subscribe(
            InlayHintsSettings.INLAY_SETTINGS_CHANGED,
            handler = object : SettingsListener {
                override fun settingsChanged() {
                    onInlayHintsSettingsChanged()
                }
            }
        )
    }

    override fun dispose() {
        connection?.dispose()
        connection?.disconnect()
    }

    private fun onInlayHintsSettingsChanged() {
        for (project in ProjectManager.getInstance().openProjects) {
            sendConfigurationToLsp(project)
        }
    }

    private fun sendConfigurationToLsp(project: Project) {
        val inlayHintsEnabled = isRellInlayHintsEnabled()

        val configurationSettings = mapOf(
            "inlayHints" to mapOf(
                "parameterHints" to inlayHintsEnabled,
                "variableTypeHints" to inlayHintsEnabled,
                "returnTypeHints" to inlayHintsEnabled
            )
        )

        try {
            val params = DidChangeConfigurationParams(configurationSettings)

            for (client in runningRellLspClients(project)) {
                client.sendNotification { it.workspaceService.didChangeConfiguration(params) }
            }
        } catch (e: Exception) {
            logger.warn("Failed to send hints configuration to lsp server: ${e.message}")
        }
    }

    fun getInlayHintsSettings(): Map<String, Boolean> = try {
        val isEnabled = isRellInlayHintsEnabled()

        mapOf(
            "parameterHints" to isEnabled,
            "variableTypeHints" to isEnabled,
            "returnTypeHints" to isEnabled
        )
    } catch (e: Exception) {
        logger.warn("Error getting inlay hints settings: ${e.message}")
        mapOf()
    }

    private fun isRellInlayHintsEnabled() = runCatching {
        val hintsSettings = InlayHintsSettings.instance()
        hintsSettings.state.disabledHintProviderIds.none { it == "Rell.LSP.hints" }
    }.onFailure {
        logger.warn("Error checking Rell->hints settings: ${it.message}")
    }.getOrDefault(false)

    companion object {
        val logger = Logger.getInstance(RellInlayHintsConfigurationListener::class.java)
    }
}

class RellInlayHintsStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        project.service<RellInlayHintsConfigurationListener>().startListening()
    }
}
