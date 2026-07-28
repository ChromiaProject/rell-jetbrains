package net.postchain.rellide.jetbrains.lsp

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.platform.lsp.api.customization.*
import net.postchain.rellide.jetbrains.colors.RellColor
import org.eclipse.lsp4j.SemanticTokenTypes

object RellLspCustomization : LspCustomization() {
    override val semanticTokensCustomizer: LspSemanticTokensCustomizer = RellSemanticTokensSupport

    // The Rell language server never provides document colors, so keep the feature permanently
    // disabled instead of asking on every highlighting pass.
    override val documentColorCustomizer: LspDocumentColorCustomizer = LspDocumentColorDisabled
}


enum class RellTokenModifier(val modifierStringId: String) {
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


object RellSemanticTokensSupport : LspSemanticTokensSupport() {
    override val tokenModifiers: List<String>
        get() = super.tokenModifiers + RellTokenModifier.entries.map { it.modifierStringId }

    override fun getTextAttributesKey(
        tokenType: String,
        modifiers: List<String>,
    ): TextAttributesKey? {
        val textAttributes = when (tokenType) {
            SemanticTokenTypes.Keyword -> RellColor.KEYWORD.textAttributesKey
            SemanticTokenTypes.Namespace -> RellColor.NAMESPACE_NAME.textAttributesKey
            SemanticTokenTypes.Type -> RellColor.TYPE_REFERENCE.textAttributesKey
            SemanticTokenTypes.Enum -> RellColor.ENUM_NAME.textAttributesKey
            SemanticTokenTypes.EnumMember -> RellColor.STATE_VARIABLE.textAttributesKey

            SemanticTokenTypes.Variable -> {
                when {
                    modifiers.contains(RellTokenModifier.GLOBAL_CONSTANT.modifierStringId) ->
                        RellColor.GLOBAL.textAttributesKey

                    modifiers.contains(RellTokenModifier.PARAMETER.modifierStringId) ->
                        RellColor.PARAMETER.textAttributesKey

                    modifiers.contains(RellTokenModifier.LOCAL_VAL.modifierStringId) ->
                        RellColor.CONSTANT.textAttributesKey

                    modifiers.contains(RellTokenModifier.LOCAL_VAR.modifierStringId) ->
                        RellColor.LOCAL_VARIABLE.textAttributesKey

                    modifiers.contains(RellTokenModifier.NAMED_ARGUMENT.modifierStringId) ->
                        RellColor.PARAMETER.textAttributesKey

                    else -> null
                }
            }

            SemanticTokenTypes.Class -> {
                when {
                    modifiers.contains(RellTokenModifier.ENTITY.modifierStringId) ->
                        RellColor.ENTITY_NAME.textAttributesKey

                    modifiers.contains(RellTokenModifier.OBJECT.modifierStringId) ->
                        RellColor.OBJECT_NAME.textAttributesKey

                    else -> null
                }
            }

            SemanticTokenTypes.Property -> RellColor.STATE_VARIABLE.textAttributesKey
            SemanticTokenTypes.Struct -> RellColor.STRUCT_NAME.textAttributesKey

            SemanticTokenTypes.Function -> {
                when {
                    modifiers.contains(RellTokenModifier.CALL.modifierStringId) ->
                        RellColor.FUNCTION_CALL.textAttributesKey

                    modifiers.contains(RellTokenModifier.OPERATION.modifierStringId) ->
                        RellColor.OPERATION_NAME.textAttributesKey

                    modifiers.contains(RellTokenModifier.QUERY.modifierStringId) ->
                        RellColor.QUERY_NAME.textAttributesKey

                    else -> RellColor.FUNCTION_DECLARATION.textAttributesKey
                }
            }

            SemanticTokenTypes.Parameter -> RellColor.PARAMETER.textAttributesKey
            else -> null
        }

        return textAttributes ?: super.getTextAttributesKey(tokenType, modifiers)
    }
}
