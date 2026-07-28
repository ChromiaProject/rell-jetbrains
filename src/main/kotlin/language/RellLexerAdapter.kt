package net.postchain.rellide.jetbrains.language

import net.postchain.rellide.jetbrains.language.parser.RellLexer
import net.postchain.rellide.jetbrains.language.psi.RellPsiElementTypes
import org.antlr.intellij.adaptor.lexer.ANTLRLexerAdaptor

class RellLexerAdapter : ANTLRLexerAdaptor(RellPsiElementTypes.registeredLanguage(), RellLexer(null))
