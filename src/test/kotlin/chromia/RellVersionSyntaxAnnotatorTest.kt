package net.postchain.rellide.jetbrains.chromia

import com.intellij.lang.annotation.HighlightSeverity

class RellVersionSyntaxAnnotatorTest : RellVersionAwareTestCase() {

    private val lambdaDapp = """
        module;
        function apply(f: (integer) -> integer, x: integer): integer = f(x);
        query test_it(): integer = apply(x -> x + 1, 7);
    """.trimIndent()

    // The daemon only runs external annotators on files without PSI syntax errors, and with the
    // 0.16.1 and 0.16.3 grammars identical no input can be version-broken yet PSI-clean — so the
    // collect-and-parse pipeline is exercised directly instead of through doHighlighting.
    fun testAnnotatorParsesOlderSupportedVersionWithItsOwnGrammar() {
        myFixture.addFileToProject("chromia.yml", "compile:\n  rellVersion: \"0.16.1\"\n")
        val psi = myFixture.configureByText("main.rell", "module;\nfunction broken( { }\n")
        val annotator = RellVersionSyntaxAnnotator()

        val info = annotator.collectInformation(psi)
        assertNotNull("Files of an older supported version must be collected", info)
        assertEquals(RellVersion(0, 16, 1), info!!.version)

        val result = annotator.doAnnotate(info)!!
        assertTrue("Expected the 0.16.1 parser to report syntax errors, got none", result.errors.isNotEmpty())
    }

    fun testAnnotatorSkipsNewestVersionFiles() {
        myFixture.addFileToProject("chromia.yml", "compile:\n  rellVersion: \"0.16.3\"\n")
        val psi = myFixture.configureByText("main.rell", lambdaDapp)
        assertNull(
            "Newest-version files are covered by the editor PSI; the annotator must not collect them",
            RellVersionSyntaxAnnotator().collectInformation(psi),
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
        for ((dir, version) in mapOf("legacy" to "0.14.5", "dropped" to "0.16.0")) {
            myFixture.addFileToProject("$dir/chromia.yml", "compile:\n  rellVersion: \"$version\"\n")
            myFixture.configureFromExistingVirtualFile(
                myFixture.addFileToProject("$dir/main.rell", lambdaDapp).virtualFile,
            )
            val versionErrors = myFixture.doHighlighting(HighlightSeverity.ERROR)
                .filter { it.description?.contains("Not valid in Rell") == true }
            assertTrue(
                "Unsupported version $version ceases all version diagnostics, got: $versionErrors",
                versionErrors.isEmpty(),
            )
        }
    }
}
