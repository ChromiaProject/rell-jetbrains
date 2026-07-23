package net.postchain.rellide.jetbrains.chromia

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RellVersionSyntaxAnnotatorTest : BasePlatformTestCase() {

    private val lambdaDapp = """
        module;
        function apply(f: (integer) -> integer, x: integer): integer = f(x);
        query test_it(): integer = apply(x -> x + 1, 7);
    """.trimIndent()

    override fun setUp() {
        super.setUp()
        RellVersionResolver.getInstance(project).dropCaches()
    }

    fun testLambdaFlaggedAsErrorInDeclared0160Project() {
        myFixture.addFileToProject("chromia.yml", "compile:\n  rellVersion: \"0.16.0\"\n")
        myFixture.configureByText("main.rell", lambdaDapp)
        val versionErrors = myFixture.doHighlighting(HighlightSeverity.ERROR)
            .filter { it.description?.contains("Not valid in Rell 0.16.0") == true }
        assertTrue(
            "Expected a version-syntax error on the lambda in a 0.16.0 project",
            versionErrors.isNotEmpty(),
        )
    }

    fun testLambdaCleanInDeclared0161Project() {
        myFixture.addFileToProject("chromia.yml", "compile:\n  rellVersion: \"0.16.1\"\n")
        myFixture.configureByText("main.rell", lambdaDapp)
        val errors = myFixture.doHighlighting(HighlightSeverity.ERROR)
        assertTrue("Expected no errors for a lambda in a 0.16.1 project, got: $errors", errors.isEmpty())
    }

    fun testLambdaCleanWithoutChromiaYml() {
        myFixture.configureByText("main.rell", lambdaDapp)
        val errors = myFixture.doHighlighting(HighlightSeverity.ERROR)
        assertTrue("Expected no errors without a chromia.yml, got: $errors", errors.isEmpty())
    }

    fun testNoVersionAnnotationsForUnsupportedVersion() {
        myFixture.addFileToProject("chromia.yml", "compile:\n  rellVersion: \"0.14.5\"\n")
        myFixture.configureByText("main.rell", lambdaDapp)
        val versionErrors = myFixture.doHighlighting(HighlightSeverity.ERROR)
            .filter { it.description?.contains("Not valid in Rell") == true }
        assertTrue(
            "Unsupported versions cease all version diagnostics, got: $versionErrors",
            versionErrors.isEmpty(),
        )
    }
}
