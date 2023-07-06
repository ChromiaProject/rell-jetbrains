package net.postchain.rellide.jetbrains.language

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import net.postchain.rellide.jetbrains.language.psi.RellTypes.*

class RellPairedBraceMatcher : PairedBraceMatcher {
    override fun getPairs() = PAIRS

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean = true

    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int) = openingBraceOffset

    companion object {
        private val PAIRS = arrayOf(
                BracePair(X_TK_LPAR, X_TK_RPAR, false),
                BracePair(X_TK_LBRACK, X_TK_RBRACK, false),
                BracePair(X_TK_LCURL, X_TK_RCURL, false)
        )
    }
}