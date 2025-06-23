package net.postchain.rellide.jetbrains.testing.ui

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionUtil
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import net.postchain.rellide.jetbrains.testing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

/**
 * Tool window for Rell test runner.
 * Provides a UI for discovering, running, and managing Rell tests.
 */
class RellTestRunnerToolWindow(private val project: Project) : SimpleToolWindowPanel(true, true) {
    
    private val tree: Tree
    private val treeModel: DefaultTreeModel
    private val rootNode: DefaultMutableTreeNode
    
    init {
        rootNode = DefaultMutableTreeNode("Rell Tests")
        treeModel = DefaultTreeModel(rootNode)
        tree = Tree(treeModel)
        
        setupTree()
        setupToolbar()
        refreshTestTree()
    }
    
    private fun setupTree() {
        tree.isRootVisible = true
        tree.showsRootHandles = true
        tree.cellRenderer = RellTestTreeCellRenderer()
        
        // Add double-click listener to run tests
        tree.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent) {
                if (e.clickCount == 2) {
                    val path = tree.getPathForLocation(e.x, e.y)
                    if (path != null) {
                        val node = path.lastPathComponent as DefaultMutableTreeNode
                        val userObject = node.userObject
                        if (userObject is RellTestNode) {
                            runTest(userObject)
                        }
                    }
                }
            }
        })
        
        setContent(JBScrollPane(tree))
    }
    
    private fun setupToolbar() {
        val actionGroup = DefaultActionGroup()
        
        // Run all tests action
        actionGroup.add(object : AnAction("Run All Tests", "Run all Rell tests in the project", AllIcons.RunConfigurations.TestState.Run) {
            override fun actionPerformed(e: AnActionEvent) {
                runAllTests()
            }
        })
        
        // Refresh tests action
        actionGroup.add(object : AnAction("Refresh Tests", "Refresh the test tree", AllIcons.Actions.Refresh) {
            override fun actionPerformed(e: AnActionEvent) {
                refreshTestTree()
            }
        })
        
        // Separator
        actionGroup.addSeparator()
        
        // Configure test runner action
        actionGroup.add(object : AnAction("Configure Test Runner", "Configure Rell test runner settings", AllIcons.General.Settings) {
            override fun actionPerformed(e: AnActionEvent) {
                // Open test runner configuration dialog
                // This could be implemented as a separate settings dialog
            }
        })
        
        val toolbar = ActionManager.getInstance().createActionToolbar(
            "RellTestRunner", 
            actionGroup, 
            true
        )
        
        toolbar.targetComponent = this
        setToolbar(toolbar.component)
    }
    
    private fun refreshTestTree() {
        rootNode.removeAllChildren()
        
        val testFiles = findTestFiles()
        
        for (testFile in testFiles) {
            val fileNode = DefaultMutableTreeNode(RellTestFileNode(testFile))
            rootNode.add(fileNode)
            
            // Add individual test functions if we can parse them
            val testFunctions = findTestFunctions(testFile)
            for (testFunction in testFunctions) {
                val functionNode = DefaultMutableTreeNode(RellTestFunctionNode(testFile, testFunction))
                fileNode.add(functionNode)
            }
        }
        
        treeModel.reload()
        
        // Expand all nodes
        for (i in 0 until tree.rowCount) {
            tree.expandRow(i)
        }
    }
    
    private fun findTestFiles(): List<VirtualFile> {
        val testFiles = mutableListOf<VirtualFile>()
        
        // Search for .rell files that contain tests
        val projectBasePath = project.basePath ?: return testFiles
        val baseDir = com.intellij.openapi.vfs.LocalFileSystem.getInstance().findFileByPath(projectBasePath)
        
        baseDir?.let { dir ->
            findTestFilesRecursively(dir, testFiles)
        }
        
        return testFiles
    }
    
    private fun findTestFilesRecursively(dir: VirtualFile, testFiles: MutableList<VirtualFile>) {
        for (child in dir.children) {
            if (child.isDirectory) {
                findTestFilesRecursively(child, testFiles)
            } else if (child.extension == "rell" && isTestFile(child)) {
                testFiles.add(child)
            }
        }
    }
    
    private fun isTestFile(file: VirtualFile): Boolean {
        val fileName = file.nameWithoutExtension.lowercase()
        return fileName.contains("test") || fileName.endsWith("_test") || fileName.startsWith("test_")
    }
    
    private fun findTestFunctions(file: VirtualFile): List<String> {
        // This is a simplified implementation
        // In a real implementation, you would parse the Rell file to find test functions
        val functions = mutableListOf<String>()
        
        try {
            val content = String(file.contentsToByteArray())
            val lines = content.lines()
            
            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.startsWith("function") && trimmed.contains("test")) {
                    // Extract function name
                    val functionName = extractFunctionName(trimmed)
                    if (functionName != null) {
                        functions.add(functionName)
                    }
                }
            }
        } catch (e: Exception) {
            // Handle parsing errors
        }
        
        return functions
    }
    
    private fun extractFunctionName(line: String): String? {
        // Simple regex to extract function name
        val regex = Regex("function\\s+(\\w+)\\s*\\(")
        val match = regex.find(line)
        return match?.groups?.get(1)?.value
    }
    
    private fun runTest(testNode: RellTestNode) {
        val runManager = RunManager.getInstance(project)
        val configurationSettings = createTestConfiguration(testNode, runManager)
        
        runManager.selectedConfiguration = configurationSettings
        ExecutionUtil.runConfiguration(configurationSettings, DefaultRunExecutor.getRunExecutorInstance())
    }
    
    private fun runAllTests() {
        val runManager = RunManager.getInstance(project)
        val factory = RellTestConfigurationFactory.getInstance()
        val configurationSettings = runManager.createConfiguration("Run All Rell Tests", factory)
        val configuration = configurationSettings.configuration as RellTestRunConfiguration
        
        val options = configuration.options
        options.setTestScope(TestScope.ALL_IN_PROJECT)
        options.setWorkingDirectory(project.basePath)
        
        runManager.selectedConfiguration = configurationSettings
        ExecutionUtil.runConfiguration(configurationSettings, DefaultRunExecutor.getRunExecutorInstance())
    }
    
    private fun createTestConfiguration(testNode: RellTestNode, runManager: RunManager): RunnerAndConfigurationSettings {
        val factory = RellTestConfigurationFactory.getInstance()
        val configurationName = when (testNode) {
            is RellTestFileNode -> "Run ${testNode.file.nameWithoutExtension}"
            is RellTestFunctionNode -> "Run ${testNode.functionName}"
        }
        
        val configurationSettings = runManager.createConfiguration(configurationName, factory)
        val configuration = configurationSettings.configuration as RellTestRunConfiguration
        
        val options = configuration.options
        when (testNode) {
            is RellTestFileNode -> {
                options.setTestScope(TestScope.MODULE)
                options.setTestModule(testNode.file.path)
            }
            is RellTestFunctionNode -> {
                options.setTestScope(TestScope.MODULE)
                options.setTestModule(testNode.file.path)
                options.setAdditionalArguments("--test-function ${testNode.functionName}")
            }
        }
        
        options.setWorkingDirectory(project.basePath)
        
        return configurationSettings
    }
}

/**
 * Base class for test tree nodes
 */
sealed class RellTestNode

/**
 * Tree node representing a test file
 */
data class RellTestFileNode(val file: VirtualFile) : RellTestNode() {
    override fun toString(): String = file.nameWithoutExtension
}

/**
 * Tree node representing a test function
 */
data class RellTestFunctionNode(val file: VirtualFile, val functionName: String) : RellTestNode() {
    override fun toString(): String = functionName
}

/**
 * Custom tree cell renderer for test nodes
 */
private class RellTestTreeCellRenderer : ColoredTreeCellRenderer() {
    override fun customizeCellRenderer(
        tree: javax.swing.JTree,
        value: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ) {
        val node = value as? DefaultMutableTreeNode
        val userObject = node?.userObject
        
        when (userObject) {
            is RellTestFileNode -> {
                icon = AllIcons.FileTypes.Any_type
                append(userObject.toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES)
            }
            is RellTestFunctionNode -> {
                icon = AllIcons.Nodes.Method
                append(userObject.toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES)
            }
            else -> {
                icon = AllIcons.Nodes.Folder
                append(userObject?.toString() ?: "", SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
            }
        }
    }
} 