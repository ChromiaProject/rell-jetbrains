package net.postchain.rellide.jetbrains.toolwindow.tree

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import net.postchain.rellide.jetbrains.language.RellIcons
import net.postchain.rellide.jetbrains.toolwindow.settings.ChromiaToolWindowSettings
import net.postchain.rellide.jetbrains.toolwindow.project.ChromiaProjectDiscovery
import javax.swing.tree.DefaultTreeModel

/**
 * Tree model for the Chromia tool window.
 * Manages the hierarchical structure of Chromia CLI commands.
 */
class ChromiaTreeModel(private val project: Project) : DefaultTreeModel(createRoot()) {

    private val settings = ChromiaToolWindowSettings.getInstance(project)
    var hasProjects: Boolean = false

    companion object {
        private fun createRoot(): ChromiaTreeNode {
            return ChromiaTreeNode(
                    displayName = "Chromia",
                    nodeType = ChromiaNodeType.ROOT,
                    icon = RellIcons.CHROMIA_ICON_FILE
            )
        }
    }

    init {
        buildTree()
    }

    private fun buildTree() {
        val root = root as ChromiaTreeNode
        root.removeAllChildren()

        val discoveredProjects = ChromiaProjectDiscovery.discoverProjects(project)
        hasProjects = discoveredProjects.isNotEmpty()

        if (discoveredProjects.isEmpty()) {
            val noProjectsNode = ChromiaTreeNode(
                    displayName = "No Chromia projects found",
                    nodeType = ChromiaNodeType.CATEGORY,
                    icon = AllIcons.General.Information
            )
            root.add(noProjectsNode)
        } else {
            for (rellProject in discoveredProjects) {
                val projectNode = createProjectNode(rellProject)
                root.add(projectNode)
            }
        }
        loadParametersFromSettings()
        reload()
    }

    /**
     * Create a project node with its command categories
     */
    private fun createProjectNode(chromiaProject: ChromiaProjectDiscovery.ChromiaProject): ChromiaTreeNode {
        val projectNode = ChromiaTreeNode(
                displayName = chromiaProject.name,
                nodeType = ChromiaNodeType.PROJECT,
                icon = AllIcons.Nodes.Module,
                projectPath = chromiaProject.path,
                description = "Chromia project at ${chromiaProject.path}"
        )

        val replCommand = createCommandNode(
                "REPL",
                "chr repl",
                "Start the Rell REPL (Read-Eval-Print Loop)",
                AllIcons.Nodes.Console,
                chromiaProject.path
        )
        projectNode.add(replCommand)


        val buildCommand = createCommandNode(
                "Build",
                "chr build",
                "Build the Rell project",
                AllIcons.Actions.Compile,
                chromiaProject.path
        )
        projectNode.add(buildCommand)

        val installCommand = createCommandNode(
                "Install",
                "chr install",
                "Install project dependencies",
                AllIcons.Actions.Install,
                chromiaProject.path
        )
        projectNode.add(installCommand)

        val keygenCommand = createCommandNode(
                "Keygen",
                "chr keygen",
                "Generate keys for the Chromia project",
                AllIcons.Nodes.SecurityRole,
                chromiaProject.path
        )
        projectNode.add(keygenCommand)


        val testCommand = createCommandNode(
                "Test",
                "chr test",
                "Run tests for the Chromia project",
                AllIcons.Scope.Tests,
                chromiaProject.path
        )
        projectNode.add(testCommand)

        val nodeCategory = ChromiaTreeNode(
                displayName = "Node",
                nodeType = ChromiaNodeType.CATEGORY,
                icon = AllIcons.Webreferences.Server
        )

        nodeCategory.add(createCommandNode(
                "Start", "chr node start",
                "Start the Chromia node",
                AllIcons.Actions.RunAnything,
                chromiaProject.path
        ))

        nodeCategory.add(createCommandNode(
                "Update", "chr node update",
                "Update the Chromia node",
                AllIcons.Actions.Restart,
                chromiaProject.path
        ))
        projectNode.add(nodeCategory)


        val generateCategory = ChromiaTreeNode(
                displayName = "Generate",
                nodeType = ChromiaNodeType.CATEGORY,
                icon = AllIcons.Actions.GeneratedFolder
        )

        generateCategory.add(createCommandNode(
                "Client Stubs", "chr generate client-stubs",
                "Generate client stubs",
                AllIcons.FileTypes.Xml,
                chromiaProject.path
        ))

        generateCategory.add(createCommandNode(
                "Docs site", "chr generate docs-site",
                "Generate the documentation site",
                AllIcons.Toolwindows.Documentation,
                chromiaProject.path
        ))

        generateCategory.add(createCommandNode(
                "Graph", "chr generate graph",
                "Generate the entity relation graph",
                AllIcons.Graph.Layout,
                chromiaProject.path
        ))

        projectNode.add(generateCategory)


        val seederCategory = ChromiaTreeNode(
                displayName = "Seeder",
                nodeType = ChromiaNodeType.CATEGORY,
                icon = AllIcons.Nodes.DataTables
        )

        seederCategory.add(createCommandNode(
                "Init", "chr seeder init",
                "Initialize the seeder",
                AllIcons.Ide.ConfigFile,
                chromiaProject.path
        ))

        seederCategory.add(createCommandNode(
                "Generate", "chr seeder generate",
                "Generate seed data",
                AllIcons.Actions.RunAnything,
                chromiaProject.path
        ))
        projectNode.add(seederCategory)

        val multiSignatureCategory = ChromiaTreeNode(
                displayName = "Multi-Signature",
                nodeType = ChromiaNodeType.CATEGORY,
                icon = AllIcons.Actions.CheckMulticaret,
        )

        multiSignatureCategory.add(createCommandNode(
                "Create", "chr multi-signature create",
                "Creates a new transaction for multi signature and signs it with your key",
                AllIcons.Actions.AddMulticaret,
                chromiaProject.path
        ))

        multiSignatureCategory.add(createCommandNode(
                "Sign", "chr multi-signature sign",
                "Sign a existing transaction with your key",
                AllIcons.Actions.EditScheme,
                chromiaProject.path
        ))

        multiSignatureCategory.add(createCommandNode(
                "Send", "chr multi-signature send",
                "Send a fully signed transaction",
                AllIcons.Nodes.WriteAccess,
                chromiaProject.path
        ))

        multiSignatureCategory.add(createCommandNode(
                "View", "chr multi-signature view",
                "View a existing transaction",
                AllIcons.Actions.ToggleVisibility,
                chromiaProject.path
        ))

        projectNode.add(multiSignatureCategory)

        return projectNode
    }

