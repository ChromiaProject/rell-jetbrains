package net.postchain.rellide.jetbrains.language
import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.highlighter.HighlighterIterator
import net.postchain.rellide.jetbrains.language.psi.RellTokenSets
import net.postchain.rellide.jetbrains.language.psi.RellTypes


class RellQuoteTokenHandler : SimpleTokenSetQuoteHandler(RellTokenSets.STRING) {
    override fun hasNonClosedLiteral(editor: Editor?, iterator: HighlighterIterator?, offset: Int) = true
    override fun isOpeningQuote(iterator: HighlighterIterator?, offset: Int): Boolean {
        if(iterator?.tokenType == RellTypes.STRING_NOT_CLOSED) {
            return true;
        }
        return super.isOpeningQuote(iterator, offset)
    }
}
