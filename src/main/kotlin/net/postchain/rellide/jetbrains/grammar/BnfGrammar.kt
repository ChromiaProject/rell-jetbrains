package net.postchain.rellide.jetbrains.grammar

import net.postchain.rell.base.utils.grammar.GrammarUtils

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.ParserReference
import net.postchain.rell.base.compiler.parser.RellToken
import net.postchain.rell.base.compiler.parser.S_Grammar
import net.postchain.rell.base.utils.LateInit
import org.apache.commons.collections4.MapUtils

fun main() {
    generateHeader()
    generateNonterminals()
    generateFooter()
}

private fun generateHeader() {
    val tokenizer = S_Grammar.tokenizer
    val text = """
        {
          parserClass="net.postchain.rellide.jetbrains.language.parser.RellParser"
          parserUtilClass="net.postchain.rellide.jetbrains.language.parser.RellParserUtil"

          extends="com.intellij.extapi.psi.ASTWrapperPsiElement"

          psiClassPrefix="Rell"
          psiImplClassSuffix="Impl"
          psiPackage="net.postchain.rellide.jetbrains.language.psi"
          psiImplPackage="net.postchain.rellide.jetbrains.language.psi.impl"

          elementTypeHolderClass="net.postchain.rellide.jetbrains.language.psi.RellTypes"
          elementTypeClass="net.postchain.rellide.jetbrains.language.psi.RellElementType"
          tokenTypeClass="net.postchain.rellide.jetbrains.language.psi.RellTokenType"

          psiImplUtilClass="net.postchain.rellide.jetbrains.language.psi.impl.RellPsiImplUtil"

          tokens=[
            space='regexp:\s+'
            booleanLiteral='regexp:true|false'

            SL_COMMENT="regexp://.*"
            ML_COMMENT="regexp:/\*([^*]|[\r\n]|(\*+([^*/]|[\r\n])))*\*+/"

            WS="regexp:(' '|'\t'|'\r'|'\n')+"
            ${tokenizer.tkIdentifier.name}='regexp:[a-zA-Z_${'$'}][a-zA-Z_${'$'}0-9]*'
            DECNUM="regexp:[0-9]+"
            DECIMAL="regexp:[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?"
            HEXDIG="regexp:[0-9]|[A-F]|[a-f]"
            ${tokenizer.tkByteArray.name}="regexp:x(('[_0-9a-fA-F]+')|(\"[_0-9a-fA-F]+\"))"
            STRBAD="regexp:\\|'\u0000' .. '\u001F'"

            ${tokenizer.tkString.name}="regexp:(\"([^\"\r\n\\]|\\.)*\")|('([^'\r\n\\]|\\.)*')"
          ]
        }
    """.trimIndent()

    println(text.trim())
}

private fun generateFooter() {
    val tokenizer = S_Grammar.tokenizer
    val text = """
        COMMON_INT ::= DECNUM | '0' 'x' HEXDIG+;
        ${tokenizer.tkBigInteger.name} ::= COMMON_INT 'L';
        ${tokenizer.tkInteger.name} ::= COMMON_INT;

        STRCHAR ::= '\t' | '\\' ('b'|'t'|'n'|'f'|'r'|'"'|"'"|'\\' | 'u' HEXDIG HEXDIG HEXDIG HEXDIG)
        
        X_tkElse ::= 'else'
        X_tkLimit ::= 'limit'
        X_tkOffset ::= 'offset'
        X_tkArrow ::= '->'
        X_tkRPAR ::= ')'
        X_tkRCURL ::= '}'
        X_tkRBRACK ::= ']'
    """.trimIndent()

    println(text.trim())
}

