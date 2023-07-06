package net.postchain.rellide.jetbrains.language
import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.highlighter.HighlighterIterator
import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet
import net.postchain.rellide.jetbrains.language.psi.RellTokenSets


class RellQuoteTokenHandler : SimpleTokenSetQuoteHandler(RellTokenSets.STRING) {
    override fun hasNonClosedLiteral(editor: Editor?, iterator: HighlighterIterator?, offset: Int) = true
    override fun isOpeningQuote(iterator: HighlighterIterator?, offset: Int): Boolean {
        if(iterator?.tokenType == TokenType.BAD_CHARACTER) {
            return true;
        }
        return super.isOpeningQuote(iterator, offset)
    }
}
