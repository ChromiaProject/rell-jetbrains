package net.postchain.rellide.jetbrains.language

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.CustomFoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import net.postchain.rellide.jetbrains.language.psi.RellTypes


class RellFoldingBuilder : CustomFoldingBuilder(), DumbAware {
    override fun buildLanguageFoldRegions(descriptors: MutableList<FoldingDescriptor>, root: PsiElement, document: Document, quick: Boolean) {
        collectDescriptorsRecursively(root.node, document, descriptors)
    }

    override fun getLanguagePlaceholderText(node: ASTNode, range: TextRange): String {
        val type = node.elementType
        return when (type) {
            RellTypes.X_BLOCK_STMT -> "{...}"
            RellTypes.ML_COMMENT -> "/*...*/"
            RellTypes.X_ENTITY_DEF,
            RellTypes.X_FUNCTION_DEF,
            RellTypes.X_OBJECT_DEF,
            RellTypes.X_STRUCT_DEF,
            RellTypes.X_NAMESPACE_DEF,
            RellTypes.X_ENUM_DEF -> "${node.text.substringBefore("{")} {...} "
            else -> "..."
        }
    }

    override fun isRegionCollapsedByDefault(node: ASTNode) = false

    companion object {

        private fun collectDescriptorsRecursively(node: ASTNode, document: Document, descriptors: MutableList<FoldingDescriptor>) {
            val type = node.elementType
            if (type === RellTypes.X_BLOCK_STMT && spanMultipleLines(node, document) ||
                    type === RellTypes.ML_COMMENT ||
                    type === RellTypes.X_ENTITY_DEF ||
                    type === RellTypes.X_OBJECT_DEF ||
                    type === RellTypes.X_FUNCTION_DEF ||
                    type === RellTypes.X_ENUM_DEF ||
                    type === RellTypes.X_NAMESPACE_DEF ||
                    type === RellTypes.X_STRUCT_DEF) {
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
    }
}
