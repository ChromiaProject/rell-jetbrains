package net.postchain.rellide.jetbrains.language

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import net.postchain.rellide.jetbrains.language.psi.RellPsiElementTypes

class RellPairedBraceMatcher : PairedBraceMatcher {
    override fun getPairs() = PAIRS

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean = true

    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int) = openingBraceOffset

}

private val PAIRS = arrayOf(
    BracePair(
        RellPsiElementTypes.token(RellPsiElementTypes.LPAR),
        RellPsiElementTypes.token(RellPsiElementTypes.RPAR),
        false
    ),
    BracePair(
        RellPsiElementTypes.token(RellPsiElementTypes.LBRACK),
        RellPsiElementTypes.token(RellPsiElementTypes.RBRACK),
        false
    ),
    BracePair(
        RellPsiElementTypes.token(RellPsiElementTypes.LCURL),
        RellPsiElementTypes.token(RellPsiElementTypes.RCURL),
        false
    ),
)
