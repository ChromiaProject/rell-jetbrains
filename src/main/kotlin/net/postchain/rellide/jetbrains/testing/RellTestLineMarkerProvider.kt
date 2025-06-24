package net.postchain.rellide.jetbrains.testing

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionUtil
import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import net.postchain.rellide.jetbrains.language.psi.RellXFunctionDef
import net.postchain.rellide.jetbrains.lsp4ij.RellTestCase
import net.postchain.rellide.jetbrains.services.RellProjectService

class RellTestLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        val projectService = element.project.service<RellProjectService>()
        val testCase = projectService.getTestCase(element) ?: return null

        val anchor = getAnchorElement(element) ?: return null

        return LineMarkerInfo(
                anchor,
                anchor.textRange,
                AllIcons.RunConfigurations.TestState.Run,
                { getTestName(element) },
                { e, elt -> runTest(elt, testCase) },
                GutterIconRenderer.Alignment.CENTER,
                { "Run test" }
        )
    }

    private fun getAnchorElement(element: PsiElement): PsiElement? {
        return element as? RellXFunctionDef
    }

    private fun runTest(element: PsiElement, testCase: RellTestCase) {
        val project = element.project
        val runManager = RunManager.getInstance(project)
        val configuration = createOrFindRunConfiguration(element, runManager, testCase)
        ExecutionUtil.runConfiguration(configuration, DefaultRunExecutor.getRunExecutorInstance())
    }

    private fun createOrFindRunConfiguration(
            element: PsiElement,
            runManager: RunManager,
            testCase: RellTestCase
    ): RunnerAndConfigurationSettings {
        val factory = RellTestConfigurationFactory.getInstance()

        val settings = runManager.createConfiguration(
                getTestName(element),
                factory
        )

        val configuration = settings.configuration as RellTestRunConfiguration
        configuration.options.apply {
            setTestScope(TestScope.TEST_PATTERN)
            setTestPattern(testCase.name)
        }

        return settings
    }

    private fun getTestName(element: PsiElement): String {
        return (element as? RellXFunctionDef)?.let {
            "${it.xQualifiedName?.text}"
        } ?: return "Unknown Test"
    }
}