package net.postchain.rellide.jetbrains.chromia

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.application.EDT
import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.platform.lsp.api.LspClientManager
import com.intellij.ui.EditorNotifications
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.postchain.rellide.jetbrains.lsp.RellLspClientDescriptor
import net.postchain.rellide.jetbrains.lsp.RellLspIntegrationProvider
import net.postchain.rellide.jetbrains.lsp.SetSettingsFilesParams
import net.postchain.rellide.jetbrains.lsp.getRellLspClient
import net.postchain.rellide.jetbrains.lsp.rellRequest

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
 * [ChromiaActiveSettings]: version-dependent highlighting must re-run, the Project view must redraw
 * because `compile.source` decides which directories are source roots and carry module names, and —
 * when the governing settings file may have changed — resolver caches must drop and the language
 * server must be told which settings files the user has chosen.
 */
internal object ChromiaConfigRefresh {

    /** A change that cannot have moved any file to a different Rell version. */
    fun touched(project: Project) {
        project.messageBus.syncPublisher(RellVersionResolver.TOPIC).chromiaConfigChanged()
        DaemonCodeAnalyzer.getInstance(project).restart("Rell Chromia settings change")
        EditorNotifications.getInstance(project).updateAllNotifications()
        project.serviceIfCreated<ProjectView>()?.refresh()
    }

    /** A change that may hand a directory to a different settings file. */
    fun versionChanged(project: Project) {
        // Structural events can leave stale entries at arbitrary paths; the cache is tiny.
        project.serviceIfCreated<RellVersionResolver>()?.dropCaches()
        touched(project)
        pushSettingsFiles(project)
    }

    /**
     * Re-sends the chosen settings files, so the server re-indexes the affected roots against the
     * newly active file. Cheaper and less disruptive than restarting the server, and it is the only
     * way a switch reaches the server at all: nothing changed on disk for its file watcher to see.
     *
     * A server too old to know the request answers null, and then the restart is the fallback — it
     * re-sends the same list as an initialization option, which is how this worked before.
     */
    private fun pushSettingsFiles(project: Project) {
        val client = getRellLspClient(project) ?: return restartClients(project)
        val uris = RellLspClientDescriptor.chromiaConfigFileUris(project) ?: emptyList()
        project.service<ChromiaConfigRefreshScope>().scope.launch {
            when (client.rellRequest { it.setSettingsFiles(SetSettingsFilesParams(uris)) }) {
                // The server re-publishes diagnostics itself; the daemon still has to re-run so
                // the editor picks them up for files it already highlighted.
                true -> withContext(Dispatchers.EDT) {
                    DaemonCodeAnalyzer.getInstance(project).restart("Rell settings file switched")
                }
                // Nothing to re-index: the chosen set did not actually change.
                false -> Unit
                null -> withContext(Dispatchers.EDT) { restartClients(project) }
            }
        }
    }

    private fun restartClients(project: Project) {
        LspClientManager.getInstance(project).stopAndRestartClientsIfNeeded(RellLspIntegrationProvider::class.java)
    }
}

/**
 * The scope the settings-file push awaits its response in. The request suspends, and the callers
 * reach [ChromiaConfigRefresh] from wherever a settings file happened to change — off the EDT, with
 * no progress indicator or job to bridge into — so it needs a scope of its own, cancelled when the
 * project closes.
 */
@Service(Service.Level.PROJECT)
internal class ChromiaConfigRefreshScope(val scope: CoroutineScope)
