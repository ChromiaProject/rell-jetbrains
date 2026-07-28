package net.postchain.rellide.jetbrains.chromia

import org.junit.Assert.assertTrue
import org.junit.Test

class VersionedRellParsersTest {

    private val lambdaDapp = """
        module;
        function apply(f: (integer) -> integer, x: integer): integer = f(x);
        query test_it(): integer = apply(x -> x + 1, 7);
    """.trimIndent()

    // Guards the registry against version bumps: when supportedRellVersions grows, the previous
    // newest version needs a generated grammar and an entry in VersionedRellParsers.
    @Test
    fun everySupportedVersionBelowNewestHasAParser() {
        for (version in RellVersionRegistry.supported.filter { it < RellVersionRegistry.max }) {
            assertTrue("No versioned parser for supported Rell $version", VersionedRellParsers.supports(version))
        }
    }

    @Test
    fun lambdaIsASyntaxErrorInRell0160() {
        val errors = VersionedRellParsers.parse(RellVersion(0, 16, 0), lambdaDapp)
        assertTrue("Expected the 0.16.0 grammar to reject the lambda, got no errors", errors.isNotEmpty())
        assertTrue(
            "Expected an error on the lambda line, got: $errors",
            errors.any { it.line == 3 },
        )
    }

    @Test
    fun functionTypesAreFineInRell0160() {
        val noLambda = """
            module;
            function apply(f: (integer) -> integer, x: integer): integer = f(x);
        """.trimIndent()
        val errors = VersionedRellParsers.parse(RellVersion(0, 16, 0), noLambda)
        assertTrue("Function types predate 0.16.1 and must parse, got: $errors", errors.isEmpty())
    }
}
