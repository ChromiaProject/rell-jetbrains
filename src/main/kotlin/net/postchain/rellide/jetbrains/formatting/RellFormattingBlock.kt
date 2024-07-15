package net.postchain.rellide.jetbrains.formatting

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.TokenType
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.formatter.FormatterUtil
import com.intellij.psi.tree.IElementType
import net.postchain.rellide.jetbrains.language.psi.RellTypes.*
import java.util.*

class RellFormattingBlock(
    private val astNode: ASTNode,
    private val alignment: Alignment?,
    private val indent: Indent,
    private val wrap: Wrap?,
    private val codeStyleSettings: CodeStyleSettings,
    private val spacingBuilder: SpacingBuilder
) : ASTBlock {
    private val nodeSubBlocks: List<Block> by lazy { buildSubBlocks() }
    private val isNodeIncomplete: Boolean by lazy { FormatterUtil.isIncomplete(node) }

    private val blockTypes = listOf(
        X_BLOCK_STMT,
        X_STATEMENT_REF,
        X_EXPRESSION_REF,
        X_REL_CLAUSE,
        X_WHEN_STMT_CASE,
        X_WHEN_EXPR_CASE,
        X_AT_EXPR_WHERE
    )

    override fun getSubBlocks(): List<Block> = nodeSubBlocks

    private fun buildSubBlocks(): List<Block> {
        val blocks = ArrayList<Block>()
        var child = astNode.firstChildNode
        while (child != null) {
            val childType = child.elementType
            if (child.textRange.length == 0) {
                child = child.treeNext
                continue
            }
            if (childType === TokenType.WHITE_SPACE) {
                child = child.treeNext
                continue
            }
            val e = buildSubBlock(child)
            blocks.add(e)
            child = child.treeNext
        }
        return Collections.unmodifiableList(blocks)
    }

    private fun buildSubBlock(child: ASTNode): Block {
        val indent = calculateIndent(child)
        return RellFormattingBlock(child, alignment, indent, null, codeStyleSettings, spacingBuilder)
    }

    private fun calculateIndent(child: ASTNode): Indent {
        val childType = child.elementType
        val type = astNode.elementType
        val parent = astNode.treeParent
        val parentType = parent?.elementType

        val result = when {
            // When statement
            type == X_WHEN_CONDITION_EXPR -> Indent.getNoneIndent()

            // Enum
            type == X_NAME && parentType == X_ENUM_DEF -> Indent.getNormalIndent()

            // At expression from block
            parentType == X_AT_EXPR_FROM -> Indent.getNormalIndent()

            // Block types
            child is PsiComment && type in blockTypes -> Indent.getNormalIndent()
            childType in blockTypes -> Indent.getNormalIndent()

            // Namespace
            type == X_ANNOTATED_DEF && parentType == X_NAMESPACE_DEF -> Indent.getNormalIndent()

            // Entity/Struct
            type == X_ATTRIBUTE_DEFINITION && parentType == X_STRUCT_DEF -> Indent.getNormalIndent()

            // Object
            type == X_ATTRIBUTE_DEFINITION && parentType == X_OBJECT_DEF -> Indent.getNormalIndent()

            // At expression what block item
            type == X_AT_EXPR_WHAT_COMPLEX_ITEM -> Indent.getNormalIndent()

            // Return type indentation of function/operation/query
            type == X_TYPE && isTopLevelDefinition(parentType) -> {
                if (astNode.treeNext?.treeNext?.findChildByType(X_FUNCTION_BODY_SHORT) != null) {
                    Indent.getNormalIndent()
                } else {
                    Indent.getSpaceIndent(8)
                }
            }

            // Formal parameters
            type == X_FORMAL_PARAMETER -> Indent.getNormalIndent()

            type == X_BASE_EXPR_TAIL -> Indent.getNormalIndent()

            // What section
            type == X_AT_EXPR_WHAT || type == X_UPDATE_WHAT_EXPR -> Indent.getNormalIndent()

            // Function body
            type == X_FUNCTION_BODY_SHORT -> {
                if (parent?.treePrev?.treePrev?.elementType == X_TYPE) {
                    Indent.getSpaceIndent(8)
                } else {
                    Indent.getNormalIndent()
                }
            }

            // Annotation parameters
            type == X_ANNOTATION_ARG -> Indent.getNormalIndent()

            // Return block with named parameters
            type == X_CALL_ARG || type == X_CREATE_EXPR_ARG -> {
                if (astNode.firstChildNode.elementType == X_NAME) {
                    Indent.getNormalIndent()
                } else {
                    Indent.getNoneIndent()
                }
            }

            type == X_CALL_ARG_VALUE -> Indent.getNormalIndent()

            // Tuple expression
            type == X_TUPLE_EXPR_FIELD -> Indent.getNormalIndent()

            else -> Indent.getNoneIndent()
        }
        return result
    }

    private fun isTopLevelDefinition(type: IElementType?): Boolean {
        return type != null && type in listOf(
            X_OP_DEF,
            X_QUERY_DEF,
            X_FUNCTION_DEF
        );
    }

    private val indentBlockTypes = listOf(
        X_STRUCT_DEF,
        X_ENUM_DEF,
        X_ENTITY_BODY_FULL,
        X_FUNCTION_DEF,
        X_BLOCK_STMT,
        X_NAMESPACE_DEF,
        X_WHEN_STMT,
        X_AT_EXPR_WHERE,
        X_AT_EXPR_WHAT_COMPLEX,
        X_AT_EXPR_FROM
    )

    private fun newChildIndent(childIndex: Int): Indent? {
        return when (node.elementType) {
            in indentBlockTypes -> Indent.getNormalIndent()
            else -> Indent.getNoneIndent()
        }
    }

    override fun getNode(): ASTNode = astNode
    override fun getTextRange(): TextRange = astNode.textRange
    override fun getWrap(): Wrap? = wrap
    override fun getIndent(): Indent? = indent
    override fun getAlignment(): Alignment? = alignment

    override fun getSpacing(child1: Block?, child2: Block): Spacing? {
        return spacingBuilder.getSpacing(this, child1, child2)
    }

    override fun getChildAttributes(newChildIndex: Int): ChildAttributes =
        ChildAttributes(newChildIndent(newChildIndex), null)

    override fun isIncomplete(): Boolean = isNodeIncomplete

    override fun isLeaf(): Boolean = astNode.firstChildNode == null
}
