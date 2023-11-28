package net.postchain.rellide.jetbrains.language

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
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

        X_TKLBRACK to RellColor.BRACKETS,
        X_TKRBRACK to RellColor.BRACKETS,
        X_TKLPAR to RellColor.PARENTHESES,
        X_TKRPAR to RellColor.PARENTHESES,
        X_TKLCURL to RellColor.BRACES,
        X_TKRCURL to RellColor.BRACES,
        X_TKSEMI to RellColor.SEMICOLON,

        X_INT_EXPR to RellColor.NUMBER,
        X_BIG_INT_EXPR to RellColor.NUMBER,
        X_DECIMAL_EXPR to RellColor.NUMBER,
        BYTES to RellColor.NUMBER,

        STRING to RellColor.STRING,
        X_FUNCTION_DEF to RellColor.FUNCTION_DECLARATION,
    ).plus(
        keywords().map { it to RellColor.KEYWORD }
    ).plus(
        literals().map { it to RellColor.KEYWORD }
    ).plus(
        operators().map { it to RellColor.OPERATION_SIGN }
    ).plus(
        types().map { it to RellColor.NUMBER }
    ).mapValues { it.value.textAttributesKey }

    fun keywords() = setOf<IElementType>(
            X_TKBREAK, X_TKIF, X_TKELSE, X_TKCONTINUE, X_TKCREATE, X_TKDELETE,
            X_TKENUM, X_TKFOR, X_TKFUNCTION, X_TKIMPORT, X_TKMODULE,
            X_ENTITY_KEYWORD, X_AT_EXPR_LIMIT, X_AT_EXPR_OFFSET, X_STRUCT_KEYWORD,
            X_TKENUM, X_TKWHILE, X_TKWHEN, X_TKGUARD, X_TKIN,
            X_TKINCLUDE, X_TKQUERY, X_TKSTRUCT, X_TKOPERATION, X_TKOBJECT,
            X_TKNAMESPACE, X_TKMUTABLE, X_TKRETURN, X_TKVIRTUAL, X_TKVAL, X_TKUPDATE, X_KEY_INDEX_KIND, X_VAR_VAL,
            X_MODIFIER
    )

    fun types() = setOf<IElementType>(
        BIG_INTEGER, COMMON_INT, DECIMAL, DECNUM, NUMBER, HEXDIGNUM
    )

    private fun literals() = setOf<IElementType>(X_NULLLITERALEXPR, X_LITERAL_EXPR,
            X_LIST_LITERAL_EXPR, X_EMPTY_MAP_LITERAL_EXPR, X_NON_EMPTY_MAP_LITERAL_EXPR)

    private fun operators() = setOf<IElementType>(
            X_TKASSIGN, X_ASSIGN_OP, X_TKPLUS, X_TKMUL, X_BINARY_OPERATOR, X_INCREMENT_OPERATOR,
            X_UNARY_PREFIX_OPERATOR, X_AT_EXPR_AT
    )
}
