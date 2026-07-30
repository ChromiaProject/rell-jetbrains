package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.serviceIfCreated
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.*

/**
 * Reacts to VFS changes affecting Chromia settings files in this project's content, at two levels:
 *
 * - any relevant change refreshes resolver state, notifies [RellVersionResolver.TOPIC], re-runs
 *   version-dependent highlighting, and updates version banners;
 * - only a change that can move files to a different toolchain — a `compile.rellVersion` or
 *   `compile.source` delta, a settings file appearing, disappearing, or losing its qualification —
 *   additionally stops the Rell language servers so the platform re-routes files. Other config
 *   edits (libs) never restart servers — the server's own file watcher handles those in-process,
 *   and a needless cold restart costs a full reindex.
 *
 * `chromia.yml` is relevant by name; other `*.yml` files qualify as settings files only by their
 * content (see [ChromiaSettingsFiles]), which cannot be checked here — [after] runs on the EDT
 * inside the write action, and parsing YAML there would stall every save. Such files are collected
 * and diffed on a background thread, which re-resolves only on an actual delta; the cheap
 * name-and-cache decisions stay inline.
 *
 * Directory-level operations matter as much as file-level ones: the VFS fires a single event for
 * the topmost directory on delete/move/rename, so those events are matched against the resolver's
 * cached settings-file paths (old and new location); a created directory is matched by looking for
 * settings files inside it. Such structural events cannot be diffed cheaply, so they count as
 * version changes. So do directories on the path to a cached settings file's source root — its
 * `compile.source`, or the default layout chain — because source-root existence decides which
 * files that settings file claims. Name comparisons ignore case because config discovery goes
 * through [VirtualFile.findChild], which honors the case-insensitivity of the underlying
 * filesystem.
 *
 * Known limitation: a directory moved into the project from outside, or created with settings
 * files nested deeper than its direct children, brings them without a matching event; consumers
 * re-resolve lazily.
 */
internal class ChromiaConfigChangeListener(private val project: Project) : BulkFileListener {
    private enum class Impact { NONE, CONFIG_TOUCHED, VERSION_CHANGED }

    override fun after(events: List<VFileEvent>) {
        val resolver = project.serviceIfCreated<RellVersionResolver>()
        var impact = Impact.NONE
        val toDiff = mutableListOf<VirtualFile>()

        for (event in events) {
            impact = maxOf(impact, impactOf(event, resolver, toDiff))
            if (impact == Impact.VERSION_CHANGED) break
        }

        when {
            impact == Impact.VERSION_CHANGED -> ChromiaConfigRefresh.versionChanged(project)
            toDiff.isNotEmpty() && resolver != null -> diffInBackground(resolver, toDiff, impact)
            impact == Impact.CONFIG_TOUCHED -> ChromiaConfigRefresh.touched(project)
        }
    }

    /**
     * Parses the collected candidates off the EDT and re-resolves only if one of them actually
     * changed a resolution input (declared version, settings-file qualification, source root).
     */
    private fun diffInBackground(resolver: RellVersionResolver, files: List<VirtualFile>, base: Impact) {
        ApplicationManager.getApplication().executeOnPooledThread {
            if (project.isDisposed) return@executeOnPooledThread
            var changed = false
            var isConfig = base == Impact.CONFIG_TOUCHED
            for (file in files) {
                if (!file.isValid) continue
                val delta = ReadAction.nonBlocking<Boolean> {
                    if (project.isDisposed) false else resolver.refreshConfig(file)
                }.executeSynchronously()
                changed = changed || delta
                isConfig = isConfig || resolver.isKnownCandidate(file.path)
            }
            if (!changed && !isConfig) return@executeOnPooledThread
            ApplicationManager.getApplication().invokeLater({
                if (project.isDisposed) return@invokeLater
                if (changed) ChromiaConfigRefresh.versionChanged(project) else ChromiaConfigRefresh.touched(project)
            }, project.disposed)
        }
    }

