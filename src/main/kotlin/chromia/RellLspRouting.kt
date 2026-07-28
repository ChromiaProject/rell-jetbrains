package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.redhat.devtools.lsp4ij.AbstractDocumentMatcher

/**
 * lsp4ij server ids for the per-version Rell language servers. The newest version keeps the
 * historical id; each older supported version has its own `<server>` entry in plugin.xml
 * (`RellLspRoutingTest` guards the two against drifting apart).
 */
object RellLspServers {
    const val NEWEST_SERVER_ID = "rellLanguageServer"

    fun versionedServerId(version: RellVersion): String =
        "rellLanguageServer_" + version.toString().replace('.', '_')

    fun allServerIds(): List<String> = listOf(NEWEST_SERVER_ID) +
        RellVersionRegistry.supported.filter { it < RellVersionRegistry.max }.map(::versionedServerId)
}

/**
 * Routes a file to the bundled newest-version server: files resolving to the newest supported Rell
 * (declared, clamped, or defaulted). Files below the compatibility floor match no server at all —
 * that is the hard cease of docs/COMPATIBILITY.md.
 */
class RellNewestVersionDocumentMatcher : AbstractDocumentMatcher() {
    override fun match(file: VirtualFile, project: Project): Boolean {
        val resolution = RellVersionResolver.getInstance(project).resolve(file)
        return resolution !is RellVersionResolution.Unsupported &&
            resolution.effectiveVersion == RellVersionRegistry.max
    }
}

/**
 * Routes a file to the server of one specific older supported version. If that runtime is not
 * downloaded yet, the download starts in the background and the file matches no server until it
 * completes.
 */
abstract class RellVersionedDocumentMatcher(private val version: RellVersion) : AbstractDocumentMatcher() {
    override fun match(file: VirtualFile, project: Project): Boolean {
        val resolution = RellVersionResolver.getInstance(project).resolve(file)
        if ((resolution as? RellVersionResolution.Supported)?.version != version) return false
        val runtimes = RellLspRuntimeManager.getInstance()
        if (runtimes.isRuntimeReady(version)) return true
        runtimes.ensureRuntimeAsync(version, project)
        return false
    }
}

class Rell0160DocumentMatcher : RellVersionedDocumentMatcher(RellVersion(0, 16, 0))

class Rell0161DocumentMatcher : RellVersionedDocumentMatcher(RellVersion(0, 16, 1))
