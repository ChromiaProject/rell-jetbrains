package net.postchain.rellide.jetbrains.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import net.postchain.rellide.jetbrains.language.RellLanguage
import net.postchain.rellide.jetbrains.language.psi.RellTypes

class KeywordCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(RellTypes.ID).withLanguage(RellLanguage.INSTANCE),
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
        keywords.forEach {
            result.addElement(LookupElementBuilder.create(it).withBoldness(true))
        }
    }

}
