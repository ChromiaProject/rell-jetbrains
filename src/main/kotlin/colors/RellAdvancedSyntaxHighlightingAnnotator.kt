package net.postchain.rellide.jetbrains.colors

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import net.postchain.rellide.jetbrains.language.parser.RellLexer
import net.postchain.rellide.jetbrains.language.parser.RellParser
import net.postchain.rellide.jetbrains.language.psi.RellPsiElementTypes
import net.postchain.rellide.jetbrains.language.psi.ancestorOfRule
import net.postchain.rellide.jetbrains.language.psi.isRule

/**
 * Semantic coloring on top of the lexer-based [net.postchain.rellide.jetbrains.language.RellSyntaxHighlighter].
 *
 * Works off the generic ANTLR PSI tree: names are [RellLexer.RULE_ID] leaves, classified by their
 * enclosing parser rules. Mirrors the previous GrammarKit-based behavior against the 0.16.0 grammar.
 */
class RellAdvancedSyntaxHighlightingAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (holder.isBatchMode) return

        val color = when {
            element.isRule(RellParser.RULE_primaryType) -> collectionTypeColor(element)
            element.elementType == ID_TOKEN -> colorForName(element)
            else -> null
        } ?: return

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .textAttributes(color.textAttributesKey)
            .create()
    }

    /** `virtual<T>` / `struct<T>` / `name<T>` type expressions render as collection types. */
    private fun collectionTypeColor(primaryType: PsiElement): RellColor? {
        val hasTypeArgs = primaryType.node.getChildren(null).any { it.text == "<" }
        return if (hasTypeArgs) RellColor.COLLECTION_TYPE else null
    }

    private fun colorForName(id: PsiElement): RellColor? {
        val parent = id.parent ?: return null
        // namespace/function names are qualifiedName children; everything else uses the name directly.
        val owner = if (parent.isRule(RellParser.RULE_qualifiedName)) parent.parent else parent

        return when {
            // Definition names.
            owner.isRule(RellParser.RULE_functionDef) -> RellColor.FUNCTION_DECLARATION
            owner.isRule(RellParser.RULE_queryDef) -> RellColor.QUERY_NAME
            owner.isRule(RellParser.RULE_opDef) -> RellColor.OPERATION_NAME
            owner.isRule(RellParser.RULE_entityDef) -> RellColor.ENTITY_NAME
            owner.isRule(RellParser.RULE_objectDef) -> RellColor.OBJECT_NAME
            owner.isRule(RellParser.RULE_structDef) -> RellColor.STRUCT_NAME
            owner.isRule(RellParser.RULE_namespaceDef) -> RellColor.NAMESPACE_NAME
            owner.isRule(RellParser.RULE_enumDef) -> enumColor(id)

            // Attribute / parameter / local-variable names (all flow through attrHeader).
            id.ancestorOfRule(RellParser.RULE_attrHeader) != null -> attrHeaderColor(id)

            // A bare name immediately followed by a call argument list is a function call.
            isCalledName(id) -> RellColor.FUNCTION_CALL

            // A name used inside a type position is a type reference.
            id.ancestorOfRule(RellParser.RULE_primaryType) != null -> RellColor.TYPE_REFERENCE

            else -> null
        }
    }

    private fun enumColor(id: PsiElement): RellColor {
        // `enum Name { MEMBER, ... }` — the name directly follows the `enum` keyword; the rest are members.
        var prev = id.prevSibling
        while (prev != null && prev.text.isBlank()) prev = prev.prevSibling
        return if (prev?.text == "enum") RellColor.ENUM_NAME else RellColor.STATE_VARIABLE
    }

    private fun attrHeaderColor(id: PsiElement): RellColor? = when {
        id.ancestorOfRule(RellParser.RULE_formalParameter) != null -> RellColor.PARAMETER
        id.ancestorOfRule(RellParser.RULE_baseAttributeDefinition) != null -> RellColor.STATE_VARIABLE
        id.ancestorOfRule(RellParser.RULE_varDeclarator) != null -> RellColor.LOCAL_VARIABLE
        else -> null
    }

    private fun isCalledName(id: PsiElement): Boolean {
        val head = id.ancestorOfRule(RellParser.RULE_baseExprHead) ?: return false
        var next = head.nextSibling
        while (next != null && next.text.isBlank()) next = next.nextSibling
        return next?.isRule(RellParser.RULE_callArgs) == true
    }

    private companion object {
        val ID_TOKEN = RellPsiElementTypes.token(RellLexer.RULE_ID)
    }
}
