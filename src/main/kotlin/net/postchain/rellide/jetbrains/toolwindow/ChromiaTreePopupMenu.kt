package net.postchain.rellide.jetbrains.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.JBMenuItem
import com.intellij.openapi.ui.JBPopupMenu
import com.intellij.openapi.ui.Messages
import com.intellij.util.ui.JBUI
import net.postchain.rellide.jetbrains.toolwindow.execution.ChromiaCommandExecutor
import net.postchain.rellide.jetbrains.toolwindow.settings.ChromiaToolWindowSettings
import net.postchain.rellide.jetbrains.toolwindow.tree.ChromiaNodeType
import net.postchain.rellide.jetbrains.toolwindow.tree.ChromiaTreeModel
import net.postchain.rellide.jetbrains.toolwindow.tree.ChromiaTreeNode
import javax.swing.Icon
import javax.swing.JMenuItem
import javax.swing.JTree
import javax.swing.tree.TreePath

/**
 * Context menu for the Chromia tree.
 * Provides options to configure command parameters, execute commands, and clear settings.
 */
class ChromiaTreePopupMenu(
        private val project: Project,
        private val treeModel: ChromiaTreeModel,
        private val tree: JTree
) : JBPopupMenu() {

    companion object {
        val logger = Logger.getInstance(ChromiaTreePopupMenu::class.java)
    }
    
    private val settings = ChromiaToolWindowSettings.getInstance(project)
    private val commandExecutor = ChromiaCommandExecutor(project)
    
    init {
        setupMenu()
    }
    
    private fun setupMenu() {
        // This will be called when the popup is about to be shown
        // We'll update the menu items based on the selected node
    }
    
    override fun show(invoker: java.awt.Component?, x: Int, y: Int) {
        removeAll() // Clear existing items
        
        val selectedPath = tree.selectionPath
        val selectedNode = selectedPath?.lastPathComponent as? ChromiaTreeNode
        
        if (selectedNode != null) {
            buildMenuForNode(selectedNode)
        }
        
        super.show(invoker, x, y)
    }
    
    private fun buildMenuForNode(node: ChromiaTreeNode) {
        when (node.nodeType) {
            ChromiaNodeType.COMMAND -> {
                buildCommandMenu(node)
            }
            ChromiaNodeType.CATEGORY -> {
                buildCategoryMenu(node)
            }
            ChromiaNodeType.PROJECT -> {
                buildProjectMenu(node)
            }
            ChromiaNodeType.ROOT -> {
                buildRootMenu()
            }
        }
    }

    private fun createMenuItem(
        text: String,
        icon: Icon? = null,
    ): JMenuItem {
        return JBMenuItem(text, icon).apply {
            border = JBUI.Borders.empty(3, 0)
        }
    }
    
    private fun buildCommandMenu(node: ChromiaTreeNode) {
        val executeItem = createMenuItem("Execute", AllIcons.Actions.Execute)
        executeItem.addActionListener {
            val fullCommand = node.getFullCommand()
            if (fullCommand != null) {
                // Use project path from node if available
                val workingDirectory = node.projectPath ?: project.basePath
                commandExecutor.executeCommand(fullCommand, workingDirectory)
            }
        }
        add(executeItem)
        
        addSeparator()

        val configureItem = createMenuItem("Configure Parameters...", AllIcons.Actions.Properties)
        configureItem.addActionListener {
            showParametersDialog(node)
        }
        add(configureItem)

        if (node.parameters.isNotBlank()) {
            val clearItem = createMenuItem("Clear Parameters", AllIcons.Actions.GC)
            clearItem.addActionListener {
                clearNodeParameters(node)
            }
            add(clearItem)
        }

        addSeparator()
        val helpItem = createMenuItem("Help", AllIcons.General.Information)
        helpItem.addActionListener {
            runHelpCommand(node)
        }
        add(helpItem)
    }

    private fun runHelpCommand(node: ChromiaTreeNode) {
        node.command?.let {
            val helpCommand = "${node.command} --help"
            val workingDirectory = node.projectPath ?: project.basePath
            commandExecutor.executeCommand(helpCommand, workingDirectory)
        }
    }

    private fun buildCategoryMenu(node: ChromiaTreeNode) {
        // Expand/Collapse
        val path = getTreePath(node)
        if (path != null) {
            if (tree.isExpanded(path)) {
                val collapseItem = JMenuItem("Collapse", AllIcons.Actions.Collapseall)
                collapseItem.addActionListener {
                    tree.collapsePath(path)
                }
                add(collapseItem)
            } else {
                val expandItem = JMenuItem("Expand", AllIcons.Actions.Expandall)
                expandItem.addActionListener {
                    tree.expandPath(path)
                }
                add(expandItem)
            }
        }
        
        addSeparator()
        
        // Clear all parameters in category
        val clearAllItem = JMenuItem("Clear All Parameters in Category", AllIcons.Actions.GC)
        clearAllItem.addActionListener {
            clearCategoryParameters(node)
        }
        add(clearAllItem)
    }
    
    private fun buildProjectMenu(node: ChromiaTreeNode) {
        val path = getTreePath(node)
        if (path != null) {
            if (tree.isExpanded(path)) {
                val collapseItem = JMenuItem("Collapse Project", AllIcons.Actions.Collapseall)
                collapseItem.addActionListener {
                    tree.collapsePath(path)
                }
                add(collapseItem)
            } else {
                val expandItem = JMenuItem("Expand Project", AllIcons.Actions.Expandall)
                expandItem.addActionListener {
                    tree.expandPath(path)
                }
                add(expandItem)
            }
        }
        
        addSeparator()

        val openDirItem = JMenuItem("Open in File Manager", AllIcons.Actions.MenuOpen)
        openDirItem.addActionListener {
            node.projectPath?.let { path ->
                try {
                    val desktop = java.awt.Desktop.getDesktop()
                    desktop.open(java.io.File(path))
                } catch (_: Exception) {
                    logger.error("Failed to open directory: $path")
                }
            }
        }
        add(openDirItem)

        val clearProjectParams = JMenuItem("Clear All Parameters in Project", AllIcons.Actions.GC)
        clearProjectParams.addActionListener {
            clearProjectParameters(node)
        }
        add(clearProjectParams)
    }
    
    private fun buildRootMenu() {
        val refreshItem = JMenuItem("Refresh", AllIcons.Actions.Refresh)
        refreshItem.addActionListener {
            treeModel.reload()
            tree.expandRow(0) // Expand root
        }
        add(refreshItem)
        
        addSeparator()

        val clearAllItem = JMenuItem("Clear All Parameters", AllIcons.Actions.GC)
        clearAllItem.addActionListener {
            clearAllParameters()
        }
        add(clearAllItem)
    }
    
    private fun showParametersDialog(node: ChromiaTreeNode) {
        val currentParameters = node.parameters
        val newParameters = Messages.showInputDialog(
            project,
            "Enter additional parameters for '${node.command}':",
            "Configure Command Parameters",
            AllIcons.Actions.Properties,
            currentParameters,
            null
        )
        
        if (newParameters != null) {
            node.parameters = newParameters
            
            // Save to settings
            if (node.command != null) {
                settings.setParameters(node.command, newParameters)
            }

            // Refresh tree display
            treeModel.nodeChanged(node)
        }
    }
    
    private fun clearNodeParameters(node: ChromiaTreeNode) {
        node.parameters = ""
        
        // Remove from settings
        if (node.command != null) {
            settings.clearParameters(node.command)
        }
        
        // Refresh tree display
        treeModel.nodeChanged(node)
    }
    
    private fun clearCategoryParameters(categoryNode: ChromiaTreeNode) {
        val result = Messages.showYesNoDialog(
            project,
            "Clear all parameters for commands in '${categoryNode.displayName}' category?",
            "Clear Category Parameters",
            Messages.getQuestionIcon()
        )
        
        if (result == Messages.YES) {
            clearParametersRecursively(categoryNode)
            treeModel.nodeStructureChanged(categoryNode)
        }
    }
    
    private fun clearProjectParameters(projectNode: ChromiaTreeNode) {
        val result = Messages.showYesNoDialog(
            project,
            "Clear all parameters for commands in '${projectNode.displayName}' project?",
            "Clear Project Parameters",
            Messages.getQuestionIcon()
        )
        
        if (result == Messages.YES) {
            clearParametersRecursively(projectNode)
            treeModel.nodeStructureChanged(projectNode)
        }
    }
    
    private fun clearAllParameters() {
        val result = Messages.showYesNoDialog(
            project,
            "Clear all command parameters?",
            "Clear All Parameters",
            Messages.getQuestionIcon()
        )
        
        if (result == Messages.YES) {
            settings.clearAllParameters()
            clearParametersRecursively(treeModel.root as ChromiaTreeNode)
            treeModel.reload()
        }
    }
    
    private fun clearParametersRecursively(node: ChromiaTreeNode) {
        if (node.nodeType == ChromiaNodeType.COMMAND) {
            node.parameters = ""
        }
        
        for (i in 0 until node.childCount) {
            val child = node.getChildAt(i) as ChromiaTreeNode
            clearParametersRecursively(child)
        }
    }
    
    private fun getTreePath(node: ChromiaTreeNode): TreePath? {
        val path = mutableListOf<ChromiaTreeNode>()
        var current: ChromiaTreeNode? = node
        
        while (current != null) {
            path.add(0, current)
            current = current.parent as? ChromiaTreeNode
        }
        
        return if (path.isNotEmpty()) TreePath(path.toTypedArray()) else null
    }
}