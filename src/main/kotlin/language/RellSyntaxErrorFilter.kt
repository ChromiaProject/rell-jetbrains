package net.postchain.rellide.jetbrains.language

import com.intellij.codeInsight.highlighting.HighlightErrorFilter
import com.intellij.injected.editor.VirtualFileWindow
import com.intellij.psi.PsiErrorElement
import net.postchain.rellide.jetbrains.lsp.routedRellVersion

/**
 * Hides PSI-parser error elements in Rell files the language server serves: the server publishes
 * the same syntax errors with human-readable messages ("Name expected"), so the editor grammar's
 * raw ANTLR messages ("missing RULE_ID at '{'") would stack on top of them in the same popup.
 * PSI errors stay visible where no server runs — injected fragments (Markdown fences) and files
 * below the compatibility floor.
 */
class RellSyntaxErrorFilter : HighlightErrorFilter() {
    override fun shouldHighlightErrorElement(element: PsiErrorElement): Boolean {
        val file = element.containingFile ?: return true
        if (file.language != RellLanguage.INSTANCE) return true
        if (file.viewProvider.virtualFile is VirtualFileWindow || file.context != null) return true
        val virtualFile = file.virtualFile ?: return true
        return routedRellVersion(file.project, virtualFile) == null
    }
}
