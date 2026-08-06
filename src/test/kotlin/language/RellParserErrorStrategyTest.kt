package net.postchain.rellide.jetbrains.language

import net.postchain.rellide.jetbrains.language.parser.RellLexer
import net.postchain.rellide.jetbrains.language.parser.RellParser
import org.antlr.v4.runtime.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RellParserErrorStrategyTest {
    private fun messages(source: String): List<String> {
        val messages = mutableListOf<String>()

        val listener = object : BaseErrorListener() {
            override fun syntaxError(
                recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int,
                charPositionInLine: Int, msg: String?, e: RecognitionException?,
            ) {
                messages += msg.orEmpty()
            }
        }

        val lexer = RellLexer(CharStreams.fromString(source)).apply { removeErrorListeners() }
        val parser = RellParser(CommonTokenStream(lexer)).apply {
            errorHandler = RellParserErrorStrategy()
            removeErrorListeners()
            addErrorListener(listener)
        }

        parser.file()
        return messages
    }

    @Test
    fun namesTheIdentifierTokenInsteadOfLeakingItsGrammarName() {
        val messages = messages("object foo { x: integer = 123; key x; }")
        assertTrue(messages.isNotEmpty(), "expected a syntax error")
        assertTrue(messages.any { "name" in it }, messages.toString())
        assertFalse(messages.any { "RULE_" in it }, messages.toString())
    }

    @Test
    fun wordsAMissingTokenAsAnExpectation() {
        assertEquals(listOf("';' expected"), messages("val x = 1 query q() = 1;"))
    }

    @Test
    fun reportsUnexpectedInputAtEndOfFile() {
        assertTrue(messages("function f(").any { "end of file" in it }, "expected an end-of-file message")
    }

    @Test
    fun leavesNoAntlrPhrasingInMessages() {
        val antlrPhrases = listOf("extraneous input", "mismatched input", "no viable alternative", "missing ")
        for (source in listOf("object foo { key x; }", "function f(", "entity 123 {}", "val = 1;")) {
            val messages = messages(source)
            assertTrue(messages.isNotEmpty(), "expected a syntax error for: $source")
            for (message in messages) {
                assertFalse(antlrPhrases.any { it in message }, "$source -> $message")
                assertFalse("RULE_" in message, "$source -> $message")
            }
        }
    }
}
