package net.postchain.rellide.jetbrains.toolwindow.tree

import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.ui.SimpleTextAttributes
import javax.swing.JTree

/**
 * Custom cell renderer for the Chromia tree.
 * Displays custom icons and formatting for different node types.
 */
class ChromiaTreeCellRenderer : ColoredTreeCellRenderer() {

    override fun customizeCellRenderer(
        tree: JTree,
        value: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean,
    ) {
        if (value is ChromiaTreeNode) {
            icon = value.icon

            // Set text and attributes based on node type
            when (value.nodeType) {
                ChromiaNodeType.ROOT -> {
                    append(value.displayName, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
                }

                ChromiaNodeType.PROJECT -> {
                    append(value.displayName, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)

                    // Show the active settings file when it is not the default chromia.yml
                    value.settingsFile?.let {
                        append(" [$it]", SimpleTextAttributes.GRAYED_ATTRIBUTES)
                    }

                    // Show project path on tooltip
                    toolTipText = value.description
                }

                ChromiaNodeType.SETTINGS_FILE -> {
                    append(value.displayName, SimpleTextAttributes.REGULAR_ATTRIBUTES)
                    if (value.isActiveSettingsFile) {
                        append(" (active)", SimpleTextAttributes.GRAYED_BOLD_ATTRIBUTES)
                    }
                    toolTipText = value.description
                }

                ChromiaNodeType.CATEGORY -> {
                    append(value.displayName, SimpleTextAttributes.REGULAR_ATTRIBUTES)
                }

                ChromiaNodeType.WARNING -> {
                    append(value.displayName, SimpleTextAttributes.ERROR_ATTRIBUTES)
                    toolTipText = value.description
                }

                ChromiaNodeType.COMMAND -> {
                    append(value.displayName, SimpleTextAttributes.REGULAR_ATTRIBUTES)

                    // Show parameters if any
                    if (value.parameters.isNotBlank()) {
                        append(" (", SimpleTextAttributes.GRAYED_ATTRIBUTES)
                        append(value.parameters, SimpleTextAttributes.GRAYED_SMALL_ATTRIBUTES)
                        append(")", SimpleTextAttributes.GRAYED_ATTRIBUTES)
                    }

                    // Show description on tooltip
                    toolTipText = value.description ?: value.getFullCommand()
                }
            }
        } else {
            // Fallback for non-ChromiaTreeNode values
            append(value?.toString() ?: "", SimpleTextAttributes.REGULAR_ATTRIBUTES)
        }
    }
}