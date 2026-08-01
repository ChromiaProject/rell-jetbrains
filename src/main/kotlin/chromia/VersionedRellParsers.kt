package net.postchain.rellide.jetbrains.chromia

import net.postchain.rellide.jetbrains.language.parser.v0_16_1.RellLexer as RellLexer0161
import net.postchain.rellide.jetbrains.language.parser.v0_16_1.RellParser as RellParser0161
import net.postchain.rellide.jetbrains.language.parser.v0_16_2.RellLexer as RellLexer0162
import net.postchain.rellide.jetbrains.language.parser.v0_16_2.RellParser as RellParser0162
import org.antlr.v4.runtime.*

/**
 * Version-exact ANTLR parsers, generated at build time from each supported Rell release's own
 * `Rell.g4` (see `versionedGrammarRoots` in build.gradle.kts). The newest version has no entry
 * here: its grammar drives the editor PSI, so its syntax errors already surface as PsiErrorElements.
 */
object VersionedRellParsers {
    data class SyntaxError(val line: Int, val column: Int, val length: Int, val message: String)

    private val parsers: Map<RellVersion, (String) -> List<SyntaxError>> = mapOf(
        RellVersion(0, 16, 1) to entry(::RellLexer0161, ::RellParser0161, RellParser0161::file),
        RellVersion(0, 16, 2) to entry(::RellLexer0162, ::RellParser0162, RellParser0162::file),
    )

    fun supports(version: RellVersion): Boolean = version in parsers

    fun parse(version: RellVersion, text: String): List<SyntaxError> = parsers.getValue(version)(text)

    /**
     * One version's parse function. Each generated grammar package declares its own unrelated
     * `RellLexer`/`RellParser` types, so the version-exact classes come in as constructor and
     * root-rule references.
     */
    private fun <L : Lexer, P : Parser> entry(
        newLexer: (CharStream) -> L,
        newParser: (TokenStream) -> P,
        rootRule: (P) -> Any?,
    ): (String) -> List<SyntaxError> = { text ->
        collectErrors(text) { charStream, listener ->
            val lexer = newLexer(charStream)
            lexer.removeErrorListeners()
            lexer.addErrorListener(listener)

            val parser = newParser(CommonTokenStream(lexer))
            parser.removeErrorListeners()
            parser.addErrorListener(listener)
            rootRule(parser)
        }
    }

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
