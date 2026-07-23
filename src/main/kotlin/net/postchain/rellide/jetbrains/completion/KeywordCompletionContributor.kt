package net.postchain.rellide.jetbrains.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import net.postchain.rellide.jetbrains.language.RellLanguage
import net.postchain.rellide.jetbrains.language.parser.RellLexer
import net.postchain.rellide.jetbrains.language.psi.RellPsiElementTypes

class KeywordCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(RellPsiElementTypes.token(RellLexer.RULE_ID)).withLanguage(RellLanguage.INSTANCE),
            KeywordCompletionProvider()
        )
    }
}

class KeywordCompletionProvider : CompletionProvider<CompletionParameters>() {
    private val keywords = listOf(
        "abstract",
        "and",
        "break",
        "class",
        "continue",
        "create",
        "delete",
        "else",
        "entity",
        "enum",
        "false",
        "for",
        "function",
        "guard",
        "if",
        "import",
        "in",
        "include",
        "index",
        "key",
        "limit",
        "list",
        "map",
        "module",
        "mutable",
        "namespace",
        "not",
        "null",
        "object",
        "offset",
        "operation",
        "or",
        "override",
        "query",
        "record",
        "return",
        "set",
        "struct",
        "true",
        "update",
        "val",
        "var",
        "virtual",
        "when",
        "while"
    )

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        for (keyword in keywords) {
            result.addElement(LookupElementBuilder.create(keyword).withBoldness(true))
        }
    }

}