private fun generateTerminals() {
    val tokenizer = S_Grammar.tokenizer

    val text = """
            terminal ML_COMMENT: '/*' -> '*/';
            terminal SL_COMMENT: '//' !('\n'|'\r')* ('\r'? '\n')?;
            terminal WS: (' '|'\t'|'\r'|'\n')+;

            terminal ${tokenizer.tkIdentifier.name}: ('A'..'Z'|'a'..'z'|'_') ('A'..'Z'|'a'..'z'|'_'|'0'..'9')*;

            terminal DECNUM: ('0'..'9')+;
            terminal EXPONENT: ('E'|'e') ('+'|'-')? DECNUM ;
            terminal ${tokenizer.tkDecimal.name}: DECNUM? '.' DECNUM EXPONENT? | DECNUM EXPONENT ;

            terminal HEXDIG: '0'..'9'|'A'..'F'|'a'..'f';
            terminal COMMON_INT: DECNUM | '0' 'x' HEXDIG+;
            terminal ${tokenizer.tkBigInteger.name}: COMMON_INT 'L';
            terminal ${tokenizer.tkInteger.name}: COMMON_INT;

            terminal ${tokenizer.tkByteArray.name}: 'x' (('\'' (HEXDIG HEXDIG)* '\'') | ('"' (HEXDIG HEXDIG)* '"'));

            terminal STRCHAR: '\t' | '\\' ('b'|'t'|'n'|'f'|'r'|'"'|"'"|'\\' | 'u' HEXDIG HEXDIG HEXDIG HEXDIG);
            terminal STRBAD: '\\' | '\u0000' .. '\u001F';
            terminal ${tokenizer.tkString.name}: '"' ( STRCHAR | !('"'|STRBAD) )*  '"' | "'" ( STRCHAR | !("'"|STRBAD) )* "'";
    """.trimIndent()

    println(text.trim())
}

private fun generateNonterminals() {
    val nonterms = BnfNontermGen.generateNonterms()
    for (nt in nonterms) {
        println(nt.generate())
    }
}

fun generateBnfActions(): Map<String, BnfActionEx> {
    val actions = BnfNontermGen.generateActions()
    return actions
}

private object BnfNontermGen {
    private val tokenizer = S_Grammar.tokenizer

    private val literalTokens = (tokenizer.tkKeywords.values + tokenizer.tkDelims).map { Pair(it.name, it) }.toMap()
    private val specialTokens = listOf(tokenizer.tkString, tokenizer.tkByteArray).map { it.name }

    private val kParsers = GrammarUtils.getParsers()

    private val xNonterms = mutableMapOf<String, BnfNonterm>()
    private val xTokenNonterms = mutableMapOf<String, BnfNonterm>()

    private val actions = mutableMapOf<String, BnfActionEx>()

    fun generateNonterms(): List<BnfNonterm> {
        generate()
        return xNonterms.values.toList()
    }

    fun generateActions(): Map<String, BnfActionEx> {
        generate()
        return actions.toMap()
    }

    private fun generate() {
        convertNonterm("rootParser")
    }

    private fun convertNonterm(name: String): BnfExpr {
        val xName = nontermNameToBnf(name)

        if (name !in xNonterms) {
            val parser = kParsers.getValue(name)
            val gram = BnfGramExprGen.createBnfGramExpr(parser)

            val xNt = BnfNonterm(xName)
            xNonterms[name] = xNt
            xNt.prods.set(convertProds(xName, gram))
        }

        return BnfExpr_Symbol(xName)
    }

    private fun convertProds(xNonterm: String, gram: BnfGramExpr): List<BnfProd> {
        val subs = if (gram is BnfGramExpr_Or) gram.subs else listOf(gram)
        return subs.mapIndexed { i, sub -> convertProd(xNonterm, sub, i, subs.size) }
    }

    private fun convertProd(xNonterm: String, gram: BnfGramExpr, index: Int, count: Int): BnfProd {
        val type = if (count == 1) xNonterm else "${xNonterm}_$index"

        val (inner, transform) = if (gram is BnfGramExpr_Map) {
            Pair(gram.sub, gram.transform)
        } else {
            Pair(gram, null)
        }

        val subs = if (inner is BnfGramExpr_And) inner.subs else listOf(inner)
        if (subs.size == 1) {
            val sub = subs[0]
            if (sub is BnfGramExpr_Nonterm && transform == null) {
                val expr = convertExpr(sub, null)
                return BnfProd(null, expr)
            } else if (sub is BnfGramExpr_Token) {
                val expr = convertExpr(sub, null)
                if (transform == null) {
                    val tokenType = createTokenType(sub.name)
                    return BnfProd(tokenType, expr)
                } else {
                    val token = if (sub.name in specialTokens) sub.name else null
                    createAction(type, BnfAction_Token(token), transform)
                    return BnfProd(type, expr)
                }
            }
        }

        val exprs = mutableListOf<BnfExpr>()
        val attrs = mutableListOf<BnfAttr>()
        for (sub in subs) {
            var attr: BnfAttr? = null
            if (sub.hasValue()) {
                val attrName = "" + Character.forDigit(10 + attrs.size, 36)
                attr = BnfAttr(attrName, sub.many())
                attrs.add(attr)
            }
            val expr = convertExpr(sub, attr)
            exprs.add(expr)
        }

        val expr = if (exprs.size == 1) exprs[0] else BnfExpr_And(exprs)
        createAction(type, BnfAction_General(attrs), transform)

        return BnfProd(type, expr)
    }

