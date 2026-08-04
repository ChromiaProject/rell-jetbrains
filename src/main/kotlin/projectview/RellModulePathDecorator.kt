package net.postchain.rellide.jetbrains.projectview

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import net.postchain.rellide.jetbrains.chromia.RellVersionResolver

/**
 * Labels each directory in a Rell source tree with the module namespace it stands for, the way the
 * Java view labels a package directory. The source root itself is left alone — its label would be
 * empty, and its icon already says what it is.
 */
class RellModulePathDecorator : ProjectViewNodeDecorator {
    override fun decorate(node: ProjectViewNode<*>, data: PresentationData) {
        val directory = (node as? PsiDirectoryNode)?.value?.takeIf { it.isValid } ?: return
        val project = node.project ?: return
        val file = directory.virtualFile
        val sourceRoot = RellVersionResolver.getInstance(project).sourceRootOf(file) ?: return
        val modulePath = RellModulePath.of(file, sourceRoot)?.takeIf { it.isNotEmpty() } ?: return
        data.locationString = modulePath
    }
}
