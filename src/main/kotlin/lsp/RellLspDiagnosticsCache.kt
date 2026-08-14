package net.postchain.rellide.jetbrains.lsp

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServerNotificationsHandler
import com.intellij.ui.EditorNotifications
import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.PublishDiagnosticsParams
import java.net.URI
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap

/**
 * The diagnostics the Rell language servers have pushed, by file path. The platform's own LSP
 * integration consumes `textDocument/publishDiagnostics` for editor highlighting only; this cache
 * keeps a copy so batch inspections (Code | Inspect Code) can report them too — the same design
 * LSP4IJ's "Language Servers | Diagnostics" inspection used.
 *
 * Keys are local filesystem paths, not URIs: the server derives its URIs with `java.net.URI`
 * while lookups start from a [VirtualFile], and the two disagree about encoding details. Paths
 * are the common denominator.
 */
@Service(Service.Level.PROJECT)
class RellLspDiagnosticsCache {
    private val diagnosticsByPath = ConcurrentHashMap<String, List<Diagnostic>>()

    fun record(params: PublishDiagnosticsParams) {
        val key = pathKey(params.uri)
        if (params.diagnostics.isEmpty()) {
            diagnosticsByPath.remove(key)
        } else {
            diagnosticsByPath[key] = params.diagnostics.toList()
        }
        if (LOG.isDebugEnabled) LOG.debug("Recorded ${params.diagnostics.size} diagnostics for $key")
    }

    fun diagnosticsFor(file: VirtualFile): List<Diagnostic> {
        val diagnostics = diagnosticsByPath[file.path].orEmpty()
        if (LOG.isDebugEnabled) {
            LOG.debug("Lookup for ${file.path}: ${diagnostics.size} diagnostics (cache has ${diagnosticsByPath.size} files)")
        }
        return diagnostics
    }

    private fun pathKey(uri: String): String = try {
        Paths.get(URI(uri)).toString()
    } catch (_: Exception) {
        uri
    }

    companion object {
        /** Resolves a `publishDiagnostics` URI back to a [VirtualFile], for callers that need to react to new diagnostics. */
        fun fileFor(uri: String): VirtualFile? = try {
            LocalFileSystem.getInstance().findFileByPath(Paths.get(URI(uri)).toString())
        } catch (_: Exception) {
            null
        }

        private val LOG = logger<RellLspDiagnosticsCache>()

        fun getInstance(project: Project): RellLspDiagnosticsCache = project.service()
    }
}

/** Records every published diagnostic into [RellLspDiagnosticsCache] before the platform sees it. */
internal class DiagnosticsRecordingHandler(
    private val delegate: LspServerNotificationsHandler,
    private val project: Project,
) : LspServerNotificationsHandler by delegate {
    override fun publishDiagnostics(params: PublishDiagnosticsParams) {
        RellLspDiagnosticsCache.getInstance(project).record(params)
        delegate.publishDiagnostics(params)
        // Banners keyed off diagnostics (e.g. the missing-lib "Run chr install" suggestion) only
        // recompute when asked; a fresh push is the one signal they have to go check again.
        RellLspDiagnosticsCache.fileFor(params.uri)
            ?.let { EditorNotifications.getInstance(project).updateNotifications(it) }
    }
}
