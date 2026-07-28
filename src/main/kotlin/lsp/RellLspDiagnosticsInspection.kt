package net.postchain.rellide.jetbrains.lsp

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.DiagnosticSeverity
import org.eclipse.lsp4j.Position

/**
 * Surfaces the language server's diagnostics in batch inspection runs (Code | Inspect Code). The
 * editor daemon gets them from the platform's LSP highlighting pass, which batch runs never
 * execute, so this inspection reports the server-pushed diagnostics cached in
 * [RellLspDiagnosticsCache] — and stays out of the editor pass to avoid duplicates.
 */
class RellLspDiagnosticsInspection : LocalInspectionTool() {

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (isOnTheFly) return null

        val virtualFile = file.virtualFile ?: return null
        // Without a running server the cache is either empty or stale — report nothing.
        val application = ApplicationManager.getApplication()
        if (!application.isUnitTestMode && runningRellLspClients(file.project).isEmpty()) return null
        val diagnostics = RellLspDiagnosticsCache.getInstance(file.project).diagnosticsFor(virtualFile)
        if (diagnostics.isEmpty()) return null

        val document = PsiDocumentManager.getInstance(file.project).getDocument(file) ?: return null

        return diagnostics.mapNotNull { diagnostic ->
            val range = textRange(document, diagnostic) ?: return@mapNotNull null
            manager.createProblemDescriptor(
                file,
                range,
                diagnostic.message,
                highlightType(diagnostic),
                false,
            )
        }.toTypedArray()
    }

    private fun textRange(document: Document, diagnostic: Diagnostic): TextRange? {
        val start = offset(document, diagnostic.range.start) ?: return null
        val end = offset(document, diagnostic.range.end) ?: return null
        if (start > end) return null
        // A zero-length range highlights nothing in inspection results; widen it to one character.
        return if (start == end && end < document.textLength) TextRange(start, end + 1) else TextRange(start, end)
    }

    private fun offset(document: Document, position: Position): Int? {
        if (position.line >= document.lineCount) return document.textLength.takeIf { document.lineCount > 0 }
        val lineStart = document.getLineStartOffset(position.line)
        val lineEnd = document.getLineEndOffset(position.line)
        return (lineStart + position.character).coerceAtMost(lineEnd)
    }

    private fun highlightType(diagnostic: Diagnostic): ProblemHighlightType = when (diagnostic.severity) {
        DiagnosticSeverity.Error -> ProblemHighlightType.GENERIC_ERROR
        DiagnosticSeverity.Warning -> ProblemHighlightType.GENERIC_ERROR_OR_WARNING
        else -> ProblemHighlightType.WEAK_WARNING
    }
}
