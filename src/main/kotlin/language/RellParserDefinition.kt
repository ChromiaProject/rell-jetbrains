package net.postchain.rellide.jetbrains.language

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.injected.editor.VirtualFileWindow
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.resolve.FileContextUtil
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import net.postchain.rellide.jetbrains.language.parser.RellParser
import net.postchain.rellide.jetbrains.language.psi.RellFile
import net.postchain.rellide.jetbrains.language.psi.RellPsiElementTypes
import org.antlr.intellij.adaptor.parser.ANTLRParserAdaptor
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.tree.ParseTree
import java.util.function.Function

class RellParserDefinition : ParserDefinition {
    override fun createLexer(project: Project): Lexer = RellLexerAdapter()

    override fun createParser(project: Project): PsiParser =
        object : ANTLRParserAdaptor(RellLanguage.INSTANCE, RellParser(null)) {
            override fun parse(parser: Parser, root: IElementType): ParseTree =
                (parser as RellParser).file()

            override fun parseCandidates(
                root: IElementType,
                builder: PsiBuilder,
            ): List<Function<Parser, ParseTree>> =
                if (isInjectedFragment(builder)) FRAGMENT_ROOTS else super.parseCandidates(root, builder)
        }

    override fun getFileNodeType(): IFileElementType = FILE
    override fun getCommentTokens(): TokenSet = RellPsiElementTypes.COMMENTS
    override fun getWhitespaceTokens(): TokenSet = RellPsiElementTypes.WHITESPACE
    override fun getStringLiteralElements(): TokenSet = RellPsiElementTypes.STRINGS
    override fun createElement(node: ASTNode): PsiElement = ASTWrapperPsiElement(node)
    override fun createFile(viewProvider: FileViewProvider): PsiFile = RellFile(viewProvider)

}

private val FILE: IFileElementType = IFileElementType(RellLanguage.INSTANCE)

/**
 * Injected Rell is rarely a whole module: a ```rell fence in Markdown usually holds statements and
 * expressions, which `file` (a module header plus definitions) rejects outright. Fragments get the
 * grammar's REPL entry rule as a second shot, so a bare `var n = 7;` stops reading as a syntax
 * error while a fence that really is a module still parses as one — `file` is tried first and wins
 * ties.
 */
private val FRAGMENT_ROOTS: List<Function<Parser, ParseTree>> = listOf(
    Function { (it as RellParser).file() },
    Function { (it as RellParser).replCommand() },
)

/**
 * True while parsing an injected fragment or a code fragment rather than a standalone file.
 * `PsiFile.getContext()` is still null this early — the host element is attached after the fragment
 * is parsed — so injection is recognised by the view provider's window file instead.
 */
private fun isInjectedFragment(builder: PsiBuilder): Boolean {
    val containing = builder.getUserData(FileContextUtil.CONTAINING_FILE_KEY) ?: return false
    return containing.viewProvider.virtualFile is VirtualFileWindow || containing.context != null
}
