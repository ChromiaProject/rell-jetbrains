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
        
        // Demo action to simulate test results (for development/testing)
        actionGroup.add(object : AnAction("Demo Test Results", "Simulate test execution results for UI testing", AllIcons.RunConfigurations.TestState.Green2) {
            override fun actionPerformed(e: AnActionEvent) {
                demonstrateTestResults()
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
        // Mark test as running before execution
        testNode.updateState(TestState.RUNNING)
        refreshTreeDisplay()
        
        val runManager = RunManager.getInstance(project)
        val configurationSettings = createTestConfiguration(testNode, runManager)
        
        runManager.selectedConfiguration = configurationSettings
        ExecutionUtil.runConfiguration(configurationSettings, DefaultRunExecutor.getRunExecutorInstance())
    }
    
    /**
     * Update the state of a test node and refresh the UI
     */
    fun updateTestState(testIdentifier: String, state: TestState, executionTime: Long? = null, errorMessage: String? = null) {
        val testNode = findTestNode(testIdentifier)
        testNode?.let { node ->
            node.updateState(state, executionTime, errorMessage)
            refreshTreeDisplay()
        }
    }
    
    /**
     * Find a test node by identifier (file path or file:function format)
     */
    private fun findTestNode(identifier: String): RellTestNode? {
        return findTestNodeRecursively(rootNode, identifier)
    }
    
    private fun findTestNodeRecursively(node: DefaultMutableTreeNode, identifier: String): RellTestNode? {
        val userObject = node.userObject
        if (userObject is RellTestNode) {
            val nodeIdentifier = when (userObject) {
                is RellTestFileNode -> userObject.file.path
                is RellTestFunctionNode -> "${userObject.file.path}:${userObject.functionName}"
            }
            if (nodeIdentifier == identifier) {
                return userObject
            }
        }
        
        // Search children
        for (i in 0 until node.childCount) {
            val child = node.getChildAt(i) as DefaultMutableTreeNode
            val result = findTestNodeRecursively(child, identifier)
            if (result != null) {
                return result
            }
        }
        
        return null
    }
    
    /**
     * Refresh the tree display without rebuilding the entire tree
     */
    private fun refreshTreeDisplay() {
        javax.swing.SwingUtilities.invokeLater {
            treeModel.reload()
        }
    }
    
    private fun runAllTests() {
        // Mark all tests as running
        markAllTestsAsRunning()
        
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
    
    /**
     * Mark all test nodes as running
     */
    private fun markAllTestsAsRunning() {
        markTestNodesAsRunningRecursively(rootNode)
        refreshTreeDisplay()
    }
    
    private fun markTestNodesAsRunningRecursively(node: DefaultMutableTreeNode) {
        val userObject = node.userObject
        if (userObject is RellTestNode) {
            userObject.updateState(TestState.RUNNING)
        }
        
        for (i in 0 until node.childCount) {
            val child = node.getChildAt(i) as DefaultMutableTreeNode
            markTestNodesAsRunningRecursively(child)
        }
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
    
    /**
     * Demonstrate test results for UI testing
     */
    private fun demonstrateTestResults() {
        demonstrateTestResultsRecursively(rootNode, 0)
        refreshTreeDisplay()
    }
    
    private fun demonstrateTestResultsRecursively(node: DefaultMutableTreeNode, depth: Int) {
        val userObject = node.userObject
        if (userObject is RellTestNode) {
            // Simulate different test states for demonstration
            val states = listOf(TestState.PASSED, TestState.FAILED, TestState.IGNORED)
            val randomState = states.random()
            val executionTime = (10..500).random().toLong()
            val errorMessage = if (randomState == TestState.FAILED) "Assertion failed: expected 4 but was 5" else null
            
            userObject.updateState(randomState, executionTime, errorMessage)
        }
        
        for (i in 0 until node.childCount) {
            val child = node.getChildAt(i) as DefaultMutableTreeNode
            demonstrateTestResultsRecursively(child, depth + 1)
        }
    }
}

/**
 * Test execution state
 */
enum class TestState {
    NOT_RUN,    // Initial state, test hasn't been executed
    RUNNING,    // Test is currently executing
    PASSED,     // Test completed successfully
    FAILED,     // Test failed
    IGNORED     // Test was ignored/skipped
}

/**
 * Base class for test tree nodes
 */
sealed class RellTestNode {
    var state: TestState = TestState.NOT_RUN
        private set
    
    var executionTime: Long? = null
        private set
    
    var errorMessage: String? = null
        private set
    
    fun updateState(newState: TestState, executionTime: Long? = null, errorMessage: String? = null) {
        this.state = newState
        this.executionTime = executionTime
        this.errorMessage = errorMessage
    }
}

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
                icon = getTestStateIcon(userObject.state)
                append(userObject.toString(), getTestStateAttributes(userObject.state))
                
                // Add execution time if available
                userObject.executionTime?.let { time ->
                    append(" (${time}ms)", SimpleTextAttributes.GRAY_ATTRIBUTES)
                }
            }
            is RellTestFunctionNode -> {
                icon = getTestStateIcon(userObject.state)
                append(userObject.toString(), getTestStateAttributes(userObject.state))
                
                // Add execution time if available
                userObject.executionTime?.let { time ->
                    append(" (${time}ms)", SimpleTextAttributes.GRAY_ATTRIBUTES)
                }
                
                // Show error message as tooltip for failed tests
                userObject.errorMessage?.let { error ->
                    toolTipText = error
                }
            }
            else -> {
                icon = AllIcons.Nodes.Folder
                append(userObject?.toString() ?: "", SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
            }
        }
    }
    
    private fun getTestStateIcon(state: TestState) = when (state) {
        TestState.NOT_RUN -> AllIcons.RunConfigurations.TestState.Run
        TestState.RUNNING -> AllIcons.RunConfigurations.TestState.Run_run
        TestState.PASSED -> AllIcons.RunConfigurations.TestState.Green2
        TestState.FAILED -> AllIcons.RunConfigurations.TestState.Red2
        TestState.IGNORED -> AllIcons.RunConfigurations.TestState.Yellow2
    }
    
    private fun getTestStateAttributes(state: TestState) = when (state) {
        TestState.NOT_RUN -> SimpleTextAttributes.REGULAR_ATTRIBUTES
        TestState.RUNNING -> SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES
        TestState.PASSED -> SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, java.awt.Color.GREEN.darker())
        TestState.FAILED -> SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, java.awt.Color.RED.darker())
        TestState.IGNORED -> SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, java.awt.Color.ORANGE.darker())
    }
} 