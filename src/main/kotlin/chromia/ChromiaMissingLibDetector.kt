package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import net.postchain.rellide.jetbrains.lsp.RellLspDiagnosticsCache
import org.eclipse.lsp4j.Diagnostic

/**
 * Tells apart a "Module 'x.y.z' not found" diagnostic caused by a declared library `chr install`
 * has not fetched yet from one caused by a genuine typo, so the plugin can suggest `chr install`
 * only in the former case.
 *
 * Installed libraries land as whole subdirectories directly under the project's own Rell source
 * root (`chr install` writes `lib/<name>/...` next to the project's own `lib/...` modules, not
 * into any separate directory this plugin could check for existence up front). A typo inside an
 * existing module resolves every intermediate directory of its module path just fine and fails
 * only on the last segment; a not-yet-installed library fails on some intermediate directory,
 * since that whole subtree is simply absent. There is no static mapping from a `libs:` entry's
 * Maven-style coordinate (e.g. `com.chromia.hybridcompute`) to the module namespace the library
 * exposes (e.g. `lib.hybridcompute`) — that mapping is only known once the library is installed —
 * so this stays a heuristic rather than an exact check.
 */
object ChromiaMissingLibDetector {
    private val MODULE_NOT_FOUND = Regex("""Module '([^']+)' not found""")

    /** The unresolved module path of [diagnostic], when it looks like a missing-install case; null otherwise. */
    fun missingLibModule(project: Project, file: VirtualFile, diagnostic: Diagnostic): String? {
        val modulePath = MODULE_NOT_FOUND.find(diagnostic.message)?.groupValues?.get(1) ?: return null
        val resolver = RellVersionResolver.getInstance(project)
        if (!resolver.hasDeclaredLibs(file)) return null
        val sourceRoot = resolver.sourceRootOf(file) ?: return null
        return modulePath.takeIf { hasMissingIntermediateDirectory(sourceRoot, it) }
    }

    /** The first missing-install module path among [file]'s current diagnostics, if any. */
    fun anyMissingLibModule(project: Project, file: VirtualFile): String? =
        RellLspDiagnosticsCache.getInstance(project).diagnosticsFor(file)
            .firstNotNullOfOrNull { missingLibModule(project, file, it) }

    /** True when some directory before [modulePath]'s last segment does not exist under [sourceRoot]. */
    private fun hasMissingIntermediateDirectory(sourceRoot: VirtualFile, modulePath: String): Boolean {
        var current = sourceRoot
        val segments = modulePath.split('.')
        for (segment in segments.dropLast(1)) {
            val next = current.findChild(segment)?.takeIf { it.isDirectory } ?: return true
            current = next
        }
        return false
    }
}
