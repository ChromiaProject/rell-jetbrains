package net.postchain.rellide.jetbrains.chromia

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class RellLspLockfileTest {

    @Test
    fun parsesLockfileLines() {
        val artifacts = RellLspLockfile.parse(
            """
            net.postchain.rell:rell-toolbox-language-server:0.16.0 rell-toolbox-language-server-0.16.0.jar abc123
            org.antlr:antlr4-runtime:4.13.1 antlr4-runtime-4.13.1.jar def456
            """.trimIndent(),
        )
        assertEquals(2, artifacts.size)
        val lsp = artifacts.first()
        assertEquals("net.postchain.rell:rell-toolbox-language-server:0.16.0", lsp.gav)
        assertEquals(
            "net/postchain/rell/rell-toolbox-language-server/0.16.0/rell-toolbox-language-server-0.16.0.jar",
            lsp.mavenPath,
        )
        assertEquals("abc123", lsp.sha256)
    }

    @Test
    fun rejectsMalformedLines() {
        assertThrows(IllegalArgumentException::class.java) { RellLspLockfile.parse("not a lockfile line") }
        assertThrows(IllegalArgumentException::class.java) { RellLspLockfile.parse("bad:gav file.jar sha extra") }
        assertThrows(IllegalArgumentException::class.java) { RellLspLockfile.parse("only-two-parts file.jar") }
    }

    // Guards the build wiring: every supported version below the newest must ship a lockfile that
    // actually pins its own language server.
    @Test
    fun everyOlderSupportedVersionHasACompleteLockfileResource() {
        for (version in RellVersionRegistry.supported.filter { it < RellVersionRegistry.max }) {
            val artifacts = RellLspLockfile.load(version)
            assertTrue("Lockfile for $version is empty", artifacts.isNotEmpty())
            assertTrue(
                "Lockfile for $version does not pin rell-toolbox-language-server:$version",
                artifacts.any { it.module == "rell-toolbox-language-server" && it.version == version.toString() },
            )
            for (artifact in artifacts) {
                assertTrue(
                    "Artifact ${artifact.gav} has a malformed SHA-256: ${artifact.sha256}",
                    artifact.sha256.matches(Regex("[0-9a-f]{64}")),
                )
            }
        }
    }
}
