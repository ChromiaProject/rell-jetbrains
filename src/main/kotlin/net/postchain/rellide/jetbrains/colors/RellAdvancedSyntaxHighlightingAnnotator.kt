package net.postchain.rellide.jetbrains.colors

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.util.descendantsOfType
import com.intellij.psi.util.parentOfType
import net.postchain.rellide.jetbrains.language.psi.RellXAnonAttrHeader
import net.postchain.rellide.jetbrains.language.psi.RellXAnyDef
import net.postchain.rellide.jetbrains.language.psi.RellXBaseExprHead
import net.postchain.rellide.jetbrains.language.psi.RellXBaseExprTail
import net.postchain.rellide.jetbrains.language.psi.RellXBaseExprTailMember
import net.postchain.rellide.jetbrains.language.psi.RellXBinaryOperator
import net.postchain.rellide.jetbrains.language.psi.RellXCallArgs
import net.postchain.rellide.jetbrains.language.psi.RellXEntityDef
import net.postchain.rellide.jetbrains.language.psi.RellXEnumDef
import net.postchain.rellide.jetbrains.language.psi.RellXFormalParameter
import net.postchain.rellide.jetbrains.language.psi.RellXFunctionDef
import net.postchain.rellide.jetbrains.language.psi.RellXImportDef
import net.postchain.rellide.jetbrains.language.psi.RellXIncludeDef
import net.postchain.rellide.jetbrains.language.psi.RellXName
import net.postchain.rellide.jetbrains.language.psi.RellXNameExpr
import net.postchain.rellide.jetbrains.language.psi.RellXNameTypeAttrHeader
import net.postchain.rellide.jetbrains.language.psi.RellXNamespaceDef
import net.postchain.rellide.jetbrains.language.psi.RellXObjectDef
import net.postchain.rellide.jetbrains.language.psi.RellXOpDef
import net.postchain.rellide.jetbrains.language.psi.RellXQualifiedName
import net.postchain.rellide.jetbrains.language.psi.RellXQueryDef
import net.postchain.rellide.jetbrains.language.psi.RellXStructDef
import net.postchain.rellide.jetbrains.language.psi.RellXType
import net.postchain.rellide.jetbrains.language.psi.RellXVarDeclarator

class RellAdvancedSyntaxHighlightingAnnotator : Annotator {
    private val logicalOperators = setOf("and", "or", "not")

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (holder.isBatchMode) return
        var color = when {
            element.text in logicalOperators -> RellColor.KEYWORD
            element is RellXName -> highlightNames(element)
            element is RellXBaseExprHead || element is RellXBaseExprTail  -> highlightCalls(element)
            else -> null
        } ?: return

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .textAttributes(color.textAttributesKey).create()
    }

    private fun highlightCalls(element: PsiElement): RellColor? =
        if (element?.nextSibling?.descendantsOfType<RellXCallArgs>()?.firstOrNull() != null) {
            RellColor.FUNCTION_CALL
        } else {
            null
        }

    private fun highlightNames(element: RellXName): RellColor? {
        val parent = element.parent
        val grandparent = parent?.parent
        return when {
            // AnyDef names
            grandparent is RellXFunctionDef ||
                    grandparent is RellXQueryDef ||
                    grandparent is RellXOpDef ||
                    grandparent is RellXEntityDef ||
                    grandparent is RellXObjectDef ||
                    parent is RellXStructDef ||
                    parent is RellXEnumDef ||
                    grandparent is RellXNamespaceDef ||
                    grandparent is RellXIncludeDef -> RellColor.FUNCTION_DECLARATION

            // Local variables
            parent is RellXQualifiedName && element.parentOfType<RellXVarDeclarator>() != null -> RellColor.LOCAL_VARIABLE

            // Function parameters
            parent is RellXNameTypeAttrHeader && element.parentOfType<RellXFormalParameter>() != null -> RellColor.PARAMETER
            grandparent is RellXAnonAttrHeader && element.parentOfType<RellXFormalParameter>() != null -> RellColor.PARAMETER

            // TODO: Instance fields / State variables
            else -> null
        }
    }
}