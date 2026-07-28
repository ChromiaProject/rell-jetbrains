package net.postchain.rellide.jetbrains.lsp4ij

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.VirtualFile
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider
import com.redhat.devtools.lsp4ij.server.definition.extension.ServerExtensionPointBean
import net.postchain.rellide.jetbrains.settings.RellPluginSettingsState
import kotlin.io.path.*

class RellLanguageServer(
    val project: Project,
    lspLibDir: java.nio.file.Path = bundledLspLibDir(),
) : OSProcessStreamConnectionProvider() {
    private val extraOptions = buildList {
        add("-Xms128m")
        add("-Xmx${JvmHeapSizeManager.determineMaxHeapSizeMB() ?: DEFAULT_MAX_HEAP_SIZE_IN_MB}m")
        add("-Duser.language=en")
        add("-Duser.region=US")
        add("-DLspIncludeDefinition=false")
        add("-DLspResolveCompletion=true")

        // The log4j2 config inside the language-server jar wires a Sentry appender into the root
        // logger, which uploads every logged error without asking the user. Point log4j2 at our own
        // config instead. Absent in local dev runs against a hand-placed jar, hence the check.
        val log4jOverride = lspLibDir / LOG4J_OVERRIDE_FILE
        if (log4jOverride.exists()) add("-Dlog4j2.configurationFile=${log4jOverride.absolutePathString()}")

        // Apache Fory reaches for sun.misc.Unsafe memory access; without this the JDK prints a deprecation
        // warning per launch. The option only exists since JDK 23, and older JBRs reject unknown options.
        if (Runtime.version().feature() >= 23) add("--sun-misc-unsafe-memory-access=allow")
    }

    init {
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
        /** Must match the `<server>` id this plugin registers with lsp4ij in `plugin.xml`. */
        private const val SERVER_ID = "rellLanguageServer"
        private const val DEFAULT_MAX_HEAP_SIZE_IN_MB = 2048
        private const val LSP_MAIN_CLASS = "net.postchain.rell.toolbox.lsp.StdioMainKt"
        private const val LOG4J_OVERRIDE_FILE = "log4j2-override.properties"

        /** The language-server runtime bundled with the plugin — always the newest supported Rell. */
        fun bundledLspLibDir(): java.nio.file.Path {
            // lsp4ij's <server> bean is PluginAware, so the platform sets our plugin descriptor on
            // it and the path comes back without any by-ID lookup — PluginManagerCore.getPlugin and
            // PluginManager.findEnabledPlugin are both internal API since 2026.2. Going through the
            // bean rather than our own class loader also keeps this working under the test runner,
            // where plugin classes come from the test classpath and carry no plugin descriptor.
            val serverBean = checkNotNull(
                ServerExtensionPointBean.EP_NAME.extensionList.find { it.id == SERVER_ID }
            ) {
                "No lsp4ij server extension registered with id $SERVER_ID"
            }

            return serverBean.pluginDescriptor.pluginPath.toAbsolutePath() / "language-server"
        }
    }
}
