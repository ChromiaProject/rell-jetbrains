package net.postchain.rellide.jetbrains.lsp4ij

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider
import net.postchain.rellide.jetbrains.settings.RellPluginSettingsState
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class RellSocketLanguageServer(private val project: Project) : StreamConnectionProvider {
    private var socket: Socket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    // These could come from settings/configuration
    private val host = "localhost"
    private val port = 5008

    override fun start() {
        try {
            socket = Socket(host, port)
            inputStream = socket?.getInputStream()
            outputStream = socket?.getOutputStream()
        } catch (e: IOException) {
            throw RuntimeException("Failed to connect to Rell language server at $host:$port", e)
        }
    }

    override fun getInputStream(): InputStream? = inputStream

    override fun getOutputStream(): OutputStream? = outputStream

    override fun getInitializationOptions(rootUri: VirtualFile?): Any {
        val pluginSettings = RellPluginSettingsState.instance
        val inlayHintsSettings = project.service<RellInlayHintsConfigurationListener>().getInlayHintsSettings()

        return mapOf(
            "indexCaching" to pluginSettings.indexCaching,
            "inlayHints" to inlayHintsSettings
        )
    }

    override fun stop() {
        runCatching {
            inputStream?.close()
        }.runCatching {
            outputStream?.close()
        }.runCatching {
            socket?.close()
        }
    }

    override fun toString(): String = "Rell Language Server (Socket: $host:$port)"
}
