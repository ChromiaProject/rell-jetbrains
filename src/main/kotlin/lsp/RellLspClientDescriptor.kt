package net.postchain.rellide.jetbrains.lsp

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.Lsp4jClient
import com.intellij.platform.lsp.api.LspCommunicationChannel
import com.intellij.platform.lsp.api.LspServerNotificationsHandler
import com.intellij.platform.lsp.api.ProjectWideLspClientDescriptor
import com.intellij.platform.lsp.api.customization.LspCustomization
import net.postchain.rellide.jetbrains.chromia.ChromiaSettingsFiles
import net.postchain.rellide.jetbrains.chromia.RellLspRuntimeManager
import net.postchain.rellide.jetbrains.chromia.RellVersion
import net.postchain.rellide.jetbrains.chromia.RellVersionRegistry
import net.postchain.rellide.jetbrains.sentry.SentryReportSubmitter
import net.postchain.rellide.jetbrains.settings.RellPluginSettingsState
import net.postchain.rellide.jetbrains.toolwindow.project.ChromiaProjectDiscovery
import org.eclipse.lsp4j.services.LanguageServer
import kotlin.io.path.*

/**
 * Descriptor of the language server for one supported Rell version. The newest supported version
 * runs from the runtime bundled with the plugin; older supported versions run from the downloaded
 * runtime in `<system>/rell-lsp/<version>/` (see [RellLspRuntimeManager]).
 *
 * The platform identifies a client by descriptor class + presentable name + roots, so the
 * version-carrying presentable name is what keeps the per-version clients apart.
 */
class RellLspClientDescriptor(
    project: Project,
    val version: RellVersion,
    lspLibDir: () -> java.nio.file.Path,
) : ProjectWideLspClientDescriptor(project, presentableName(version)) {
    // Resolved only when a server actually launches: the bundled dir needs the installed plugin's
    // path, which does not exist under the test runner.
    private val lspLibDir by lazy(lspLibDir)

    override fun isSupportedFile(file: VirtualFile): Boolean =
        file.extension == RELL_EXTENSION && routedRellVersion(project, file) == version

    override val lspCommunicationChannel: LspCommunicationChannel
        get() = if (useSocket() && version == RellVersionRegistry.max) {
            LspCommunicationChannel.Socket(SOCKET_PORT, startProcess = false)
        } else {
            LspCommunicationChannel.StdIO
        }

    override fun createCommandLine(): GeneralCommandLine {
        val extraOptions = buildList {
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

        val classpath = (lspLibDir / "*").pathString

        return GeneralCommandLine(
            listOf(computeJavaPath(), *extraOptions.toTypedArray(), "-cp", classpath, LSP_MAIN_CLASS)
        )
    }

    override fun createInitializationOptions(): Any {
        val pluginSettings = RellPluginSettingsState.instance
        val inlayHintsSettings = project.service<RellInlayHintsConfigurationListener>().getInlayHintsSettings()

        return buildMap {
            put("indexCaching", pluginSettings.indexCaching)
            put("inlayHints", inlayHintsSettings)
            // Sent only when some project uses a non-default settings file: servers that
            // understand the option anchor an index root at each of these files and let them
            // shadow the chromia.yml of their own directory, while still discovering every other
            // project by name. Released servers ignore the unknown key. When every project uses
            // chromia.yml the key is omitted and the server's own discovery already matches.
            chromiaConfigFileUris(project)?.let { put("chromiaConfigFiles", it) }
        }
    }

    override val lsp4jServerClass: Class<out LanguageServer> = RellServerApi::class.java

    // Keeps a copy of every pushed diagnostic so batch inspections can report them
    // (see RellLspDiagnosticsInspection).
    override fun createLsp4jClient(handler: LspServerNotificationsHandler): Lsp4jClient =
        Lsp4jClient(DiagnosticsRecordingHandler(handler, project))

    override val lspCustomization: LspCustomization = RellLspCustomization

    private fun computeJavaPath(): String = Path(
        System.getProperty("java.home"), "bin/java" + (if (SystemInfo.isWindows) ".exe" else "")
    ).absolutePathString()

    companion object {
        const val RELL_EXTENSION = "rell"
        private const val USE_SOCKET_PROPERTY = "rell.lsp.useSocket"
        private const val SOCKET_PORT = 5008
        private const val DEFAULT_MAX_HEAP_SIZE_IN_MB = 2048
        private const val LSP_MAIN_CLASS = "net.postchain.rell.toolbox.lsp.StdioMainKt"
        private const val LOG4J_OVERRIDE_FILE = "log4j2-override.properties"

        private fun presentableName(version: RellVersion): String =
            if (version == RellVersionRegistry.max) "Rell Language Server" else "Rell Language Server ($version)"

        /** Dev mode (work/local-lsp.sh): connect to an externally launched server instead of spawning one. */
        private fun useSocket(): Boolean = System.getProperty(USE_SOCKET_PROPERTY, "false").toBoolean()

        fun newest(project: Project): RellLspClientDescriptor =
            RellLspClientDescriptor(project, RellVersionRegistry.max, ::bundledLspLibDir)

        fun versioned(project: Project, version: RellVersion): RellLspClientDescriptor =
            if (version == RellVersionRegistry.max) {
                newest(project)
            } else {
                RellLspClientDescriptor(project, version) {
                    RellLspRuntimeManager.getInstance().cachedRuntimeDir(version)
                }
            }

        // ErrorReportSubmitter.EP_NAME exists but is internal API, like every by-ID or by-class
        // plugin lookup (PluginManagerCore.getPlugin, PluginManager.findEnabledPlugin/
        // getPluginByClass); the EP itself and this constructor are not.
        private val ERROR_HANDLER_EP = ExtensionPointName<ErrorReportSubmitter>("com.intellij.errorHandler")

        /**
         * The active settings file of every discovered Chromia project whose active file is not
         * the default `chromia.yml`, as `file:` URIs — null when there is none, since the server
         * discovers `chromia.yml` itself. Only the alternates are sent: the server merges them
         * with its own discovery, so listing the defaults would add nothing while making the list
         * look like a complete set of roots (project discovery is bounded to the project base
         * path, and the server's scan is not).
         */
        private fun chromiaConfigFileUris(project: Project): List<String>? =
            ChromiaProjectDiscovery.discoverProjects(project)
                .filter { it.activeSettingsFile?.let { name -> !ChromiaSettingsFiles.isDefaultName(name) } == true }
                .mapNotNull { it.configFile }
                .map { java.io.File(it).toURI().toString() }
                .takeIf { it.isNotEmpty() }

        /** The language-server runtime bundled with the plugin — always the newest supported Rell. */
        fun bundledLspLibDir(): java.nio.file.Path {
            // The plugin path comes from our own error-handler extension bean: extension beans are
            // PluginAware, so the platform injects the descriptor without any plugin lookup.
            val submitter = ERROR_HANDLER_EP.extensionList.filterIsInstance<SentryReportSubmitter>().firstOrNull()
            val plugin = checkNotNull(submitter?.pluginDescriptor) { "Rell plugin descriptor unavailable" }
            return plugin.pluginPath.toAbsolutePath() / "language-server"
        }
    }
}
