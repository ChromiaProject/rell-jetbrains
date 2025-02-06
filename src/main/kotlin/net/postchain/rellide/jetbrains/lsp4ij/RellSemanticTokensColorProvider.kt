package net.postchain.rellide.jetbrains.lsp4ij

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiFile
import com.redhat.devtools.lsp4ij.features.semanticTokens.DefaultSemanticTokensColorsProvider
import net.postchain.rellide.jetbrains.colors.RellColor
import org.eclipse.lsp4j.SemanticTokenTypes


enum class RellTokenModifier(
    val modifierStringId: String
) {
    GLOBAL_CONSTANT("rell-global_constant"),
    LOCAL_VAL("rell-local_val"),
    LOCAL_VAR("rell-local_var"),
    NAMED_ARGUMENT("rell-named_argument"),
    ENTITY("rell-entity"),
    OBJECT("rell-object"),
    QUERY("rell-query"),
    OPERATION("rell-operation"),
    PARAMETER("rell-parameter"),
    CALL("rell-call"),
}


class RellSemanticTokensColorProvider : DefaultSemanticTokensColorsProvider() {
    override fun getTextAttributesKey(
        tokenType: String,
        tokenModifiers: List<String>,
        file: PsiFile
    ): TextAttributesKey? {
        val textAttributes =  when (tokenType) {
            SemanticTokenTypes.Keyword -> RellColor.KEYWORD.textAttributesKey
            SemanticTokenTypes.Namespace -> RellColor.NAMESPACE_NAME.textAttributesKey
            SemanticTokenTypes.Type -> RellColor.TYPE_REFERENCE.textAttributesKey
            SemanticTokenTypes.Enum -> RellColor.ENUM_NAME.textAttributesKey
            SemanticTokenTypes.EnumMember -> RellColor.STATE_VARIABLE.textAttributesKey
            SemanticTokenTypes.Variable -> {
                when {
                    tokenModifiers.contains(RellTokenModifier.GLOBAL_CONSTANT.modifierStringId) -> RellColor.GLOBAL.textAttributesKey
                    tokenModifiers.contains(RellTokenModifier.PARAMETER.modifierStringId) -> RellColor.PARAMETER.textAttributesKey
                    tokenModifiers.contains(RellTokenModifier.LOCAL_VAL.modifierStringId) -> RellColor.CONSTANT.textAttributesKey
                    tokenModifiers.contains(RellTokenModifier.LOCAL_VAR.modifierStringId) -> RellColor.LOCAL_VARIABLE.textAttributesKey
                    tokenModifiers.contains(RellTokenModifier.NAMED_ARGUMENT.modifierStringId) -> RellColor.PARAMETER.textAttributesKey
                    else -> null
                }
            }
            SemanticTokenTypes.Class -> {
                when {
                    tokenModifiers.contains(RellTokenModifier.ENTITY.modifierStringId) -> RellColor.ENTITY_NAME.textAttributesKey
                    tokenModifiers.contains(RellTokenModifier.OBJECT.modifierStringId) -> RellColor.OBJECT_NAME.textAttributesKey
                    else -> null
                }
            }
            SemanticTokenTypes.Property -> RellColor.STATE_VARIABLE.textAttributesKey
            SemanticTokenTypes.Struct -> RellColor.STRUCT_NAME.textAttributesKey
            SemanticTokenTypes.Function -> {
                when {
                    tokenModifiers.contains(RellTokenModifier.CALL.modifierStringId) -> RellColor.FUNCTION_CALL.textAttributesKey
                    tokenModifiers.contains(RellTokenModifier.OPERATION.modifierStringId) -> RellColor.OPERATION_NAME.textAttributesKey
                    tokenModifiers.contains(RellTokenModifier.QUERY.modifierStringId) -> RellColor.QUERY_NAME.textAttributesKey
                    else -> RellColor.FUNCTION_DECLARATION.textAttributesKey
                }

            }
            SemanticTokenTypes.Parameter -> RellColor.PARAMETER.textAttributesKey
            else -> null
        }
        return textAttributes ?: super.getTextAttributesKey(tokenType, tokenModifiers, file)
    }
}
