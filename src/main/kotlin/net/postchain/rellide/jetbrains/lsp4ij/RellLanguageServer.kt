package net.postchain.rellide.jetbrains.lsp4ij

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.VirtualFile
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider
import net.postchain.rellide.jetbrains.lsp4ij.RellInlayHintsConfigurationListener.Companion.getInlayHintsSettings
import net.postchain.rellide.jetbrains.settings.RellPluginSettingsState
import java.io.File
import kotlin.Any
import kotlin.IllegalStateException
import kotlin.String

class RellLanguageServer(val project: Project) : OSProcessStreamConnectionProvider() {
    private val extraOptions = listOf(
        "-Xms128m",
        "-Xmx${JVMHeapSizeManager.determineMaxHeapSizeMB() ?: DEFAULT_MAX_HEAP_SIZE_IN_MB}m",
        "-Duser.language=en",
        "-Duser.region=US",
        "-DLspIncludeDefinition=false",
        "-DLspResolveCompletion=true",
    )

    init {
        val pluginDescriptor = PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID))
            ?: throw IllegalStateException("Cannot find plugin by ID: $PLUGIN_ID")
        val lspJarPath = pluginDescriptor.pluginPath.toAbsolutePath()
            .resolve("language-server/rell-language-server-0.8.5.jar")

        val jvmExecutablePath = computeJavaPath()

        val launchCommands = listOf(jvmExecutablePath, *extraOptions.toTypedArray(), "-jar", lspJarPath.toString())
        setCommandLine(GeneralCommandLine(launchCommands))
    }

    override fun getInitializationOptions(rootUri: VirtualFile?): Any? {
        val pluginSettings = RellPluginSettingsState.instance
        val inlayHintsSettings = getInlayHintsSettings()
        
        return mapOf(
            "indexCaching" to pluginSettings.indexCaching,
            "inlayHints" to inlayHintsSettings
        )
    }
    
    private fun computeJavaPath(): String {
        return File(System.getProperty("java.home"), "bin/java" + (if (SystemInfo.isWindows) ".exe" else "")).absolutePath
    }

    companion object {
        private const val PLUGIN_ID = "net.postchain.rellide.jetbrains"
        private const val DEFAULT_MAX_HEAP_SIZE_IN_MB = 2048
    }
}
