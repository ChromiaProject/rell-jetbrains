package net.postchain.rellide.jetbrains.toolwindow

import com.intellij.openapi.project.Project
import net.postchain.rellide.jetbrains.toolwindow.execution.ChromiaCommandExecutor
import net.postchain.rellide.jetbrains.toolwindow.tree.ChromiaNodeType
import net.postchain.rellide.jetbrains.toolwindow.tree.ChromiaTreeModel
import net.postchain.rellide.jetbrains.toolwindow.tree.ChromiaTreeNode
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JTree
import javax.swing.tree.TreePath

/**
 * Handles mouse events for the Chromia tree.
 * Double-click on command nodes executes the corresponding CLI command.
 */
class ChromiaTreeMouseListener(
    private val project: Project,
    private val treeModel: ChromiaTreeModel
) : MouseAdapter() {
    private val commandExecutor = ChromiaCommandExecutor(project)
    
    override fun mouseClicked(e: MouseEvent) {
        if (e.clickCount == 2) { // Double-click
            val tree = e.source as JTree
            val row = tree.getRowForLocation(e.x, e.y)
            
            if (row != -1) {
                val path = tree.ui.getPathForRow(tree, row)
                if (path != null) {
                    tree.selectionPath = path
                    handleDoubleClick(path)
                }
            }
        }
    }
    
    private fun handleDoubleClick(path: TreePath) {
        val node = path.lastPathComponent as? ChromiaTreeNode ?: return
        
        when (node.nodeType) {
            ChromiaNodeType.COMMAND -> {
                executeCommand(node)
            }
            ChromiaNodeType.CATEGORY -> {
                // Categories don't execute commands, but could expand/collapse
                // This is handled by the tree automatically
            }
            ChromiaNodeType.ROOT -> {
                // Root node doesn't execute commands
            }
            ChromiaNodeType.PROJECT -> {
                // Projects don't execute commands directly, but could be expanded to show commands
                // This is handled by the tree automatically
            }
        }
    }
    
    private fun executeCommand(node: ChromiaTreeNode) {
        val fullCommand = node.getFullCommand()
        if (fullCommand != null) {
            val workingDirectory = node.projectPath ?: project.basePath
            commandExecutor.executeCommand(fullCommand, workingDirectory)
        }
    }
}