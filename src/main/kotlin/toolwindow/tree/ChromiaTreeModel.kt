package net.postchain.rellide.jetbrains.toolwindow.tree

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import net.postchain.rellide.jetbrains.chromia.ChromiaSettingsFiles
import net.postchain.rellide.jetbrains.language.RellIcons
import net.postchain.rellide.jetbrains.settings.ChrVersionService
import net.postchain.rellide.jetbrains.toolwindow.project.ChromiaProjectDiscovery
import net.postchain.rellide.jetbrains.toolwindow.settings.ChromiaToolWindowSettings
import javax.swing.tree.DefaultTreeModel

/**
 * Tree model for the Chromia tool window.
 * Manages the hierarchical structure of Chromia CLI commands.
 */
class ChromiaTreeModel(private val project: Project) : DefaultTreeModel(createRoot()) {

    private val settings = ChromiaToolWindowSettings.getInstance(project)

    companion object {
        private fun createRoot(): ChromiaTreeNode = ChromiaTreeNode(
            displayName = "Chromia",
            nodeType = ChromiaNodeType.ROOT,
            icon = RellIcons.CHROMIA_ICON_FILE
        )

        /** Commands whose `chr` subcommand accepts `-s/--settings` (keygen and some others do not). */
        private val SETTINGS_AWARE_COMMANDS = setOf(
            "chr repl", "chr build", "chr install", "chr test",
            "chr node start", "chr node update",
            "chr generate client-stubs", "chr generate docs-site", "chr generate graph",
            "chr seeder init", "chr seeder generate",
            "chr multi-signature create", "chr multi-signature send",
        )
    }

    init {
        rebuild()
    }

    /** Re-runs project discovery and rebuilds the whole tree. */
    fun rebuild() {
        val root = root as ChromiaTreeNode
        root.removeAllChildren()

        val discoveredProjects = ChromiaProjectDiscovery.discoverProjects(project)

        if (discoveredProjects.isEmpty()) {
            root.add(
                ChromiaTreeNode(
                    displayName = "No Chromia projects found",
                    nodeType = ChromiaNodeType.CATEGORY,
                    icon = AllIcons.General.Information
                )
            )
        } else {
            for (rellProject in discoveredProjects) {
                root.add(createProjectNode(rellProject))
            }
        }

        loadParametersFromSettings()
        reload()
    }

