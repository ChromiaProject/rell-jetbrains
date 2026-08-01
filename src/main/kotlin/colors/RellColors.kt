package net.postchain.rellide.jetbrains.colors

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors as Defaults

enum class RellColor(humanName: String, default: TextAttributesKey) {
    LINE_COMMENT("Comments//Comment", Defaults.LINE_COMMENT),
    BLOCK_COMMENT("Comments//BlockComment", Defaults.BLOCK_COMMENT),

    ENTITY_NAME("Types//Entity name", Defaults.CLASS_NAME),
    STRUCT_NAME("Types//Struct name", Defaults.CLASS_NAME),
    OBJECT_NAME("Types//Object name", Defaults.CLASS_NAME),
    NAMESPACE_NAME("Types//Namespace name", Defaults.CLASS_NAME),
    OPERATION_NAME("Types//Operation name", Defaults.FUNCTION_DECLARATION),
    QUERY_NAME("Types//Query name", Defaults.FUNCTION_DECLARATION),
    ENUM_NAME("Types//Enum name", Defaults.CLASS_NAME),
    TYPE_REFERENCE("Types//Type reference", Defaults.CLASS_REFERENCE),

    GLOBAL("Identifiers//Global", Defaults.GLOBAL_VARIABLE),
    CONSTANT("Identifiers//Constant", Defaults.STATIC_FIELD),
    STATE_VARIABLE("Identifiers//State variable", Defaults.INSTANCE_FIELD),
    LOCAL_VARIABLE("Identifiers//Local variable", Defaults.LOCAL_VARIABLE),

    FUNCTION_DECLARATION("Functions//Function declaration", Defaults.FUNCTION_DECLARATION),
    FUNCTION_CALL("Functions//Function call", Defaults.FUNCTION_CALL),
    PARAMETER("Functions//Parameter", Defaults.PARAMETER),

    BRACES("Other//Braces", Defaults.BRACES),
    BRACKETS("Other//Brackets", Defaults.BRACKETS),
    PARENTHESES("Other//Parentheses", Defaults.PARENTHESES),
    SEMICOLON("Other//Semicolon", Defaults.SEMICOLON),
    NUMBER("Other//Number", Defaults.NUMBER),
    STRING("Other//String", Defaults.STRING),
    KEYWORD("Other//Keyword", Defaults.KEYWORD),
    OPERATION_SIGN("Other//Operation signs", Defaults.OPERATION_SIGN),
    ;

    val textAttributesKey = TextAttributesKey.createTextAttributesKey("net.postchain.rellide.jetbrains.$name", default)
    val attributesDescriptor = AttributesDescriptor(humanName, textAttributesKey)
}
