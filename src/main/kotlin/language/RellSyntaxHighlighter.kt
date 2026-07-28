package net.postchain.rellide.jetbrains.language

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import net.postchain.rellide.jetbrains.colors.RellColor
import net.postchain.rellide.jetbrains.language.parser.RellLexer
import net.postchain.rellide.jetbrains.language.psi.RellPsiElementTypes

object RellSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = RellLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<out TextAttributesKey> =
        pack(tokenMapping[tokenType])

    private val tokenMapping: Map<IElementType, TextAttributesKey> = buildMap {
        fun color(antlrTokenType: Int, color: RellColor) {
            put(RellPsiElementTypes.token(antlrTokenType), color.textAttributesKey)
        }

        color(RellLexer.RULE_SL_COMMENT, RellColor.LINE_COMMENT)
        color(RellLexer.RULE_ML_COMMENT, RellColor.BLOCK_COMMENT)
        color(RellLexer.RULE_STRING, RellColor.STRING)

        color(RellLexer.RULE_NUMBER, RellColor.NUMBER)
        color(RellLexer.RULE_BIG_INTEGER, RellColor.NUMBER)
        color(RellLexer.RULE_DECIMAL, RellColor.NUMBER)
        color(RellLexer.RULE_BYTES, RellColor.NUMBER)

        color(RellPsiElementTypes.LPAR, RellColor.PARENTHESES)
        color(RellPsiElementTypes.RPAR, RellColor.PARENTHESES)
        color(RellPsiElementTypes.LBRACK, RellColor.BRACKETS)
        color(RellPsiElementTypes.RBRACK, RellColor.BRACKETS)
        color(RellPsiElementTypes.LCURL, RellColor.BRACES)
        color(RellPsiElementTypes.RCURL, RellColor.BRACES)
        color(RellPsiElementTypes.SEMICOLON, RellColor.SEMICOLON)

        for (type in RellPsiElementTypes.KEYWORD_TYPES) {
            color(type, RellColor.KEYWORD)
        }

        for (type in RellPsiElementTypes.OPERATOR_TYPES) {
            color(type, RellColor.OPERATION_SIGN)
        }
    }
}