    /**
     * Create a project node with its settings files and command categories
     */
    private fun createProjectNode(chromiaProject: ChromiaProjectDiscovery.ChromiaProject): ChromiaTreeNode {
        // Only a non-default active file needs surfacing and a --settings argument; chr reads
        // chromia.yml on its own.
        val alternateSettings = chromiaProject.activeSettingsFile
            ?.takeUnless { ChromiaSettingsFiles.isDefaultName(it) }

        val projectNode = ChromiaTreeNode(
            displayName = chromiaProject.name,
            nodeType = ChromiaNodeType.PROJECT,
            icon = AllIcons.Nodes.Module,
            projectPath = chromiaProject.path,
            description = "Chromia project at ${chromiaProject.path}" +
                    (chromiaProject.activeSettingsFile?.let { ", active settings file: $it" } ?: "")
        )
        projectNode.settingsFile = alternateSettings

        chrVersionWarning(chromiaProject)?.let { projectNode.add(it) }

        if (chromiaProject.settingsFiles.size > 1) {
            val settingsCategory = ChromiaTreeNode(
                displayName = "Settings Files",
                nodeType = ChromiaNodeType.CATEGORY,
                icon = AllIcons.Nodes.ConfigFolder
            )

            for (name in chromiaProject.settingsFiles) {
                settingsCategory.add(
                    ChromiaTreeNode(
                        displayName = name,
                        nodeType = ChromiaNodeType.SETTINGS_FILE,
                        description = "Chromia settings file. The active one decides the Rell version " +
                                "and the --settings argument of project commands",
                        icon = RellIcons.CHROMIA_ICON_FILE,
                        projectPath = chromiaProject.path,
                    ).apply {
                        settingsFile = name
                        isActiveSettingsFile = name == chromiaProject.activeSettingsFile
                    }
                )
            }

            projectNode.add(settingsCategory)
        }

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

        nodeCategory.add(
            createCommandNode(
                "Start", "chr node start",
                "Start the Chromia node",
                AllIcons.Actions.RunAnything,
                chromiaProject.path
            )
        )

        nodeCategory.add(
            createCommandNode(
                "Update", "chr node update",
                "Update the Chromia node",
                AllIcons.Actions.Restart,
                chromiaProject.path
            )
        )

        projectNode.add(nodeCategory)


        val generateCategory = ChromiaTreeNode(
            displayName = "Generate",
            nodeType = ChromiaNodeType.CATEGORY,
            icon = AllIcons.Actions.GeneratedFolder
        )

        generateCategory.add(
            createCommandNode(
                "Client Stubs", "chr generate client-stubs",
                "Generate client stubs",
                AllIcons.FileTypes.Xml,
                chromiaProject.path
            )
        )

        generateCategory.add(
            createCommandNode(
                "Docs site", "chr generate docs-site",
                "Generate the documentation site",
                AllIcons.Toolwindows.Documentation,
                chromiaProject.path
            )
        )

        generateCategory.add(
            createCommandNode(
                "Graph", "chr generate graph",
                "Generate the entity relation graph",
                AllIcons.Graph.Layout,
                chromiaProject.path
            )
        )

        projectNode.add(generateCategory)


        val seederCategory = ChromiaTreeNode(
            displayName = "Seeder",
            nodeType = ChromiaNodeType.CATEGORY,
            icon = AllIcons.Nodes.DataTables
        )

        seederCategory.add(
            createCommandNode(
                "Init", "chr seeder init",
                "Initialize the seeder",
                AllIcons.Ide.ConfigFile,
                chromiaProject.path
            )
        )

        seederCategory.add(
            createCommandNode(
                "Generate", "chr seeder generate",
                "Generate seed data",
                AllIcons.Actions.RunAnything,
                chromiaProject.path
            )
        )
        projectNode.add(seederCategory)

        val multiSignatureCategory = ChromiaTreeNode(
            displayName = "Multi-Signature",
            nodeType = ChromiaNodeType.CATEGORY,
            icon = AllIcons.Actions.CheckMulticaret,
        )

        multiSignatureCategory.add(
            createCommandNode(
                "Create", "chr multi-signature create",
                "Creates a new transaction for multi signature and signs it with your key",
                AllIcons.Actions.AddMulticaret,
                chromiaProject.path
            )
        )

        multiSignatureCategory.add(
            createCommandNode(
                "Sign", "chr multi-signature sign",
                "Sign a existing transaction with your key",
                AllIcons.Actions.EditScheme,
                chromiaProject.path
            )
        )

        multiSignatureCategory.add(
            createCommandNode(
                "Send", "chr multi-signature send",
                "Send a fully signed transaction",
                AllIcons.Nodes.WriteAccess,
                chromiaProject.path
            )
        )

        multiSignatureCategory.add(
            createCommandNode(
                "View", "chr multi-signature view",
                "View a existing transaction",
                AllIcons.Actions.ToggleVisibility,
                chromiaProject.path
            )
        )

        projectNode.add(multiSignatureCategory)

        if (alternateSettings != null) {
            applySettingsFile(projectNode, alternateSettings)
        }

        return projectNode
    }

    /**
     * A warning node when the Chromia CLI supports a lower Rell version than the active settings
     * file declares. Null when either side is unknown — no parseable `compile.rellVersion`, or the
     * CLI's version not probed yet (asking [ChrVersionService] starts a background probe that
     * refreshes this tree once it delivers).
     */
    private fun chrVersionWarning(chromiaProject: ChromiaProjectDiscovery.ChromiaProject): ChromiaTreeNode? {
        val declared = chromiaProject.activeDeclaredVersion ?: return null
        val chrMax = ChrVersionService.getInstance().maxRellVersion() ?: return null
        if (declared <= chrMax) return null
        val settingsFileName = chromiaProject.activeSettingsFile ?: ChromiaSettingsFiles.CHROMIA_YML
        return ChromiaTreeNode(
            displayName = "chr supports Rell up to $chrMax, but $settingsFileName declares $declared",
            nodeType = ChromiaNodeType.WARNING,
            icon = AllIcons.General.Warning,
            description = "The configured Chromia CLI reported Rell $chrMax as its maximal version, " +
                    "so commands run against $settingsFileName (compile.rellVersion: $declared) may fail. " +
                    "Update the Chromia CLI or lower compile.rellVersion.",
            projectPath = chromiaProject.path,
        )
    }

    /** Stamps the active non-default settings file on every command that accepts `--settings`. */
    private fun applySettingsFile(node: ChromiaTreeNode, settingsFileName: String) {
        node.selfAndDescendants()
            .filter { it.nodeType == ChromiaNodeType.COMMAND && it.command in SETTINGS_AWARE_COMMANDS }
            .forEach { it.settingsFile = settingsFileName }
    }

    private fun createCommandNode(
        name: String,
        command: String,
        description: String,
        icon: javax.swing.Icon,
        projectPath: String? = null,
    ): ChromiaTreeNode = ChromiaTreeNode(
        displayName = name,
        nodeType = ChromiaNodeType.COMMAND,
        command = command,
        description = description,
        icon = icon,
        projectPath = projectPath
    )

    private fun loadParametersFromSettings() {
        (root as ChromiaTreeNode).selfAndDescendants()
            .filter { it.nodeType == ChromiaNodeType.COMMAND }
            .forEach { node -> node.command?.let { node.parameters = settings.getParameters(it) } }
    }
}
