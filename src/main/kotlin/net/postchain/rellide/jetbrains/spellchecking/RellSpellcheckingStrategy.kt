package net.postchain.rellide.jetbrains.spellchecking

import com.intellij.codeInsight.CodeInsightUtilCore
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.spellchecker.inspections.PlainTextSplitter
import com.intellij.spellchecker.tokenizer.EscapeSequenceTokenizer
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy
import com.intellij.spellchecker.tokenizer.TokenConsumer
import com.intellij.spellchecker.tokenizer.Tokenizer
import net.postchain.rellide.jetbrains.language.RellLanguage
import net.postchain.rellide.jetbrains.language.parser.RellLexer
import net.postchain.rellide.jetbrains.language.psi.RellPsiElementTypes

class RellSpellcheckingStrategy : SpellcheckingStrategy() {
    override fun isMyContext(element: PsiElement) = RellLanguage.INSTANCE.`is`(element.language)

    override fun getTokenizer(element: PsiElement?): Tokenizer<*> = when {
        element?.node?.elementType == RellPsiElementTypes.token(RellLexer.RULE_STRING) -> StringExpressionTokenizer
        else -> super.getTokenizer(element)
    }
}

object StringExpressionTokenizer : EscapeSequenceTokenizer<LeafPsiElement>() {
    override fun tokenize(element: LeafPsiElement, consumer: TokenConsumer) {
        val text = element.text

        if (!text.contains("\\")) {
            consumer.consumeToken(element, PlainTextSplitter.getInstance())
        } else {
            processTextWithEscapeSequences(element, text, consumer)
        }
    }

    private fun processTextWithEscapeSequences(element: LeafPsiElement, text: String, consumer: TokenConsumer) {
        val unescapedText = StringBuilder()
        val offsets = IntArray(text.length + 1)
        CodeInsightUtilCore.parseStringCharacters(text, unescapedText, offsets)
        processTextWithOffsets(element, consumer, unescapedText, offsets, 1)
    }
}

