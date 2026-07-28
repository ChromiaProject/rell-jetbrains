package net.postchain.rellide.jetbrains.language.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType

/** True if this element is an ANTLR parser-rule node of the given rule index. */
fun PsiElement.isRule(ruleIndex: Int): Boolean = elementType == RellPsiElementTypes.rule(ruleIndex)

/** True if this element is an ANTLR token leaf of the given token type. */
fun PsiElement.isToken(antlrTokenType: Int): Boolean = elementType == RellPsiElementTypes.token(antlrTokenType)

/** Nearest ancestor (exclusive of self) that is a parser-rule node of the given rule index. */
fun PsiElement.ancestorOfRule(ruleIndex: Int): PsiElement? {
    val target = RellPsiElementTypes.rule(ruleIndex)
    var current = parent

    while (current != null) {
        if (current.elementType == target) return current
        current = current.parent
    }

    return null
}
