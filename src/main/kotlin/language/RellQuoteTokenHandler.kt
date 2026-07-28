package net.postchain.rellide.jetbrains.language

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.highlighter.HighlighterIterator
import net.postchain.rellide.jetbrains.language.psi.RellPsiElementTypes

class RellQuoteTokenHandler : SimpleTokenSetQuoteHandler(RellPsiElementTypes.STRINGS) {
    override fun hasNonClosedLiteral(editor: Editor?, iterator: HighlighterIterator?, offset: Int) = true
}