    private fun impactOf(event: VFileEvent, resolver: RellVersionResolver?, toDiff: MutableList<VirtualFile>): Impact =
        when (event) {
            // The deleted file is invalid, so content-scoping is impossible — a settings file (or a
            // directory holding one) that anything depended on is necessarily in the resolver cache.
            is VFileDeleteEvent -> when {
                affectsCachedConfig(resolver, event.path) -> Impact.VERSION_CHANGED
                affectsCachedSourceRoot(resolver, event.path) -> Impact.VERSION_CHANGED
                else -> Impact.NONE
            }

            is VFilePropertyChangeEvent -> when {
                event.propertyName != VirtualFile.PROP_NAME -> Impact.NONE
                renameInvolvesSettingsFile(resolver, event) -> Impact.VERSION_CHANGED
                affectsCachedConfig(resolver, event.oldPath, event.path) -> Impact.VERSION_CHANGED
                affectsCachedSourceRoot(resolver, event.oldPath, event.path) -> Impact.VERSION_CHANGED
                else -> Impact.NONE
            }

            is VFileMoveEvent -> when {
                isSettingsFile(resolver, event.file, cachedAt = event.oldPath) && isInProjectContent(event.file) ->
                    Impact.VERSION_CHANGED

                affectsCachedConfig(resolver, event.oldPath, event.path) -> Impact.VERSION_CHANGED
                affectsCachedSourceRoot(resolver, event.oldPath, event.path) -> Impact.VERSION_CHANGED
                else -> Impact.NONE
            }
            // The event's file is the copy *source*; the copy is a brand-new path with no cache
            // entry, so a copy landing as a settings file is a new config wherever it appears.
            is VFileCopyEvent -> {
                val copy = event.findCreatedFile()
                when {
                    copy == null || !isInProjectContent(copy) -> Impact.NONE
                    ChromiaSettingsFiles.isDefaultName(event.newChildName) -> Impact.VERSION_CHANGED
                    ChromiaSettingsFiles.isYmlName(event.newChildName) -> defer(copy, toDiff)
                    else -> Impact.NONE
                }
            }
            // A new directory can bring settings files with it (copy, checkout, unzip) without a
            // per-child event — but only react when it actually contains one, so ordinary
            // build/VCS directory churn stays free of side effects.
            is VFileCreateEvent -> when {
                event.isDirectory -> createdDirectoryImpact(event, resolver, toDiff)
                !ChromiaSettingsFiles.isYmlName(event.childName) -> Impact.NONE
                !isInProjectContent(event.file) -> Impact.NONE
                isSettingsFile(resolver, event.file, cachedAt = event.file?.path) -> Impact.VERSION_CHANGED
                // A created alternate only counts once its content proves it is a settings file.
                else -> defer(event.file, toDiff)
            }
            // Content changes and anything else file-level.
            else -> {
                val name = event.path.substringAfterLast('/')
                when {
                    !ChromiaSettingsFiles.isYmlName(name) -> Impact.NONE
                    !isInProjectContent(event.file) -> Impact.NONE
                    // Nothing cached depends on it yet, and an alternate cannot qualify unparsed.
                    resolver == null ->
                        if (ChromiaSettingsFiles.isDefaultName(name)) Impact.CONFIG_TOUCHED else Impact.NONE
                    // An already-parsed file (settings file or not) costs one re-read to diff, the
                    // same as before alternates existed, so it stays inline and its effect is
                    // visible as soon as the write action finishes — including a plain yml that
                    // has just gained a `blockchains` section and become a settings file.
                    ChromiaSettingsFiles.isDefaultName(name) || resolver.isCached(event.path) ->
                        configContentImpact(event.file, resolver)
                    // A never-parsed *.yml would need a full parse to classify — off the EDT.
                    else -> defer(event.file, toDiff)
                }
            }
        }

    private fun configContentImpact(file: VirtualFile?, resolver: RellVersionResolver?): Impact = when {
        file == null || resolver == null -> Impact.VERSION_CHANGED // cannot diff — stay conservative
        resolver.refreshConfig(file) -> Impact.VERSION_CHANGED
        else -> Impact.CONFIG_TOUCHED
    }

    private fun createdDirectoryImpact(
        event: VFileCreateEvent,
        resolver: RellVersionResolver?,
        toDiff: MutableList<VirtualFile>,
    ): Impact {
        if (affectsCachedSourceRoot(resolver, "${event.parent.path}/${event.childName}")) {
            return Impact.VERSION_CHANGED
        }
        val dir = event.file
        if (dir == null || !dir.isValid || !isInProjectContent(dir)) return Impact.NONE

        var impact = Impact.NONE
        for (child in dir.children) {
            if (child.isDirectory || !ChromiaSettingsFiles.isYmlName(child.name)) continue
            if (ChromiaSettingsFiles.isDefaultName(child.name)) return Impact.VERSION_CHANGED
            impact = maxOf(impact, defer(child, toDiff))
        }
        return impact
    }

    /** Queues [file] for the background parse; contributes no impact of its own. */
    private fun defer(file: VirtualFile?, toDiff: MutableList<VirtualFile>): Impact {
        file?.let(toDiff::add)
        return Impact.NONE
    }

    /**
     * Whether [file] is a settings file for routing purposes: `chromia.yml` by name, or — for an
     * alternate name — a cached candidate at [cachedAt]. Never parses: this runs on the EDT inside
     * the write action, so an unqualified alternate is deliberately treated as irrelevant here and
     * picked up by the background diff of its own content event instead.
     */
    private fun isSettingsFile(resolver: RellVersionResolver?, file: VirtualFile?, cachedAt: String?): Boolean {
        val name = file?.name ?: cachedAt?.substringAfterLast('/') ?: return false
        if (ChromiaSettingsFiles.isDefaultName(name)) return true
        if (!ChromiaSettingsFiles.isYmlName(name)) return false
        return cachedAt != null && resolver?.isKnownCandidate(cachedAt) == true
    }

    private fun renameInvolvesSettingsFile(resolver: RellVersionResolver?, event: VFilePropertyChangeEvent): Boolean {
        val oldName = event.oldValue as? String
        val newName = event.newValue as? String
        if (ChromiaSettingsFiles.isDefaultName(oldName) || ChromiaSettingsFiles.isDefaultName(newName)) {
            return isInProjectContent(event.file)
        }
        if (!ChromiaSettingsFiles.isYmlName(oldName) && !ChromiaSettingsFiles.isYmlName(newName)) return false
        return isSettingsFile(resolver, event.file, cachedAt = event.oldPath) && isInProjectContent(event.file)
    }

    private fun affectsCachedSourceRoot(resolver: RellVersionResolver?, vararg paths: String): Boolean =
        resolver != null && paths.any(resolver::affectsCachedSourceRoot)

    private fun affectsCachedConfig(resolver: RellVersionResolver?, vararg paths: String): Boolean =
        resolver != null && paths.any(resolver::hasCachedConfigUnder)

    private fun isInProjectContent(file: VirtualFile?): Boolean {
        if (file == null || !file.isValid) return false
        return ApplicationManager.getApplication().runReadAction<Boolean> {
            !project.isDisposed && ProjectFileIndex.getInstance(project).isInContent(file)
        }
    }
}
