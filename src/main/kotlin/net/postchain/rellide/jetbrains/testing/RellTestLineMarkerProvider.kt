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
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.elementType
import com.redhat.devtools.lsp4ij.ServerStatus
import net.postchain.rellide.jetbrains.language.psi.RellTypes
import net.postchain.rellide.jetbrains.language.psi.RellXFunctionDef
import net.postchain.rellide.jetbrains.language.psi.RellXModuleHeader
import net.postchain.rellide.jetbrains.lsp4ij.RellTestCase
import net.postchain.rellide.jetbrains.lsp4ij.RellTestFile
import net.postchain.rellide.jetbrains.lsp4ij.getRellLanguageServerStatus
import net.postchain.rellide.jetbrains.services.RellProjectService
import net.postchain.rellide.jetbrains.testing.actions.createTestConfiguration

class RellTestLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        return runReadAction {
            if (element !is LeafPsiElement) {
                return@runReadAction null
            }

            if (isTestModuleHeader(element)) {
                val rellTestFile = getRellTestFileForElement(element) ?: return@runReadAction null
                val project = element.project
                val virtualFile = element.containingFile.virtualFile ?: return@runReadAction null

                return@runReadAction LineMarkerInfo(
                        element,
                        element.textRange,
                        AllIcons.RunConfigurations.TestState.Run_run,
                        { rellTestFile.moduleName },
                        { e, elt ->
                            val runManager = RunManager.getInstance(project)
                            val configurationSettings = createTestConfiguration(project, virtualFile, runManager, rellTestFile)
                            runManager.addConfiguration(configurationSettings)
                            runManager.selectedConfiguration = configurationSettings
                            ExecutionUtil.runConfiguration(configurationSettings, DefaultRunExecutor.getRunExecutorInstance())
                        },
                        GutterIconRenderer.Alignment.CENTER,
                        { "Run tests in module" }
                )
            }
            if (!isFunctionName(element)) {
                return@runReadAction null
            }
            val languageServerStatus = getRellLanguageServerStatus(element.project)
            if (languageServerStatus != ServerStatus.started) {
                return@runReadAction null
            }
            val projectService = element.project.service<RellProjectService>()
            val testCase = projectService.getTestCase(element) ?: return@runReadAction null

            return@runReadAction LineMarkerInfo(
                    element,
                    element.textRange,
                    AllIcons.RunConfigurations.TestState.Run,
                    { getTestName(element) },
                    { e, elt -> runTest(elt, testCase) },
                    GutterIconRenderer.Alignment.CENTER,
                    { "Run test" }
            )
        }
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

    private fun getRellTestFileForElement(element: PsiElement): RellTestFile? {
        val languageServerStatus = getRellLanguageServerStatus(element.project)
        if (languageServerStatus != ServerStatus.started) {
            return null
        }
        val projectService = element.project.service<RellProjectService>()
        val virtualFile = element.containingFile?.virtualFile ?: return null
        return projectService.getTestFile(virtualFile)
    }

    private fun getAnchorElement(element: PsiElement): PsiElement? {
        return element
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
        return (element as? RellXFunctionDef)?.let {
            "${it.xQualifiedName?.text}"
        } ?: return "Unknown Test"
    }
}