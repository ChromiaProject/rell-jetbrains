package net.postchain.rellide.jetbrains.chromia

import org.antlr.v4.runtime.*

/**
 * Version-exact ANTLR parsers, generated at build time from each supported Rell release's own
 * `Rell.g4` (see `versionedGrammarRoots` in build.gradle.kts). The newest version has no entry
 * here: its grammar drives the editor PSI, so its syntax errors already surface as PsiErrorElements.
 */
object VersionedRellParsers {
    data class SyntaxError(val line: Int, val column: Int, val length: Int, val message: String)

    private val parsers: Map<RellVersion, (String) -> List<SyntaxError>> = mapOf(
        RellVersion(0, 16, 1) to { text ->
            collectErrors(text) { lexer, listener ->
                val antlrLexer = net.postchain.rellide.jetbrains.language.parser.v0_16_1.RellLexer(lexer)
                antlrLexer.removeErrorListeners()
                antlrLexer.addErrorListener(listener)
                val parser =
                    net.postchain.rellide.jetbrains.language.parser.v0_16_1.RellParser(CommonTokenStream(antlrLexer))
                parser.removeErrorListeners()
                parser.addErrorListener(listener)
                parser.file()
            }
        },
    )

    fun supports(version: RellVersion): Boolean = version in parsers

    fun parse(version: RellVersion, text: String): List<SyntaxError> = parsers.getValue(version)(text)

    private inline fun collectErrors(
        text: String,
        parse: (CharStream, BaseErrorListener) -> Unit,
    ): List<SyntaxError> {
        val errors = mutableListOf<SyntaxError>()

        val listener = object : BaseErrorListener() {
            override fun syntaxError(
                recognizer: Recognizer<*, *>?,
                offendingSymbol: Any?,
                line: Int,
                charPositionInLine: Int,
                msg: String?,
                e: RecognitionException?,
            ) {
                val length = (offendingSymbol as? Token)
                    ?.let { it.stopIndex - it.startIndex + 1 }
                    ?.coerceAtLeast(1)
                    ?: 1

                errors += SyntaxError(line, charPositionInLine, length, msg ?: "syntax error")
            }
        }

        parse(CharStreams.fromString(text), listener)
        return errors
    }
}
