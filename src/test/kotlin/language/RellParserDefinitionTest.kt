package net.postchain.rellide.jetbrains.language

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Rell injected into a Markdown fence is a snippet, not a module, so the parser falls back to the
 * grammar's REPL entry rule for fragments. Standalone `.rell` files keep the strict `file` rule.
 */
class RellParserDefinitionTest : BasePlatformTestCase() {

    private fun errorsInFence(vararg snippetLines: String): List<String> {
        val snippet = snippetLines.joinToString("\n")
        val markdown = "Docs:\n\n```rell\n$snippet\n```\n"
        myFixture.configureByText("doc.md", markdown)

        val offsetInSnippet = markdown.indexOf(snippet) + 1
        val injected = InjectedLanguageManager.getInstance(project)
            .findInjectedElementAt(myFixture.file, offsetInSnippet)
            ?.containingFile
        assertNotNull("Markdown did not inject Rell into the ```rell fence", injected)
        assertEquals(RellLanguage.INSTANCE, injected!!.language)

        return errorsIn(injected)
    }

    private fun errorsIn(file: PsiFile): List<String> =
        PsiTreeUtil.findChildrenOfType(file, PsiErrorElement::class.java).map { it.errorDescription }

    fun testStatementSnippetInMarkdownFenceParsesCleanly() {
        val errors = errorsInFence(
            "var n = 7;",
            "val xs = [1, 2, 3];",
            "xs.add(4);",
            "n = 8;",
        )
        assertEmpty("Statement-only snippet must not be reported as a broken module: $errors", errors)
    }

    fun testTrailingExpressionSnippetParsesCleanly() {
        val errors = errorsInFence("val xs = [1, 2, 3];", "xs.size()")
        assertEmpty("A REPL-style trailing expression must parse: $errors", errors)
    }

    fun testModuleSnippetInMarkdownFenceStillParsesAsModule() {
        val errors = errorsInFence("module;", "entity user { name; }")
        assertEmpty("A fence holding a whole module must still parse as one: $errors", errors)
    }

    fun testBrokenSnippetStillReportsErrors() {
        val errors = errorsInFence("function broken( {")
        assertNotEmpty("Genuinely broken snippets must still be reported", errors)
    }

    fun testStandaloneFileKeepsStrictModuleGrammar() {
        val file = myFixture.configureByText("main.rell", "var n = 7;\n")
        assertNotEmpty(
            "A statement at the top level of a real .rell file is still a syntax error",
            errorsIn(file),
        )
    }

    private fun assertNotEmpty(message: String, values: List<String>) =
        assertTrue(message, values.isNotEmpty())
}
