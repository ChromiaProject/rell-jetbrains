package net.postchain.rellide.jetbrains.projectview

import com.intellij.icons.AllIcons
import com.intellij.ide.IconProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import net.postchain.rellide.jetbrains.chromia.RellVersionResolver
import javax.swing.Icon

/**
 * Draws Rell source trees the way JVM and Python source trees are drawn: a source-root icon on the
 * directory a Chromia settings file compiles from, a package icon on every directory beneath it.
 * Without this a Rell project is a wall of plain folders, with nothing to show where the module
 * namespace starts.
 *
 * `PsiDirectoryNode.setupIcon` resolves directory icons through `CompoundIconProvider`, so this
 * extension point also covers the navigation bar and Go to File, not just the Project view.
 */
class RellDirectoryIconProvider : IconProvider(), DumbAware {
    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        val directory = element as? PsiDirectory ?: return null
        val file = directory.virtualFile
        val sourceRoot = RellVersionResolver.getInstance(directory.project).sourceRootOf(file) ?: return null
        if (sourceRoot == file) return AllIcons.Modules.SourceRoot
        return AllIcons.Nodes.Package.takeIf { RellModulePath.of(file, sourceRoot) != null }
    }
}
