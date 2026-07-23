package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.io.createDirectories
import java.nio.file.Files
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import kotlin.io.path.deleteIfExists
import kotlin.io.path.writeText

class RellLspRoutingTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        RellVersionResolver.getInstance(project).dropCaches()
    }

    // Guards plugin.xml against version bumps: every supported version below the newest needs its
    // own <server> and <languageMapping> with a documentMatcher.
    fun testPluginXmlDeclaresAServerPerSupportedVersion() {
        val pluginXml = javaClass.classLoader.getResourceAsStream("META-INF/plugin.xml")
            ?: error("plugin.xml not on test classpath")
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pluginXml)

        val servers = document.getElementsByTagName("server")
        val serverIds = (0 until servers.length).map { (servers.item(it) as Element).getAttribute("id") }
        val mappings = document.getElementsByTagName("languageMapping")
        val mappingsByServer = (0 until mappings.length).associate {
            val element = mappings.item(it) as Element
            element.getAttribute("serverId") to element.getAttribute("documentMatcher")
        }
        val semanticTokenProviders = document.getElementsByTagName("semanticTokensColorsProvider")
        val semanticTokenServerIds = (0 until semanticTokenProviders.length)
            .map { (semanticTokenProviders.item(it) as Element).getAttribute("serverId") }

        assertTrue(RellLspServers.NEWEST_SERVER_ID in serverIds)
        assertTrue(
            "The newest server's mapping needs a documentMatcher (it must not match ceased or older-version files)",
            mappingsByServer[RellLspServers.NEWEST_SERVER_ID].orEmpty().isNotEmpty(),
        )
        for (version in RellVersionRegistry.supported.filter { it < RellVersionRegistry.max }) {
            val id = RellLspServers.versionedServerId(version)
            assertTrue("plugin.xml has no <server id=\"$id\"> for supported Rell $version", id in serverIds)
            assertTrue(
                "plugin.xml has no languageMapping with documentMatcher for $id",
                mappingsByServer[id].orEmpty().isNotEmpty(),
            )
            assertTrue(
                "plugin.xml has no semanticTokensColorsProvider for $id",
                id in semanticTokenServerIds,
            )
        }
    }

    fun testNewestMatcherRoutesDefaultsAndNewestButNotOldOrCeased() {
        val matcher = RellNewestVersionDocumentMatcher()
        assertTrue(matcher.match(rellFile("no-config", null), project))
        assertTrue(matcher.match(rellFile("newest", "0.16.1"), project))
        assertTrue("Clamped versions run the newest toolchain", matcher.match(rellFile("clamped", "0.17.0"), project))
        assertFalse("Older versions have their own server", matcher.match(rellFile("older", "0.16.0"), project))
        assertFalse("Below the floor no server may match", matcher.match(rellFile("ceased", "0.14.5"), project))
    }

    fun testVersionedMatcherRequiresExactVersionAndReadyRuntime() {
        val matcher = Rell0160DocumentMatcher()
        val version = RellVersion(0, 16, 0)
        val file = rellFile("versioned", "0.16.0")

        assertFalse("Runtime not downloaded: must not match", matcher.match(file, project))

        // isRuntimeReady validates the marker against the current lockfile, so the fake marker
        // must carry the real expected content.
        val marker = RellLspRuntimeManager.getInstance().cachedRuntimeDir(version).resolve(".complete")
        marker.parent.createDirectories()
        marker.writeText(RellLspLockfile.load(version).joinToString("\n") { "${it.gav} ${it.sha256}" })

        try {
            RellVersionResolver.getInstance(project).dropCaches()
            assertTrue("Runtime ready: must match", matcher.match(file, project))
            assertFalse("Wrong version must not match", matcher.match(rellFile("newest2", "0.16.1"), project))
            assertFalse("Ceased versions must not match", matcher.match(rellFile("ceased2", "0.14.5"), project))
        } finally {
            marker.deleteIfExists()
        }
    }

    private fun rellFile(dir: String, rellVersion: String?): VirtualFile {
        if (rellVersion != null) {
            myFixture.addFileToProject("$dir/chromia.yml", "compile:\n  rellVersion: \"$rellVersion\"\n")
        }
        return myFixture.addFileToProject("$dir/src/main.rell", "module;\n").virtualFile
    }
}
