package net.postchain.rellide.jetbrains.chromia

import org.junit.Assert.assertTrue
import org.junit.Test

class VersionedRellParsersTest {

    // Guards the registry against version bumps: when supportedRellVersions grows, the previous
    // newest version needs a generated grammar and an entry in VersionedRellParsers.
    @Test
    fun everySupportedVersionBelowNewestHasAParser() {
        for (version in RellVersionRegistry.supported.filter { it < RellVersionRegistry.max }) {
            assertTrue("No versioned parser for supported Rell $version", VersionedRellParsers.supports(version))
        }
    }

    @Test
    fun lambdaParsesInRell0161() {
        val lambdaDapp = """
            module;
            function apply(f: (integer) -> integer, x: integer): integer = f(x);
            query test_it(): integer = apply(x -> x + 1, 7);
        """.trimIndent()
        val errors = VersionedRellParsers.parse(RellVersion(0, 16, 1), lambdaDapp)
        assertTrue("Lambdas entered the grammar in 0.16.1 and must parse, got: $errors", errors.isEmpty())
    }

    @Test
    fun syntaxErrorsCarryTheirLine() {
        val broken = """
            module;
            function broken( { }
        """.trimIndent()
        val errors = VersionedRellParsers.parse(RellVersion(0, 16, 1), broken)
        assertTrue("Expected the 0.16.1 grammar to reject broken syntax, got no errors", errors.isNotEmpty())
        assertTrue("Expected an error on the broken line, got: $errors", errors.any { it.line == 2 })
    }
}
