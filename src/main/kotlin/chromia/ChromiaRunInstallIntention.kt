package net.postchain.rellide.jetbrains.chromia

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import net.postchain.rellide.jetbrains.lsp.RellLspDiagnosticRanges
import net.postchain.rellide.jetbrains.lsp.RellLspDiagnosticsCache
import net.postchain.rellide.jetbrains.toolwindow.execution.ChromiaCommandExecutor

/**
 * Alt-Enter action offering `chr install` when the caret sits on an unresolved-module diagnostic
 * that looks like a not-yet-installed library dependency rather than a typo — see
 * [ChromiaMissingLibDetector]. Shown as an intention rather than attached to the diagnostic
 * itself: quick fixes on LSP diagnostics come only from the server (see [RellCodeActionsSupport]),
 * and this suggestion is entirely client-side.
 */
class ChromiaRunInstallIntention : IntentionAction {
    override fun getText(): String = "Run chr install"
    override fun getFamilyName(): String = text
    override fun startInWriteAction(): Boolean = false

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (editor == null || file == null) return false
        return missingLibModuleAtCaret(project, file, editor) != null
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val virtualFile = file?.virtualFile ?: return
        val configDirectory = RellVersionResolver.getInstance(project).governingConfigDirectory(virtualFile)
        ChromiaCommandExecutor(project).executeCommand("chr install", configDirectory?.path)
    }

    private fun missingLibModuleAtCaret(project: Project, file: PsiFile, editor: Editor): String? {
        val virtualFile = file.virtualFile ?: return null
        val document = PsiDocumentManager.getInstance(project).getDocument(file) ?: return null
        val caretOffset = editor.caretModel.offset
        val diagnostics = RellLspDiagnosticsCache.getInstance(project).diagnosticsFor(virtualFile)
        val diagnosticAtCaret = diagnostics.firstOrNull { diagnostic ->
            val range = RellLspDiagnosticRanges.textRange(document, diagnostic) ?: return@firstOrNull false
            caretOffset in range.startOffset..range.endOffset
        } ?: return null
        return ChromiaMissingLibDetector.missingLibModule(project, virtualFile, diagnosticAtCaret)
    }
}
