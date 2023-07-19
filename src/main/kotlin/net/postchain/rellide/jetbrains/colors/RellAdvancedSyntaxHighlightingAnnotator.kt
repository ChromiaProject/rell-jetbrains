package net.postchain.rellide.jetbrains.colors

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.util.descendantsOfType
import com.intellij.psi.util.parentOfType
import com.intellij.psi.util.siblings
import net.postchain.rellide.jetbrains.language.psi.*

class RellAdvancedSyntaxHighlightingAnnotator : Annotator {
    private val logicalOperators = setOf("and", "or", "not")

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (holder.isBatchMode) return
        var color = when {
            element is RellXTypeRef -> RellColor.TYPE_REFERENCE
            element is RellXGenericType || element is RellXVirtualType -> RellColor.COLLECTION_TYPE
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
            grandparent is RellXFunctionDef -> RellColor.FUNCTION_DECLARATION
            parent is RellXQueryDef -> RellColor.QUERY_NAME
            parent is RellXOpDef -> RellColor.OPERATION_NAME
            parent is RellXEntityDef -> RellColor.ENTITY_NAME
            parent is RellXObjectDef -> RellColor.OBJECT_NAME
            parent is RellXStructDef -> RellColor.STRUCT_NAME
            parent is RellXEnumDef -> getEnumColor(element)
            grandparent is RellXNamespaceDef -> RellColor.NAMESPACE_NAME

            // Local variables
            parent is RellXQualifiedName && element.parentOfType<RellXVarDeclarator>() != null -> RellColor.LOCAL_VARIABLE

            // Function parameters
            parent is RellXNameTypeAttrHeader && element.parentOfType<RellXFormalParameter>() != null -> RellColor.PARAMETER
            grandparent is RellXAnonAttrHeader && element.parentOfType<RellXFormalParameter>() != null -> RellColor.PARAMETER

            // Struct/Entity/Enum/Object attributes
            parent is RellXNameTypeAttrHeader && element.parentOfType<RellXBaseAttributeDefinition>() != null -> RellColor.STATE_VARIABLE
            grandparent is RellXAnonAttrHeader && element.parentOfType<RellXBaseAttributeDefinition>() != null -> RellColor.STATE_VARIABLE

            else -> null
        }
    }

    private fun getEnumColor(element: RellXName): RellColor? {
        return if (element.siblings().find { it.text == "{" } != null) {
            RellColor.ENUM_NAME
        } else {
            RellColor.STATE_VARIABLE
        }
    }
}