package net.postchain.rellide.jetbrains.language


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
import net.postchain.rellide.jetbrains.language.psi.RellTokenSets
import net.postchain.rellide.jetbrains.language.psi.RellTypes



class RellParserDefinition : ParserDefinition {
    override fun createLexer(project: Project): Lexer {
        return RellLexerAdapter()
    }

    override fun getCommentTokens(): TokenSet {
        return RellTokenSets.ML_COMMENT
    }

    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun createParser(project: Project): PsiParser {
        return RellParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return FILE
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return RellFile(viewProvider)
    }

    override fun createElement(node: ASTNode): PsiElement {
        return RellTypes.Factory.createElement(node)
    }

    companion object {
        val FILE = IFileElementType(RellLanguage.INSTANCE)
    }
}
