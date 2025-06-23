package net.postchain.rellide.jetbrains.testing

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.ex.EditorGutterComponentEx
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.redhat.devtools.lsp4ij.LanguageServerManager
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import net.postchain.rellide.jetbrains.language.psi.RellFile
import net.postchain.rellide.jetbrains.language.psi.RellXFunctionDef
import net.postchain.rellide.jetbrains.lsp4ij.RellServerApi
import net.postchain.rellide.jetbrains.lsp4ij.RellTestCase
import javax.swing.Icon
import com.intellij.terminal.TerminalExecutionConsole
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.guessProjectDir

class RellTestRunnerProvider(private val project: Project) {

    // TODO: Handle !!
    private val editor = FileEditorManager.getInstance(project).selectedTextEditor!!

    fun updateButtons(fileUri: String) {
        val testCases = mutableListOf<RellTestCase>()
        try {
            runBlocking {
                LanguageServerManager.getInstance(project).getLanguageServer(RELL_LANGUAGE_SERVER_ID).get()?.let { lsItem ->
                    val rellServer = lsItem.server as RellServerApi
                    val fileTestCases = rellServer.listTestCases(fileUri).await()
                    testCases.addAll(fileTestCases)
                }
            }
        } catch (e: Exception) {
            println("Bad call, you lose")
        }

        ApplicationManager.getApplication().invokeLater {
            testCases.forEach { testCase ->
                val testCaseMarkerExists = editor.markupModel.allHighlighters.any { it.textAttributesKey?.externalName == testCase.name }
                if (!testCaseMarkerExists) {
                    addRunButtonForTestCase(testCase)
                }
            }
        }
    }

    private fun addRunButtonForTestCase(testCase: RellTestCase) {

        // TODO: Jetbrains VirutalFileManager has three backslashes in their file uris

        val virtualFile = VirtualFileManager.getInstance().findFileByUrl(testCase.uri.replace("file:/", "file:///"))
                ?: return
        val psiFile = PsiManager.getInstance(project).findFile(virtualFile) as? RellFile ?: return
        val document = psiFile.viewProvider.document ?: return

        try {
            val startLine = testCase.range.start.line
            val startChar = testCase.range.start.character
            val lineStartOffset = document.getLineStartOffset(startLine)
            val offset = lineStartOffset + startChar
            val elementAtOffset = psiFile.findElementAt(offset) ?: return
            val testFunctionDef = getTestFunctionDef(elementAtOffset) ?: return
            addRunButtonToElement(testFunctionDef, testCase.name, null)
        } catch (e: Exception) {
            println("Failed to add run button: ${e.message}")
        }
    }

    private fun getTestFunctionDef(element: PsiElement): RellXFunctionDef? {
        // Getting parent because token at offset will be "function" token, not function definition
        val possibleTestFunctionDef = element.parent

        return if (possibleTestFunctionDef is RellXFunctionDef) {
            possibleTestFunctionDef
        } else {
            null
        }
    }

    private fun addRunButtonToElement(element: PsiElement, testCaseName: String, runAction: Runnable? = null) {

        val iconRenderer = object : GutterIconRenderer() {
            override fun getIcon(): Icon = AllIcons.Actions.Execute
            override fun getTooltipText(): String = "Run $testCaseName"
            override fun getClickAction(): AnAction = object : AnAction() {
                override fun actionPerformed(e: AnActionEvent) {

                    val toolWindowManager = ToolWindowManager.getInstance(project)
                    val terminalWindow = toolWindowManager.getToolWindow("Terminal")

                    ApplicationManager.getApplication().invokeLater {
                        terminalWindow?.show {
                            val commandLine = GeneralCommandLine("chr", "test", "--tests", testCaseName)
                            commandLine.workDirectory = project.guessProjectDir()?.toNioPath()?.toFile()

                            val processHandler = OSProcessHandler(commandLine)
                            val terminal = TerminalExecutionConsole(project, processHandler)

                            processHandler.startNotify()
                            terminal.attachToProcess(processHandler)


                            // Try to find existing content
                            val existingContent = terminalWindow.contentManager.contents.firstOrNull()

                            if (existingContent != null) {
                                // If content exists, dispose it and reuse the tab
                                existingContent.dispose()
                                terminalWindow.contentManager.addContent(terminalWindow.contentManager.factory.createContent(terminal.component, "Terminal", false))
                            } else {
                                // If no content exists, create new
                                terminalWindow.contentManager.addContent(terminalWindow.contentManager.factory.createContent(terminal.component, "Terminal", false))
                            }
                        }
                    }
                }
            }

            override fun equals(other: Any?): Boolean = false
            override fun hashCode(): Int = System.identityHashCode(this)
            override fun getAlignment(): Alignment = Alignment.CENTER
        }

        val startOffset = element.textRange.startOffset
        val endOffset = element.textRange.endOffset
        val markupModel = editor.markupModel

        val highlighter = markupModel.addRangeHighlighter(TextAttributesKey.createTextAttributesKey(testCaseName), startOffset, endOffset, 0, HighlighterTargetArea.EXACT_RANGE)

        highlighter.gutterIconRenderer = iconRenderer
        val gutter = editor.gutter as? EditorGutterComponentEx
        gutter?.revalidateMarkup()
    }

    companion object {
        const val RELL_LANGUAGE_SERVER_ID = "rellLanguageServer"
    }
}
