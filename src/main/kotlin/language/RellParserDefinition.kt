package net.postchain.rellide.jetbrains.language

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import net.postchain.rellide.jetbrains.language.parser.RellParser
import net.postchain.rellide.jetbrains.language.psi.RellFile
import net.postchain.rellide.jetbrains.language.psi.RellPsiElementTypes
import org.antlr.intellij.adaptor.parser.ANTLRParserAdaptor
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.tree.ParseTree

class RellParserDefinition : ParserDefinition {
    override fun createLexer(project: Project): Lexer = RellLexerAdapter()

    override fun createParser(project: Project): PsiParser =
        object : ANTLRParserAdaptor(RellLanguage.INSTANCE, RellParser(null)) {
            override fun parse(parser: Parser, root: com.intellij.psi.tree.IElementType): ParseTree {
                // The only root we are asked to parse is a whole file.
                return (parser as RellParser).file()
            }
        }

    override fun getFileNodeType(): IFileElementType = FILE
    override fun getCommentTokens(): TokenSet = RellPsiElementTypes.COMMENTS
    override fun getWhitespaceTokens(): TokenSet = RellPsiElementTypes.WHITESPACE
    override fun getStringLiteralElements(): TokenSet = RellPsiElementTypes.STRINGS
    override fun createElement(node: ASTNode): PsiElement = ASTWrapperPsiElement(node)
    override fun createFile(viewProvider: FileViewProvider): PsiFile = RellFile(viewProvider)

}

private val FILE: IFileElementType = IFileElementType(RellLanguage.INSTANCE)
