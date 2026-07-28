package net.postchain.rellide.jetbrains.toolwindow.tree

import javax.swing.Icon
import javax.swing.tree.DefaultMutableTreeNode

/**
 * Represents a node in the Chromia tool window tree.
 * Each node can represent either a category or a command.
 */
data class ChromiaTreeNode(
        val displayName: String,
        val nodeType: ChromiaNodeType,
        val command: String? = null,
        var parameters: String = "",
        val description: String? = null,
        val icon: Icon? = null,
        val projectPath: String? = null // Path to the project directory for PROJECT nodes
) : DefaultMutableTreeNode(displayName) {
    
    /**
     * Returns the full command to execute including parameters
     */
    fun getFullCommand(): String? {
        return command?.let { cmd ->
            if (parameters.isNotBlank()) {
                "$cmd $parameters"
            } else {
                cmd
            }
        }
    }
    
    /**
     * Returns display text with parameters if any
     */
    fun getDisplayText(): String {
        return if (parameters.isNotBlank() && nodeType == ChromiaNodeType.COMMAND) {
            "$displayName ($parameters)"
        } else {
            displayName
        }
    }
    
    override fun toString(): String = getDisplayText()
}

enum class ChromiaNodeType {
    ROOT,
    PROJECT,
    CATEGORY,
    COMMAND
}