    private fun convertExpr(gram: BnfGramExpr, attr: BnfAttr?): BnfExpr {
        return when (gram) {
            is BnfGramExpr_Token -> convertToken(gram.name, attr)
            is BnfGramExpr_Nonterm -> createAttr(convertNonterm(gram.name), attr)
            is BnfGramExpr_Skip -> convertExpr(gram.sub, null)
            is BnfGramExpr_And -> {
                if (attr != null) {
                    val values = gram.subs.filter { it.hasValue() }
                    check(values.size <= 1) { "More than one element has value" }
                }
                BnfExpr_And(gram.subs.map { convertExpr(it, if (it.hasValue()) attr else null) })
            }
            is BnfGramExpr_Or -> BnfExpr_Or(gram.subs.map { convertExpr(it, attr) })
            is BnfGramExpr_Opt -> BnfExpr_Opt(convertExpr(gram.sub, attr))
            is BnfGramExpr_Rep -> {
                val term = convertExpr(gram.term, attr)
                if (gram.sep == null) {
                    BnfExpr_Rep(term, gram.zero)
                } else {
                    val sep = convertExpr(gram.sep, null)
                    val rep = BnfExpr_Rep(BnfExpr_And(listOf(sep, term)), true)
                    val one = BnfExpr_And(listOf(term, rep))
                    if (gram.zero) BnfExpr_Opt(one) else one
                }
            }
            is BnfGramExpr_Map -> throw IllegalStateException("Map not expected here")
        }
    }

    private fun convertToken(name: String, attr: BnfAttr?): BnfExpr {
        if (attr == null) {
            return convertToken0(name)
        }

        if (name !in xTokenNonterms) {
            val ntName = termNameToBnf("tk$name")
            check(ntName !in xNonterms)
            val expr = convertToken0(name)
            val type = createTokenType(name)
            val prod = BnfProd(type, expr)
            val nonterm = BnfNonterm(ntName)
            nonterm.prods.set(listOf(prod))
            xTokenNonterms[name] = nonterm
            xNonterms[ntName] = nonterm
        }

        val nonterm = xTokenNonterms.getValue(name)
        val expr = BnfExpr_Symbol(nonterm.name)
        return createAttr(expr, attr)
    }

    private fun convertToken0(name: String): BnfExpr {
        val token = literalTokens[name]
        return if (token != null) BnfExpr_Token(token.token.pattern) else BnfExpr_Symbol(name)
    }

    private fun createTokenType(name: String): String {
        val tail = if (name !in specialTokens) "" else name.toLowerCase().capitalize()
        val type = nontermNameToBnf("token$tail")
        if (type !in actions) {
            val token = if (name in specialTokens) name else null
            actions[type] = BnfActionEx(BnfAction_Token(token), null)
        }
        return type
    }

    private fun createAttr(expr: BnfExpr, attr: BnfAttr?): BnfExpr {
        return if (attr == null) expr else BnfExpr_Attr(attr.name, attr.many, expr)
    }

    private fun createAction(type: String, action: BnfAction, transform: ((Any) -> Any)?) {
        check(type !in actions) { type }
        actions[type] = BnfActionEx(action, transform)
    }
}

private object BnfGramExprGen {
    private val parsers = GrammarUtils.getParsers()
    private val nonterms = MapUtils.invertMap(parsers).toMap()

    fun createBnfGramExpr(parser: Any): BnfGramExpr {
        return createBnfGramExpr0(parser)
    }

    private fun createBnfGramExprSub(parser: Any): BnfGramExpr {
        val nt = nonterms[parser]
        if (nt != null) {
            return BnfGramExpr_Nonterm(nt)
        }
        return createBnfGramExpr0(parser)
    }

