package net.postchain.rellide.jetbrains.chromia

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.serviceIfCreated
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent
import com.intellij.openapi.vfs.newvfs.events.VFilePropertyChangeEvent
import com.intellij.ui.EditorNotifications
import com.redhat.devtools.lsp4ij.LanguageServerManager

/**
 * Reacts to VFS changes affecting `chromia.yml` files in this project's content, at two levels:
 *
 * - any relevant change refreshes resolver state, notifies [RellVersionResolver.TOPIC], re-runs
 *   version-dependent highlighting, and updates version banners;
 * - only a change to a declared `compile.rellVersion` additionally stops the Rell language
 *   servers so lsp4ij re-routes files to the right toolchain. Non-version config edits (libs,
 *   source dir) never restart servers — the server's own file watcher handles those in-process,
 *   and a needless cold restart costs a full reindex.
 *
 * Directory-level operations matter as much as file-level ones: the VFS fires a single event for
 * the topmost directory on delete/move/rename, so those events are matched against the resolver's
 * cached config paths (old and new location); a created directory is matched by actually looking
 * for a `chromia.yml` inside it. Such structural events cannot be diffed cheaply, so they count
 * as version changes. Name comparisons ignore case because config discovery goes through
 * [VirtualFile.findChild], which honors the case-insensitivity of the underlying filesystem.
 *
 * Known limitation: a directory moved into the project from outside, or created with a config
 * nested deeper than its direct children, brings its `chromia.yml` files without a matching
 * event; consumers re-resolve lazily.
 */
internal class ChromiaConfigChangeListener(private val project: Project) : BulkFileListener {
    private enum class Impact { NONE, CONFIG_TOUCHED, VERSION_CHANGED }

    override fun after(events: List<VFileEvent>) {
        val resolver = project.serviceIfCreated<RellVersionResolver>()
        var impact = Impact.NONE

        for (event in events) {
            impact = maxOf(impact, impactOf(event, resolver))
            if (impact == Impact.VERSION_CHANGED) break
        }

        if (impact == Impact.NONE) return

        if (impact == Impact.VERSION_CHANGED) {
            // Structural events can leave stale entries at arbitrary paths; the cache is tiny.
            resolver?.dropCaches()
        }

        project.messageBus.syncPublisher(RellVersionResolver.TOPIC).chromiaConfigChanged()
        // Version-dependent highlighting (RellVersionSyntaxAnnotator) must re-run against the
        // re-resolved state, and version banners (RellVersionEditorNotificationProvider) must
        // appear or disappear accordingly.
        DaemonCodeAnalyzer.getInstance(project).restart("Rell chromia.yml change")
        EditorNotifications.getInstance(project).updateAllNotifications()

        if (impact == Impact.VERSION_CHANGED) {
            // A changed compile.rellVersion can move files to a different toolchain: stop the Rell
            // servers so lsp4ij re-routes through the document matchers on the next request.
            // willDisable=false is essential — the default StopOptions permanently disables the
            // server definition for the whole IDE session.
            val serverManager = LanguageServerManager.getInstance(project)
            val stopOnly = LanguageServerManager.StopOptions().setWillDisable(false)
            for (s in RellLspServers.allServerIds()) {
                serverManager.stop(s, stopOnly)
            }
        }
    }

    private fun impactOf(event: VFileEvent, resolver: RellVersionResolver?): Impact = when (event) {
        // The deleted file is invalid, so content-scoping is impossible — a config (or a directory
        // holding one) that anything depended on is necessarily in the resolver cache.
        is VFileDeleteEvent ->
            if (affectsCachedConfig(resolver, event.path)) Impact.VERSION_CHANGED else Impact.NONE
        is VFilePropertyChangeEvent -> when {
            event.propertyName != VirtualFile.PROP_NAME -> Impact.NONE
            (isConfigName(event.newValue) || isConfigName(event.oldValue)) && isInProjectContent(event.file) ->
                Impact.VERSION_CHANGED
            affectsCachedConfig(resolver, event.oldPath, event.path) -> Impact.VERSION_CHANGED
            else -> Impact.NONE
        }
        is VFileMoveEvent -> when {
            isConfigName(event.file.name) && isInProjectContent(event.file) -> Impact.VERSION_CHANGED
            affectsCachedConfig(resolver, event.oldPath, event.path) -> Impact.VERSION_CHANGED
            else -> Impact.NONE
        }
        // A new directory can bring a config with it (copy, checkout, unzip) without a per-child
        // event — but only react when it actually contains one, so ordinary build/VCS directory
        // churn stays free of side effects.
        is VFileCreateEvent -> when {
            event.isDirectory ->
                if (createdDirectoryHasConfig(event.file)) Impact.VERSION_CHANGED else Impact.NONE
            isConfigName(event.childName) && isInProjectContent(event.file) -> Impact.VERSION_CHANGED
            else -> Impact.NONE
        }
        // Content changes and anything else file-level: diff the declared version.
        else ->
            if (isConfigName(event.path.substringAfterLast('/')) && isInProjectContent(event.file)) {
                configContentImpact(event.file, resolver)
            } else {
                Impact.NONE
            }
    }

    private fun configContentImpact(file: VirtualFile?, resolver: RellVersionResolver?): Impact = when {
        file == null -> Impact.VERSION_CHANGED // cannot diff — stay conservative
        resolver == null -> Impact.CONFIG_TOUCHED // no resolver yet, so nothing cached depends on it
        resolver.refreshConfig(file) -> Impact.VERSION_CHANGED
        else -> Impact.CONFIG_TOUCHED
    }

    private fun isConfigName(value: Any?): Boolean =
        (value as? String)?.equals(RellVersionResolver.CHROMIA_YML, ignoreCase = true) == true

    private fun affectsCachedConfig(resolver: RellVersionResolver?, vararg paths: String): Boolean =
        resolver != null && paths.any(resolver::hasCachedConfigUnder)

    private fun createdDirectoryHasConfig(dir: VirtualFile?): Boolean {
        if (dir == null || !dir.isValid) return false
        if (dir.findChild(RellVersionResolver.CHROMIA_YML) == null) return false
        return isInProjectContent(dir)
    }

    private fun isInProjectContent(file: VirtualFile?): Boolean {
        if (file == null || !file.isValid) return false
        return ApplicationManager.getApplication().runReadAction<Boolean> {
            !project.isDisposed && ProjectFileIndex.getInstance(project).isInContent(file)
        }
    }
}
