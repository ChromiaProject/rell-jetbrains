package net.postchain.rellide.jetbrains.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import net.postchain.rellide.jetbrains.toolwindow.tree.ChromiaTreeModel
import net.postchain.rellide.jetbrains.toolwindow.tree.ChromiaTreeCellRenderer
import javax.swing.JComponent

/**
 * Chromia tool window component.
 * Contains a tree structure with Chromia CLI commands that can be executed.
 */
class ChromiaToolWindow(private val project: Project) {

    private val tree: Tree
    val treeModel: ChromiaTreeModel
    
    init {
        treeModel = ChromiaTreeModel(project)
        tree = Tree(treeModel)
        setupTree()
    }
    
    private fun setupTree() {
        tree.isRootVisible = false
        tree.showsRootHandles = true
        tree.isEditable = false

        tree.cellRenderer = ChromiaTreeCellRenderer()

        tree.addMouseListener(ChromiaTreeMouseListener(project, treeModel))

        tree.componentPopupMenu = ChromiaTreePopupMenu(project, treeModel, tree)
        
        // Expand root by default
        tree.expandRow(0)
    }
    
    fun getContent(): JComponent {
        return JBScrollPane(tree)
    }
    
    fun refreshTree() {
        treeModel.reload()
        tree.expandRow(0) // Keep root expanded after refresh
    }
}