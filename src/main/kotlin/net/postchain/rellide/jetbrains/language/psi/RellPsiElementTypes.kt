package net.postchain.rellide.jetbrains.language.psi

import com.intellij.psi.tree.TokenSet
import net.postchain.rellide.jetbrains.language.RellLanguage
import net.postchain.rellide.jetbrains.language.parser.RellLexer
import net.postchain.rellide.jetbrains.language.parser.RellParser
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory
import org.antlr.intellij.adaptor.lexer.RuleIElementType
import org.antlr.intellij.adaptor.lexer.TokenIElementType

/**
 * Bridges the ANTLR-generated [RellLexer] / [RellParser] token and rule types to IntelliJ
 * [com.intellij.psi.tree.IElementType]s via antlr4-intellij-adaptor.
 *
 * The element types are derived directly from the grammar's vocabulary, so they stay correct when
 * the grammar is regenerated and token/rule numbering shifts.
 */
object RellPsiElementTypes {
    init {
        PSIElementTypeFactory.defineLanguageIElementTypes(
            RellLanguage.INSTANCE,
            RellParser.VOCABULARY,
            RellParser.ruleNames,
        )
    }

    private val tokenTypes: List<TokenIElementType> =
        PSIElementTypeFactory.getTokenIElementTypes(RellLanguage.INSTANCE)
    private val ruleTypes: List<RuleIElementType> =
        PSIElementTypeFactory.getRuleIElementTypes(RellLanguage.INSTANCE)

    /** Element type for the given ANTLR token type (e.g. [RellLexer.RULE_STRING]). */
    fun token(antlrTokenType: Int): TokenIElementType = tokenTypes[antlrTokenType]

    /** Element type for the given ANTLR parser rule index (e.g. [RellParser.RULE_functionDef]). */
    fun rule(ruleIndex: Int): RuleIElementType = ruleTypes[ruleIndex]

    private val literalToType: Map<String, Int> = buildMap {
        val vocab = RellParser.VOCABULARY
        for (type in 1..vocab.maxTokenType) {
            val literal = vocab.getLiteralName(type) ?: continue
            // Literal names are quoted, e.g. "'{'" or "'module'".
            put(literal.trim('\''), type)
        }
    }

    private fun literal(text: String): Int =
        literalToType[text] ?: error("No ANTLR token for literal '$text'")

    // --- Punctuation, referenced by brace matching and highlighting. ---
    val LPAR = literal("(")
    val RPAR = literal(")")
    val LBRACK = literal("[")
    val RBRACK = literal("]")
    val LCURL = literal("{")
    val RCURL = literal("}")
    val SEMICOLON = literal(";")

    private val bracketLiterals = setOf("(", ")", "[", "]", "{", "}", ";", ",")

    /** Keyword literal tokens — any literal made of letters (e.g. `function`, `if`, `and`). */
    val KEYWORD_TYPES: List<Int> =
        literalToType.filterKeys { it.all(Char::isLetter) }.values.toList()

    /** Operator/symbol literal tokens — punctuation that is neither a bracket nor a keyword. */
    val OPERATOR_TYPES: List<Int> =
        literalToType.filterKeys { it !in bracketLiterals && !it.all(Char::isLetter) }.values.toList()

    // --- Token sets. ---
    val WHITESPACE: TokenSet = tokenSet(RellLexer.RULE_WS)
    val COMMENTS: TokenSet = tokenSet(RellLexer.RULE_ML_COMMENT, RellLexer.RULE_SL_COMMENT)
    val STRINGS: TokenSet = tokenSet(RellLexer.RULE_STRING)

    fun tokenSet(vararg antlrTokenTypes: Int): TokenSet =
        PSIElementTypeFactory.createTokenSet(RellLanguage.INSTANCE, *antlrTokenTypes)
}
