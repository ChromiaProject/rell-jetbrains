package net.postchain.rellide.jetbrains.language

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.CustomFoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import net.postchain.rellide.jetbrains.language.parser.RellLexer
import net.postchain.rellide.jetbrains.language.parser.RellParser
import net.postchain.rellide.jetbrains.language.psi.RellPsiElementTypes

class RellFoldingBuilder : CustomFoldingBuilder(), DumbAware {
    override fun buildLanguageFoldRegions(
        descriptors: MutableList<FoldingDescriptor>,
        root: PsiElement,
        document: Document,
        quick: Boolean,
    ) {
        collectDescriptorsRecursively(root.node, document, descriptors)
    }

    override fun getLanguagePlaceholderText(node: ASTNode, range: TextRange): String {
        return when (node.elementType) {
            BLOCK_STMT -> "{...}"
            ML_COMMENT -> "/*...*/"
            in DEF_TYPES -> "${node.text.substringBefore("{")} {...} "
            else -> "..."
        }
    }

    override fun isRegionCollapsedByDefault(node: ASTNode) = false

}

private val BLOCK_STMT: IElementType = RellPsiElementTypes.rule(RellParser.RULE_blockStmt)
private val ML_COMMENT: IElementType = RellPsiElementTypes.token(RellLexer.RULE_ML_COMMENT)

private val DEF_TYPES: Set<IElementType> = setOf(
    RellPsiElementTypes.rule(RellParser.RULE_entityDef),
    RellPsiElementTypes.rule(RellParser.RULE_functionDef),
    RellPsiElementTypes.rule(RellParser.RULE_objectDef),
    RellPsiElementTypes.rule(RellParser.RULE_structDef),
    RellPsiElementTypes.rule(RellParser.RULE_namespaceDef),
    RellPsiElementTypes.rule(RellParser.RULE_enumDef),
)

private fun collectDescriptorsRecursively(
    node: ASTNode,
    document: Document,
    descriptors: MutableList<FoldingDescriptor>,
) {
    val type = node.elementType
    if (type === BLOCK_STMT && spanMultipleLines(node, document) || type === ML_COMMENT || type in DEF_TYPES) {
        descriptors.add(FoldingDescriptor(node, node.textRange))
    }
    for (child in node.getChildren(null)) {
        collectDescriptorsRecursively(child, document, descriptors)
    }
}

private fun spanMultipleLines(node: ASTNode, document: Document): Boolean {
    val range = node.textRange
    return document.getLineNumber(range.startOffset) < document.getLineNumber(range.endOffset)
}
