package net.postchain.rellide.jetbrains.testing

import com.intellij.execution.testframework.AbstractTestProxy
import com.intellij.execution.testframework.TestStatusListener
import com.intellij.execution.testframework.sm.runner.SMTestProxy
import com.intellij.openapi.project.Project

class RellTestStatusListener : TestStatusListener() {
    override fun testSuiteFinished(root: AbstractTestProxy?) {
        // Nothing
    }

    override fun testSuiteFinished(root: AbstractTestProxy?, project: Project?) {

        if (root == null
                || (root is SMTestProxy.SMRootTestProxy
                        && root.testConsoleProperties?.configuration?.name?.contains("(run by NGM)") == false)) {
            return
        }

//        val testRunResults = TestResultParseUtils.parseTestResult(root as SMRootTestProxy)
//
//        val testRunnerDataHolder = project?.getService(TestRunnerDataHolder::class.java) ?: return
//        val defaultMutableTreeNode = testRunnerDataHolder.treeModel.root as DefaultMutableTreeNode
//        val targetNodes = when (val userObject = defaultMutableTreeNode.userObject) {
//            is String -> defaultMutableTreeNode.children().asSequence().map { (it as DefaultMutableTreeNode).userObject as BaseNodeDescriptor }.toList()
//            is BaseNodeDescriptor -> listOf(userObject)
//            else -> null
//        }
//
//        targetNodes?.forEach { targetNode ->
//            val visitor = ChangingTestMethodIconVisitor(testRunResults)
//            targetNode.accept(visitor)
//
//            targetNode.applyTestResult()
//        }
//
//        testRunnerDataHolder.treeModel.reload()
//        ToolWindowUtils.activateNgmTestRunnerToolWindow(project)
    }
}