    private fun createBnfGramExpr0(parser: Any): BnfGramExpr {
        return when (parser) {
            is ParserReference<*> -> createBnfGramExprSub(parser.parser)
            is RellToken -> BnfGramExpr_Token(parser.name)
            is SkipParser -> BnfGramExpr_Skip(createBnfGramExprSub(parser.innerParser))
            is AndCombinator<*> -> BnfGramExpr_And(GrammarUtils.andParsers(parser).map { createBnfGramExprSub(it) })
            is OrCombinator<*> -> BnfGramExpr_Or(GrammarUtils.orParsers(parser).map { createBnfGramExprSub(it) })
            is OptionalCombinator<*> -> BnfGramExpr_Opt(createBnfGramExprSub(parser.parser))
            is SeparatedCombinator<*, *> -> {
                val term = createBnfGramExprSub(parser.termParser)
                val sep = createBnfGramExprSub(parser.separatorParser)
                BnfGramExpr_Rep(term, sep, parser.acceptZero)
            }
            is RepeatCombinator<*> -> {
                check(parser.atLeast >= 0)
                check(parser.atMost == -1)
                BnfGramExpr_Rep(createBnfGramExprSub(parser.parser), null, parser.atLeast == 0)
            }
            is MapCombinator<*, *> -> {
                if (parser.innerParser is SeparatedCombinator<*, *>) {
                    createBnfGramExprSub(parser.innerParser)
                } else {
                    BnfGramExpr_Map(createBnfGramExprSub(parser.innerParser), parser.transform as (Any) -> Any)
                }
            }
            else -> throw IllegalStateException(parser::class.java.simpleName)
        }
    }
}

private fun nontermNameToBnf(name: String): String {
    return "X_" + name.capitalize()
}

private fun termNameToBnf(name: String): String {
    return "X_$name"
}

private class BnfNonterm(val name: String) {
    val prods = LateInit<List<BnfProd>>()
    val terminals = mutableListOf<Pair<String, String>>()
    fun generate(): String {
        val ps = prods.get().joinToString("\n   | ") { it.generate() }
        if(name == "X_IfStmt") {
             return "$name ::= ${ps.replace("'else'", "X_tkElse")}\n"
        }
        return "\n$name ::= $ps\n"
    }
}

private class BnfProd(private val type: String?, private val expr: BnfExpr) {
    fun generate(): String {
        val s = expr.generate()
        return if (type != null) "$s" else s
    }
}

private sealed class BnfExpr {
    abstract fun generate(): String
}

private class BnfExpr_Symbol(private val name: String): BnfExpr() {
    override fun generate() = "$name"
}

private class BnfExpr_Token(private val text: String): BnfExpr() {
    override fun generate() = "'$text'"
}

private class BnfExpr_Or(private val subs: List<BnfExpr>): BnfExpr() {
    override fun generate() = "(" + subs.joinToString(" | ") { it.generate() } + ")"
}

private class BnfExpr_And(private val subs: List<BnfExpr>): BnfExpr() {
    override fun generate() = subs.joinToString(" ") { it.generate() }
}

private class BnfExpr_Rep(private val sub: BnfExpr, private val zero: Boolean): BnfExpr() {
    override fun generate() = "(" + sub.generate() + ")" + (if (zero) "*" else "+")
}

private class BnfExpr_Opt(private val sub: BnfExpr): BnfExpr() {
    override fun generate() = "(" + sub.generate() + ")?"
}

private class BnfExpr_Attr(private val attr: String, private val many: Boolean, private val sub: BnfExpr): BnfExpr() {
    override fun generate(): String {
        val op = if (many) "+=" else "="
//        return "$attr$op" + sub.generate()
        return sub.generate()
    }
}

private sealed class BnfGramExpr {
    abstract fun hasValue(): Boolean
    abstract fun many(): Boolean
}

private class BnfGramExpr_Token(val name: String): BnfGramExpr() {
    override fun hasValue() = true
    override fun many() = false
}

private class BnfGramExpr_Nonterm(val name: String): BnfGramExpr() {
    override fun hasValue() = true
    override fun many() = false
}

private class BnfGramExpr_Skip(val sub: BnfGramExpr): BnfGramExpr() {
    override fun hasValue() = false
    override fun many() = sub.many()
}

private class BnfGramExpr_Map(val sub: BnfGramExpr, val transform: (Any) -> Any): BnfGramExpr() {
    override fun hasValue() = true
    override fun many() = false
}

private class BnfGramExpr_And(val subs: List<BnfGramExpr>): BnfGramExpr() {
    override fun hasValue() = subs.any { it.hasValue() }
    override fun many() = subs.any { it.many() }
}

private class BnfGramExpr_Or(val subs: List<BnfGramExpr>): BnfGramExpr() {
    override fun hasValue() = subs.any { it.hasValue() }
    override fun many() = subs.any { it.many() }
}

private class BnfGramExpr_Opt(val sub: BnfGramExpr): BnfGramExpr() {
    override fun hasValue() = sub.hasValue()
    override fun many() = sub.many()
}

private class BnfGramExpr_Rep(val term: BnfGramExpr, val sep: BnfGramExpr?, val zero: Boolean): BnfGramExpr() {
    override fun hasValue() = term.hasValue()
    override fun many() = true
}
