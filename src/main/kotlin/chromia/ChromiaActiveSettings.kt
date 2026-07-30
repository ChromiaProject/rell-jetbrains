package net.postchain.rellide.jetbrains.chromia

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.platform.lsp.api.LspClientManager
import com.intellij.ui.EditorNotifications
import net.postchain.rellide.jetbrains.lsp.RellLspIntegrationProvider

/**
 * The user's choice of active Chromia settings file per directory, mirroring `chr`'s
 * `-s/--settings` flag. When a directory holds several settings files (e.g. one per deployment
 * network), the active one decides the Rell version its files resolve to and the `--settings`
 * argument tool-window commands run with. Directories without a choice fall back to
 * [ChromiaSettingsFiles.defaultChoice]. Stored in the workspace file: the choice is per-user
 * working state, like a selected run configuration, not project configuration.
 */
@Service(Service.Level.PROJECT)
@State(name = "ChromiaActiveSettings", storages = [Storage(StoragePathMacros.WORKSPACE_FILE)])
class ChromiaActiveSettings(private val project: Project) : PersistentStateComponent<ChromiaActiveSettings.State> {

    class State {
        var activeByDirectory: MutableMap<String, String> = mutableMapOf()
    }

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    /** The chosen settings file name for [directoryPath], or null when the default rule applies. */
    fun activeFileName(directoryPath: String): String? = state.activeByDirectory[normalize(directoryPath)]

    /**
     * Chooses [fileName] as the active settings file for [directoryPath] (null reverts to the
     * default rule) and re-resolves everything that depends on it — a changed active file can move
     * every file of the project to a different toolchain, exactly like editing a `rellVersion`.
     */
    fun setActive(directoryPath: String, fileName: String?) {
        val key = normalize(directoryPath)
        if (fileName == null) {
            state.activeByDirectory.remove(key)
        } else {
            state.activeByDirectory[key] = fileName
        }
        ChromiaConfigRefresh.versionChanged(project)
    }

    companion object {
        fun getInstance(project: Project): ChromiaActiveSettings = project.getService(ChromiaActiveSettings::class.java)

        /**
         * Directory keys reach this store from both [com.intellij.openapi.vfs.VirtualFile.path]
         * (always `/`-separated) and [java.io.File.getAbsolutePath] (`\`-separated on Windows), so
         * they are normalized to the VFS form — otherwise the banners and the tool window would
         * write to two different halves of the same map.
         */
        fun normalize(directoryPath: String): String =
            directoryPath.replace('\\', '/').trimEnd('/').ifEmpty { "/" }
    }
}

/**
 * The re-resolution cascade after Chromia settings state changes, shared by the VFS listener and
 * [ChromiaActiveSettings]: version-dependent highlighting must re-run, banners must update, and —
 * when the governing version may have changed — resolver caches must drop and the Rell language
 * servers must restart so the platform re-routes files to the right toolchain.
 */
internal object ChromiaConfigRefresh {

    /** A change that cannot have moved any file to a different Rell version. */
    fun touched(project: Project) {
        project.messageBus.syncPublisher(RellVersionResolver.TOPIC).chromiaConfigChanged()
        DaemonCodeAnalyzer.getInstance(project).restart("Rell Chromia settings change")
        EditorNotifications.getInstance(project).updateAllNotifications()
    }

    /** A change that may route files to a different toolchain. */
    fun versionChanged(project: Project) {
        // Structural events can leave stale entries at arbitrary paths; the cache is tiny.
        project.serviceIfCreated<RellVersionResolver>()?.dropCaches()
        touched(project)
        LspClientManager.getInstance(project).stopAndRestartClientsIfNeeded(RellLspIntegrationProvider::class.java)
    }
}
