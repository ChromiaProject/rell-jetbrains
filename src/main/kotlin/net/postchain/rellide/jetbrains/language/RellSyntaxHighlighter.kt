package net.postchain.rellide.jetbrains.language

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import groovyjarjarantlr4.v4.runtime.atn.SemanticContext.AND
import net.postchain.rellide.jetbrains.colors.RellColor
import net.postchain.rellide.jetbrains.language.psi.RellTypes.*

object RellSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer {
        return RellLexerAdapter()
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<out TextAttributesKey> {
        return pack(tokenMapping[tokenType])
    }

    private val tokenMapping: Map<IElementType, TextAttributesKey> = mapOf(
        SL_COMMENT to RellColor.LINE_COMMENT,
        ML_COMMENT to RellColor.BLOCK_COMMENT,

        X_TK_LBRACK to RellColor.BRACES,
        X_TK_LPAR to RellColor.PARENTHESES,
        X_TK_SEMI to RellColor.SEMICOLON,

        X_INT_EXPR to RellColor.NUMBER,
        X_BIG_INT_EXPR to RellColor.NUMBER,
        X_DECIMAL_EXPR to RellColor.NUMBER,
        BYTES to RellColor.NUMBER,

        STRING to RellColor.STRING
    ).plus(
        keywords().map { it to RellColor.KEYWORD }
    ).plus(
        literals().map { it to RellColor.KEYWORD }
    ).plus(
        operators().map { it to RellColor.OPERATION_SIGN }
    ).plus(
        types().map { it to RellColor.TYPE }
    ).mapValues { it.value.textAttributesKey }

    fun keywords() = setOf<IElementType>(
        X_TK_BREAK, X_TK_IF, X_TK_CONTINUE, X_TK_CREATE, X_TK_DELETE,
        X_TK_ENUM, X_TK_FOR, X_TK_FUNCTION, X_TK_IMPORT, X_TK_MODULE,
        X_ENTITY_KEYWORD, X_ENTITY_DEF,
        X_TK_ENUM, X_TK_WHILE, X_TK_WHEN, X_TK_FOR, X_TK_GUARD, X_TK_IN,
        X_TK_INCLUDE, X_TK_QUERY, X_TK_STRUCT, X_TK_OPERATION, X_TK_OBJECT,
        X_TK_NAMESPACE, X_TK_MUTABLE, X_TK_RETURN, X_TK_VIRTUAL, X_TK_VAL, X_TK_UPDATE, X_KEY_INDEX_KIND
    )

    fun types() = setOf<IElementType>(
        BIG_INTEGER, COMMON_INT, DECIMAL, DECNUM, NUMBER
    )

    private fun literals() = setOf<IElementType>(BOOLEANLITERAL)

    private fun operators() = setOf<IElementType>(
        X_TK_ASSIGN, X_ASSIGN_OP, X_TK_PLUS, X_TK_MUL, EXPONENT, X_BINARY_OPERATOR,
    )
}
