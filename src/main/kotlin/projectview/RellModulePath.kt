package net.postchain.rellide.jetbrains.projectview

import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile

/**
 * The Rell module path a directory contributes, as the compiler derives it: the directory's path
 * relative to the source root with `/` replaced by `.` (see `C_ModuleUtils.getModuleInfo`). So
 * `src/main/core` under source root `src` is the namespace `main.core`, which is also the module
 * name of every `.rell` file in it that has no `module` header.
 *
 * A directory only contributes when every segment is a valid Rell name — the compiler drops a path
 * with an unnameable segment instead of mangling it, and so does this, leaving such directories
 * undecorated rather than labelled with a module name that no `import` could ever spell.
 */
object RellModulePath {
    private val SEGMENT = Regex("[A-Za-z_][A-Za-z0-9_]*")

    /**
     * [directory]'s dot-separated path under [sourceRoot], empty for the source root itself and
     * null when [directory] is outside it or names a segment Rell cannot address.
     */
    fun of(directory: VirtualFile, sourceRoot: VirtualFile): String? {
        val relative = VfsUtilCore.getRelativePath(directory, sourceRoot) ?: return null
        if (relative.isEmpty()) return ""
        val segments = relative.split('/')
        return if (segments.all(SEGMENT::matches)) segments.joinToString(".") else null
    }
}
