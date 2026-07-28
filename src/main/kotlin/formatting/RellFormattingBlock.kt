package net.postchain.rellide.jetbrains.formatting

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.TokenType
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.formatter.FormatterUtil
import com.intellij.psi.tree.IElementType
import net.postchain.rellide.jetbrains.language.psi.RellPsiElementTypes
import java.util.*

/**
 * Indentation is derived purely from bracket nesting (`{}`, `[]`, `()`) rather than from specific
 * parser rules. The 0.16.0 ANTLR grammar uses labeled alternatives (e.g. `if`/`when` statements)
 * that do not produce dedicated rule nodes, so a delimiter-depth model indents them all uniformly
 * without depending on the tree shape.
 */
class RellFormattingBlock(
    private val astNode: ASTNode,
    private val alignment: Alignment?,
    private val indent: Indent,
    private val wrap: Wrap?,
    private val codeStyleSettings: CodeStyleSettings,
    private val spacingBuilder: SpacingBuilder,
) : ASTBlock {
    private val nodeSubBlocks: List<Block> by lazy { buildSubBlocks() }
    private val isNodeIncomplete: Boolean by lazy { FormatterUtil.isIncomplete(node) }

    override fun getSubBlocks(): List<Block> = nodeSubBlocks

    private fun buildSubBlocks(): List<Block> {
        val blocks = ArrayList<Block>()
        var depth = 0
        var child = astNode.firstChildNode
        while (child != null) {
            val type = child.elementType
            if (child.textRange.isEmpty || type === TokenType.WHITE_SPACE) {
                child = child.treeNext
                continue
            }
            val isClosing = type in CLOSING
            val childIndent = when {
                // A closing delimiter aligns with its enclosing opener.
                isClosing -> Indent.getNoneIndent()
                depth > 0 -> Indent.getNormalIndent()
                else -> Indent.getNoneIndent()
            }
            blocks.add(RellFormattingBlock(child, alignment, childIndent, null, codeStyleSettings, spacingBuilder))

            if (type in OPENING) depth++
            if (isClosing && depth > 0) depth--
            child = child.treeNext
        }
        return Collections.unmodifiableList(blocks)
    }

    override fun getNode(): ASTNode = astNode
    override fun getTextRange(): TextRange = astNode.textRange
    override fun getWrap(): Wrap? = wrap
    override fun getIndent(): Indent = indent
    override fun getAlignment(): Alignment? = alignment

    override fun getSpacing(child1: Block?, child2: Block): Spacing? =
        spacingBuilder.getSpacing(this, child1, child2)

    override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
        val opensBlock = astNode.getChildren(null).any { it.elementType in OPENING }
        val childIndent = if (opensBlock) Indent.getNormalIndent() else Indent.getNoneIndent()
        return ChildAttributes(childIndent, null)
    }

    override fun isIncomplete(): Boolean = isNodeIncomplete

    override fun isLeaf(): Boolean = astNode.firstChildNode == null

    private companion object {
        val OPENING: Set<IElementType> = setOf(
            RellPsiElementTypes.token(RellPsiElementTypes.LCURL),
            RellPsiElementTypes.token(RellPsiElementTypes.LBRACK),
            RellPsiElementTypes.token(RellPsiElementTypes.LPAR),
        )
        val CLOSING: Set<IElementType> = setOf(
            RellPsiElementTypes.token(RellPsiElementTypes.RCURL),
            RellPsiElementTypes.token(RellPsiElementTypes.RBRACK),
            RellPsiElementTypes.token(RellPsiElementTypes.RPAR),
        )
    }
}