    private fun createCommandNode(name: String, command: String, description: String, icon: javax.swing.Icon, projectPath: String? = null): ChromiaTreeNode {
        return ChromiaTreeNode(
                displayName = name,
                nodeType = ChromiaNodeType.COMMAND,
                command = command,
                description = description,
                icon = icon,
                projectPath = projectPath
        )
    }

    private fun loadParametersFromSettings() {
        fun loadNodeParameters(node: ChromiaTreeNode) {
            if (node.nodeType == ChromiaNodeType.COMMAND && node.command != null) {
                node.parameters = settings.getParameters(node.command)
            }

            for (i in 0 until node.childCount) {
                val child = node.getChildAt(i) as ChromiaTreeNode
                loadNodeParameters(child)
            }
        }

        loadNodeParameters(root as ChromiaTreeNode)
    }

    /**
     * Save parameters for all command nodes to settings
     */
    fun saveParametersToSettings() {
        val commandParameters = mutableMapOf<String, String>()

        fun collectParameters(node: ChromiaTreeNode) {
            if (node.nodeType == ChromiaNodeType.COMMAND && node.command != null) {
                commandParameters[node.command] = node.parameters
            }

            for (i in 0 until node.childCount) {
                val child = node.getChildAt(i) as ChromiaTreeNode
                collectParameters(child)
            }
        }

        collectParameters(root as ChromiaTreeNode)
        settings.commandParameters = commandParameters
    }

    /**
     * Find a command node by its command string
     */
    fun findCommandNode(command: String): ChromiaTreeNode? {
        fun searchNode(node: ChromiaTreeNode): ChromiaTreeNode? {
            if (node.nodeType == ChromiaNodeType.COMMAND && node.command == command) {
                return node
            }

            for (i in 0 until node.childCount) {
                val child = node.getChildAt(i) as ChromiaTreeNode
                val found = searchNode(child)
                if (found != null) return found
            }

            return null
        }

        return searchNode(root as ChromiaTreeNode)
    }
}