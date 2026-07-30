package net.postchain.rellide.jetbrains.chromia

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RellLspLockfileTest {

    @Test
    fun parsesLockfileLines() {
        val artifacts = RellLspLockfile.parse(
            """
            net.postchain.rell:rell-toolbox-language-server:0.16.0 rell-toolbox-language-server-0.16.0.jar abc123
            org.antlr:antlr4-runtime:4.13.1 antlr4-runtime-4.13.1.jar def456
            """.trimIndent(),
        )
        assertEquals(expected = 2, actual = artifacts.size)
        val lsp = artifacts.first()
        assertEquals(expected = "net.postchain.rell:rell-toolbox-language-server:0.16.0", actual = lsp.gav)
        assertEquals(
            expected = "net/postchain/rell/rell-toolbox-language-server/0.16.0/rell-toolbox-language-server-0.16.0.jar",
            actual = lsp.mavenPath,
        )
        assertEquals(expected = "abc123", actual = lsp.sha256)
    }

    @Test
    fun rejectsMalformedLines() {
        assertFailsWith<IllegalArgumentException> { RellLspLockfile.parse("not a lockfile line") }
        assertFailsWith<IllegalArgumentException> { RellLspLockfile.parse("bad:gav file.jar sha extra") }
        assertFailsWith<IllegalArgumentException> { RellLspLockfile.parse("only-two-parts file.jar") }
    }

    // Guards the build wiring: every supported version below the newest must ship a lockfile that
    // actually pins its own language server.
    @Test
    fun everyOlderSupportedVersionHasACompleteLockfileResource() {
        for (version in RellVersionRegistry.supported.filter { it < RellVersionRegistry.max }) {
            val artifacts = RellLspLockfile.load(version)
            assertTrue(artifacts.isNotEmpty(), "Lockfile for $version is empty")
            assertTrue(
                artifacts.any { it.module == "rell-toolbox-language-server" && it.version == version.toString() },
                "Lockfile for $version does not pin rell-toolbox-language-server:$version",
            )
            for (artifact in artifacts) {
                assertTrue(
                    artifact.sha256.matches(Regex("[0-9a-f]{64}")),
                    "Artifact ${artifact.gav} has a malformed SHA-256: ${artifact.sha256}",
                )
            }
        }
    }
}
