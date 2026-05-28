package net.postchain.rellide.jetbrains.language

import net.postchain.rellide.jetbrains.language.parser.RellLexer
import org.antlr.intellij.adaptor.lexer.ANTLRLexerAdaptor

class RellLexerAdapter : ANTLRLexerAdaptor(RellLanguage.INSTANCE, RellLexer(null))
