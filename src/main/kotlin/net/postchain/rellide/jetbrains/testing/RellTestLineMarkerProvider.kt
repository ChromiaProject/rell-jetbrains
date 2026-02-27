package net.postchain.rellide.jetbrains.testing

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionUtil
import com.intellij.icons.AllIcons
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.elementType
import net.postchain.rellide.jetbrains.language.psi.RellTypes
import net.postchain.rellide.jetbrains.language.psi.RellXFunctionDef
import net.postchain.rellide.jetbrains.language.psi.RellXModuleHeader
import net.postchain.rellide.jetbrains.lsp4ij.RellTestCase
import net.postchain.rellide.jetbrains.lsp4ij.RellTestFile
import net.postchain.rellide.jetbrains.lsp4ij.rellLanguageServerIsRunning
import net.postchain.rellide.jetbrains.services.RellProjectService
import net.postchain.rellide.jetbrains.testing.actions.createTestConfiguration

class RellTestLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        return runReadAction {
            if (element !is LeafPsiElement) return@runReadAction null

            val isModuleHeader = isTestModuleHeader(element)
            val isFunctionName = isFunctionName(element)
            if (!isModuleHeader && !isFunctionName) return@runReadAction null

            val virtualFile = element.containingFile?.virtualFile ?: return@runReadAction null
            val testFile = getTestFileForElement(element, virtualFile) ?: return@runReadAction null

            when {
                isModuleHeader -> return@runReadAction createTestModuleLineMarker(element, testFile, virtualFile)
                else -> {
                    val testCase = testFile.testCases.firstOrNull { it.name == element.text } ?: return@runReadAction null
                    return@runReadAction createTestCaseLineMarker(element, testCase)
                }
            }
        }
    }

    private fun createTestCaseLineMarker(
        element: LeafPsiElement,
        testCase: RellTestCase
    ): LineMarkerInfo<LeafPsiElement> = LineMarkerInfo(
        element,
        element.textRange,
        AllIcons.RunConfigurations.TestState.Run,
        { getTestName(element) },
        { _, elt -> runTest(elt, testCase) },
        GutterIconRenderer.Alignment.CENTER,
        { "Run test" }
    )

    private fun createTestModuleLineMarker(
        element: LeafPsiElement,
        testFile: RellTestFile,
        virtualFile: VirtualFile
    ): LineMarkerInfo<LeafPsiElement> = LineMarkerInfo(
        element,
        element.textRange,
        AllIcons.RunConfigurations.TestState.Run_run,
        { testFile.moduleName },
        { _, elt ->
            val project = elt.project
            val runManager = RunManager.getInstance(project)
            val configurationSettings = createTestConfiguration(project, virtualFile, runManager, testFile)
            runManager.addConfiguration(configurationSettings)
            runManager.selectedConfiguration = configurationSettings
            ExecutionUtil.runConfiguration(configurationSettings, DefaultRunExecutor.getRunExecutorInstance())
        },
        GutterIconRenderer.Alignment.CENTER,
        { "Run tests in module" }
    )

    private fun getTestFileForElement(element: PsiElement, virtualFile: VirtualFile): RellTestFile? {
        if (!rellLanguageServerIsRunning(element.project)) return null
        return element.project.service<RellProjectService>().getTestFile(virtualFile)
    }

    private fun isFunctionName(element: PsiElement): Boolean {
        if (element.elementType != RellTypes.ID) {
            return false
        }
        if (element.parent?.parent?.parent?.parent !is RellXFunctionDef) {
            return false
        }
        return true
    }

    private fun isTestModuleHeader(element: PsiElement): Boolean {
        if (element.elementType != RellTypes.ID) {
            return false
        }
        if (element.parent?.parent?.parent?.parent?.parent?.parent !is RellXModuleHeader) {
            return false
        }
        return element.text == "test"
    }

    private fun runTest(element: PsiElement, testCase: RellTestCase) {
        val project = element.project
        val runManager = RunManager.getInstance(project)
        val configuration = createOrFindRunConfiguration(element, runManager, testCase)
        runManager.addConfiguration(configuration)
        runManager.selectedConfiguration = configuration
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
        return element.text ?: return "Unknown Test"
    }
}