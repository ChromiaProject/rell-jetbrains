package net.postchain.rellide.jetbrains

import net.postchain.rellide.jetbrains.language.parser.RellLexer
import net.postchain.rellide.jetbrains.language.parser.RellParser
import org.antlr.v4.runtime.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Sanity-checks that the ANTLR-generated lexer/parser instantiate (ATN deserialization succeeds with
 * the bundled antlr4-runtime) and parse representative Rell without syntax errors.
 */
class RellAntlrGrammarTest {
    private class CollectingErrorListener : BaseErrorListener() {
        val errors = mutableListOf<String>()

        override fun syntaxError(
            recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int,
            charPositionInLine: Int, msg: String?, e: RecognitionException?,
        ) {
            errors += "$line:$charPositionInLine $msg"
        }
    }

    private fun parseErrors(source: String): List<String> {
        val listener = CollectingErrorListener()

        val lexer = RellLexer(CharStreams.fromString(source)).apply {
            removeErrorListeners()
            addErrorListener(listener)
        }

        val parser = RellParser(CommonTokenStream(lexer)).apply {
            removeErrorListeners()
            addErrorListener(listener)
        }

        parser.file()
        return listener.errors
    }

    @Test
    fun parsesRepresentativeModule() {
        val source = """
            module;

            entity user {
                key name;
                mutable balance: integer = 0;
            }

            function transfer(from: user, to: user, amount: integer): boolean {
                if (from.balance >= amount) {
                    update from ( balance -= amount );
                    return true;
                }
                return false;
            }

            query all_users() = user @* {};
        """.trimIndent()
        assertEquals(expected = emptyList(), actual = parseErrors(source))
    }

    @Test
    fun reportsSyntaxErrorOnGarbage() {
        // assertTrue, not Kotlin's `assert`, which is a no-op unless assertions are enabled.
        assertTrue(parseErrors("function (").isNotEmpty())
    }
}
