package net.postchain.rellide.jetbrains.language

import org.antlr.intellij.adaptor.parser.ErrorStrategyAdaptor
import org.antlr.v4.runtime.InputMismatchException
import org.antlr.v4.runtime.NoViableAltException
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.misc.IntervalSet

/**
 * Formats the editor grammar's syntax errors the way Rell itself reports them ("Name expected"):
 * raw grammar token names (`RULE_ID`) and ANTLR's internal phrasing ("extraneous input", "no
 * viable alternative") never reach the user. This matters where no language server runs and the
 * ANTLR message is all there is — injected snippets, and files below the compatibility floor.
 *
 * Mirrors `RellParserErrorStrategy` in the Rell compiler, so the same mistake reads the same in an
 * injected fence and in a served file. Only the report methods that compose user-visible messages
 * are overridden; recovery behavior stays as [ErrorStrategyAdaptor] defines it.
 */
class RellParserErrorStrategy : ErrorStrategyAdaptor() {
    override fun reportInputMismatch(recognizer: Parser, e: InputMismatchException) {
        recognizer.notifyErrorListeners(
            e.offendingToken,
            expectedGot(recognizer, e.expectedTokens, e.offendingToken),
            e
        )
    }

    override fun reportNoViableAlternative(recognizer: Parser, e: NoViableAltException) {
        val token = e.offendingToken ?: e.startToken
        recognizer.notifyErrorListeners(token, unexpected(token), e)
    }

    override fun reportUnwantedToken(recognizer: Parser) {
        if (inErrorRecoveryMode(recognizer)) return
        beginErrorCondition(recognizer)
        val token = recognizer.currentToken
        recognizer.notifyErrorListeners(token, expectedGot(recognizer, getExpectedTokens(recognizer), token), null)
    }

    override fun reportMissingToken(recognizer: Parser) {
        if (inErrorRecoveryMode(recognizer)) return
        beginErrorCondition(recognizer)
        val token = recognizer.currentToken
        val expected = displayTokenSet(recognizer, getExpectedTokens(recognizer))
        val msg = if (expected == null) unexpected(token) else capitalize("$expected expected")
        recognizer.notifyErrorListeners(token, msg, null)
    }

    private fun expectedGot(recognizer: Parser, expected: IntervalSet?, token: Token?): String {
        val display = displayTokenSet(recognizer, expected) ?: return unexpected(token)
        return capitalize("$display expected, got ${displayToken(token)}")
    }

    private fun unexpected(token: Token?): String = when {
        token == null -> "Unexpected input"
        token.type == Token.EOF -> "Unexpected end of file"
        else -> "Unexpected token ${displayToken(token)}"
    }

    /**
     * Human-readable "A, B or C" rendering of an expected-token set, or null when there is nothing
     * to show — the set is empty, or so wide that listing alternatives would be noise (callers then
     * degrade to a plain "unexpected token" message).
     */
    private fun displayTokenSet(recognizer: Parser, expected: IntervalSet?): String? {
        expected ?: return null
        val names = expected.toList().map { displayTokenType(recognizer, it) }.distinct()

        return when {
            names.isEmpty() || names.size > MAX_EXPECTED_TOKENS -> null
            names.size == 1 -> names[0]
            else -> names.dropLast(1).joinToString(", ") + " or " + names.last()
        }
    }

    private fun displayToken(token: Token?): String = when {
        token == null -> "input"
        token.type == Token.EOF -> "end of file"
        else -> "'${escapeTokenText(token.text ?: "")}'"
    }

    /**
     * Token types are grammar-order dependent, so the lexer's own symbolic names — not the
     * generated `RellLexer` constants — decide the wording; every supported Rell version's grammar
     * is then handled by one map.
     */
    private fun displayTokenType(recognizer: Parser, type: Int): String {
        if (type == Token.EOF) return "end of file"
        val vocabulary = recognizer.vocabulary
        vocabulary.getLiteralName(type)?.let { return it }
        return LEXICAL_TOKEN_NAMES[vocabulary.getSymbolicName(type)] ?: "'${vocabulary.getDisplayName(type)}'"
    }

    private fun escapeTokenText(text: String): String {
        val escaped = text.replace("\\", "\\\\").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t")
        return if (escaped.length <= MAX_TOKEN_TEXT) escaped else escaped.take(MAX_TOKEN_TEXT) + "..."
    }

    private fun capitalize(msg: String): String = msg.replaceFirstChar { it.uppercaseChar() }

    private companion object {
        private const val MAX_EXPECTED_TOKENS = 8
        private const val MAX_TOKEN_TEXT = 20

        private val LEXICAL_TOKEN_NAMES = mapOf(
            "RULE_ID" to "name",
            "RULE_NUMBER" to "number",
            "RULE_BIG_INTEGER" to "number",
            "RULE_DECIMAL" to "number",
            "RULE_STRING" to "string",
            "RULE_BYTES" to "byte array",
        )
    }
}
