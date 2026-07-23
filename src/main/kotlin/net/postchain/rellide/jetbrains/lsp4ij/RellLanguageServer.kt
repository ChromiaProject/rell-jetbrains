package net.postchain.rellide.jetbrains.lsp4ij

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.components.service
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.VirtualFile
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider
import net.postchain.rellide.jetbrains.settings.RellPluginSettingsState
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.div
import kotlin.io.path.pathString

class RellLanguageServer(val project: Project) : OSProcessStreamConnectionProvider() {
    private val extraOptions = listOf(
        "-Xms128m",
        "-Xmx${JvmHeapSizeManager.determineMaxHeapSizeMB() ?: DEFAULT_MAX_HEAP_SIZE_IN_MB}m",
        "-Duser.language=en",
        "-Duser.region=US",
        "-DLspIncludeDefinition=false",
        "-DLspResolveCompletion=true",
    )

    init {
        val pluginDescriptor = checkNotNull(PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID))) {
            "Cannot find plugin by ID: $PLUGIN_ID"
        }

        val lspLibDir = pluginDescriptor.pluginPath.toAbsolutePath() / "language-server"
        val classpath = (lspLibDir / "*").pathString

        val jvmExecutablePath = computeJavaPath()

        val launchCommands = listOf(jvmExecutablePath, *extraOptions.toTypedArray(), "-cp", classpath, LSP_MAIN_CLASS)
        commandLine = GeneralCommandLine(launchCommands)
    }

    override fun getInitializationOptions(rootUri: VirtualFile?): Any {
        val pluginSettings = RellPluginSettingsState.instance
        val inlayHintsSettings = project.service<RellInlayHintsConfigurationListener>().getInlayHintsSettings()

        return mapOf("indexCaching" to pluginSettings.indexCaching, "inlayHints" to inlayHintsSettings)
    }

    private fun computeJavaPath(): String = Path(
        System.getProperty("java.home"), "bin/java" + (if (SystemInfo.isWindows) ".exe" else "")
    ).absolutePathString()

    companion object {
        private const val PLUGIN_ID = "net.postchain.rellide.jetbrains"
        private const val DEFAULT_MAX_HEAP_SIZE_IN_MB = 2048
        private const val LSP_MAIN_CLASS = "net.postchain.rell.toolbox.lsp.StdioMainKt"
    }
}
