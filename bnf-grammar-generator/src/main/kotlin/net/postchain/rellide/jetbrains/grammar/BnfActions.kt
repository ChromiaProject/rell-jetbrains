package net.postchain.rellide.jetbrains.grammar

import net.postchain.rell.base.utils.RellVersions
import net.postchain.rell.base.utils.grammar.GrammarUtils

// TODO: Implement this when compiler integration will be neccessary.
// Currently it's semi baked, just to make grammar generation compilable.
fun main() {
    BnfGenUtils.printHeader()

    println("package net.postchain.rellide.jetbrains.language.compiler;\n")
    println("import com.intellij.psi.PsiElement;\n")
    println("import  net.postchain.rellide.jetbrains.language.psi.RellTypes;\n")

    println("public final class BnfToRell {")

    val actions = generateBnfActions()

    val transforms = actions.filterValues { it.transform != null }.keys
    for (type in transforms) {
        val name = typeToTransform(type)
        println("    private static final RellcTransformer $name = RellcUtils.transformer(\"$type\");")
    }
    if (!transforms.isEmpty()) println()

    println("    public static Object process(BnfToRellContext ctx, EObject obj) {")
    println("        if (obj == null) return null;\n")

    println("        switch (obj.eClass().getClassifierID()) {")

    for ((type, action) in actions) {
        val id = typeToId(type)
        println("            case $id: {")

        val attrs = action.action.generate(type)
        val attrsStr = attrs.joinToString(", ")

        val tupleExpr = "RellcUtils.tuple($attrsStr)"

        val expr = if (action.transform == null) tupleExpr else {
            println("                Object tup = $tupleExpr;")
            val transformName = typeToTransform(type)
            "$transformName.transform(ctx, obj, tup)"
        }

        println("                return $expr;")
        println("            }")
    }

    println("            default:")
    println("                throw new IllegalArgumentException(obj.eClass().getName());")
    println("        }")
    println("    }")
    println("}")
}

private fun typeToId(type: String): String {
    return "RellPackage." + camelCaseToUpper(type)
}

private fun typeToTransform(type: String): String {
    return "TRANS_" + camelCaseToUpper(type)
}

// Must use same algorithm as the Bnf code generator.
private fun camelCaseToUpper(s: String): String {
    val b = StringBuilder(s.length * 2)
    for (i in s.indices) {
        val c = s[i]
        if (Character.isUpperCase(c) && i > 0 && Character.isLowerCase(s[i - 1])) b.append('_')
        if (c == '_' && i > 0 && Character.isUpperCase(s[i - 1])) continue
        b.append(Character.toUpperCase(c))
    }
    return b.toString()
}

class BnfActionEx(val action: BnfAction, val transform: ((Any) -> Any)?)

sealed class BnfAction {
    abstract fun generate(type: String): List<String>
}

class BnfAction_Token(private val name: String?): BnfAction() {
    override fun generate(type: String): List<String> {
        val tail = if (name == null) "" else name.lowercase().replaceFirstChar(Char::uppercaseChar)
        println("                Object a = RellcUtils.token$tail(obj);")
        return listOf("a")
    }
}

class BnfAttr(val name: String, val many: Boolean)

class BnfAction_General(private val attrs: List<BnfAttr>): BnfAction() {
    override fun generate(type: String): List<String> {
        val fullType = "net.postchain.rellide.xtext.rell.$type"
        println("                $fullType node = ($fullType) obj;")

        for (attr in attrs) {
            val getter = "get" + attr.name.uppercase()
            val expr = if (attr.many) {
                "RellcUtils.processList(ctx, node.$getter())"
            } else {
                "RellcUtils.processObject(ctx, node.$getter())"
            }
            println("                Object ${attr.name} = $expr;")
        }

        return attrs.map { it.name }
    }
}

object BnfGenUtils {
    fun printHeader() {
        val timestamp = System.currentTimeMillis()
        val timestampStr = GrammarUtils.timestampToString(timestamp)
        println("// Rell version: ${RellVersions.VERSION_STR}")
        println("// Timestamp: $timestamp ($timestampStr)")
    }
}
