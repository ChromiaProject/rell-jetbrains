package net.postchain.rellide.jetbrains.toolwindow

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import net.postchain.rellide.jetbrains.chromia.RellVersionResolver
import net.postchain.rellide.jetbrains.toolwindow.tree.ChromiaTreeCellRenderer
import net.postchain.rellide.jetbrains.toolwindow.tree.ChromiaTreeModel
import javax.swing.JComponent

/**
 * Chromia tool window component.
 * Contains a tree structure with Chromia CLI commands that can be executed.
 */
class ChromiaToolWindow(private val project: Project) {
    private val tree: Tree
    val treeModel: ChromiaTreeModel = ChromiaTreeModel(project)

    init {
        tree = Tree(treeModel)
        setupTree()
    }

    /**
     * Rebuilds the tree whenever Chromia settings state changes, so switching the active settings
     * file from a version banner is reflected here too — the active marker and the `--settings`
     * argument of every command depend on it.
     */
    fun subscribeToSettingsChanges(parent: Disposable) {
        project.messageBus.connect(parent).subscribe(
            RellVersionResolver.TOPIC,
            net.postchain.rellide.jetbrains.chromia.ChromiaConfigListener {
                ApplicationManager.getApplication().invokeLater({
                    if (!project.isDisposed) refreshTree()
                }, project.disposed)
            },
        )
    }

    private fun setupTree() {
        tree.isRootVisible = false
        tree.showsRootHandles = true
        tree.isEditable = false

        tree.cellRenderer = ChromiaTreeCellRenderer()

        tree.addMouseListener(ChromiaTreeMouseListener(project))

        tree.componentPopupMenu = ChromiaTreePopupMenu(project, treeModel, tree)

        // Expand root by default
        tree.expandRow(0)
    }

    fun getContent(): JComponent = JBScrollPane(tree)

    fun refreshTree() {
        treeModel.rebuild()
        tree.expandRow(0) // Keep root expanded after refresh
    }
}