package net.postchain.rellide.jetbrains.colors

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import net.postchain.rellide.jetbrains.language.psi.RellXAnyDef
import net.postchain.rellide.jetbrains.language.psi.RellXName
import net.postchain.rellide.jetbrains.language.psi.RellXQualifiedName
import net.postchain.rellide.jetbrains.language.psi.RellXVarDeclarator

class RellAdvancedSyntaxHighlightingAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (holder.isBatchMode) return
        var color = when {
            element is RellXName -> highlightNames(element)
            else -> null
        } ?: return

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .textAttributes(color.textAttributesKey).create()
    }

    private fun highlightNames(element: RellXName): RellColor? {
        return when {
            // AnyDef names
            element?.parent?.parent is RellXAnyDef -> RellColor.FUNCTION_DECLARATION
            element?.parent is RellXQualifiedName && element?.parent?.parent?.parent is RellXAnyDef -> RellColor.FUNCTION_DECLARATION

            // Local variables
            element?.parent is RellXQualifiedName && element.parentOfType<RellXVarDeclarator>() != null -> RellColor.LOCAL_VARIABLE

            // TODO: Function calls

            // TODO: Function parameters

            // TODO: Parameters

            // TODO: Instance fields / State variables

            // TODO: Constants
            else -> null
        }
    }
}