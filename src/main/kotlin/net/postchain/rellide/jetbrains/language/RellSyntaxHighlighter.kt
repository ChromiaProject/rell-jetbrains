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
        println(tokenType.debugName);
        return pack(tokenMapping[tokenType])
//        if (tokenType == RellTypes.X_TK_SEMI) {
//            return SEPARATOR_KEYS
//        }
//        if (tokenType == RellTypes.X_BASIC_TYPE) {
//            return KEY_KEYS
//        }
//        if (tokenType == RellTypes.X_TK_VAL) {
//            return VALUE_KEYS
//        }
//        if (tokenType == RellTypes.ML_COMMENT) {
//            return COMMENT_KEYS
//        }
//        if (tokenType == RellTypes.SL_COMMENT) {
//            return COMMENT_KEYS
//        }
//        return if (tokenType == TokenType.BAD_CHARACTER) {
//            BAD_CHAR_KEYS
//        } else EMPTY_KEYS
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
        X_ENTITY_KEYWORD,
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

//    companion object {
//        val SEPARATOR = TextAttributesKey.createTextAttributesKey(
//            "RELL_SEPARATOR",
//            DefaultLanguageHighlighterColors.OPERATION_SIGN
//        )
//        val KEY = TextAttributesKey.createTextAttributesKey("RELL_KEY", DefaultLanguageHighlighterColors.KEYWORD)
//        val VALUE = TextAttributesKey.createTextAttributesKey("RELL_VALUE", DefaultLanguageHighlighterColors.STRING)
//        val COMMENT =
//            TextAttributesKey.createTextAttributesKey("RELL_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
//        val BAD_CHARACTER =
//            TextAttributesKey.createTextAttributesKey("RELL_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)
//
//
//
//        private val BAD_CHAR_KEYS = arrayOf(BAD_CHARACTER)
//        private val SEPARATOR_KEYS = arrayOf(SEPARATOR)
//        private val KEY_KEYS = arrayOf(KEY)
//        private val VALUE_KEYS = arrayOf(VALUE)
//        private val COMMENT_KEYS = arrayOf(COMMENT)
//        private val EMPTY_KEYS = arrayOf<TextAttributesKey>()
//    }
}
