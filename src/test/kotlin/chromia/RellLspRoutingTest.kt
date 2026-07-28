package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspClientDescriptor
import com.intellij.platform.lsp.api.LspIntegrationProvider
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.io.createDirectories
import net.postchain.rellide.jetbrains.lsp.RellLspClientDescriptor
import net.postchain.rellide.jetbrains.lsp.RellLspIntegrationProvider
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import kotlin.io.path.deleteIfExists
import kotlin.io.path.writeText

class RellLspRoutingTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        RellVersionResolver.getInstance(project).dropCaches()
    }

    // Guards plugin.xml against losing the LSP integration: the provider that routes every Rell
    // file to the server of its version must stay registered.
    fun testPluginXmlRegistersTheLspIntegrationProvider() {
        val pluginXml = javaClass.classLoader.getResourceAsStream("META-INF/plugin.xml")
            ?: error("plugin.xml not on test classpath")
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pluginXml)

        val providers = document.getElementsByTagName("platform.lsp.integrationProvider")
        val implementations = (0 until providers.length).map {
            (providers.item(it) as Element).getAttribute("implementation")
        }

        assertContainsElements(implementations, RellLspIntegrationProvider::class.java.name)
    }

    fun testNewestDescriptorServesDefaultsAndNewestButNotOldOrCeased() {
        val descriptor = RellLspClientDescriptor.newest(project)
        assertTrue(descriptor.isSupportedFile(rellFile("no-config", null)))
        assertTrue(descriptor.isSupportedFile(rellFile("newest", "0.16.2")))
        assertTrue("Clamped versions run the newest toolchain", descriptor.isSupportedFile(rellFile("clamped", "0.17.0")))
        assertFalse("Older versions have their own server", descriptor.isSupportedFile(rellFile("older", "0.16.1")))
        assertFalse("Below the floor no server may match", descriptor.isSupportedFile(rellFile("ceased", "0.14.5")))
    }

    fun testVersionedDescriptorRequiresExactVersion() {
        val descriptor = RellLspClientDescriptor.versioned(project, RellVersion(0, 16, 1))
        assertTrue(descriptor.isSupportedFile(rellFile("versioned", "0.16.1")))
        assertFalse("Wrong version must not match", descriptor.isSupportedFile(rellFile("newest2", "0.16.2")))
        assertFalse("Ceased versions must not match", descriptor.isSupportedFile(rellFile("ceased2", "0.14.5")))
    }

    // Distinct presentable names are what keep the per-version clients apart: the platform
    // identifies a client by descriptor class + presentable name + roots.
    fun testDescriptorsOfDifferentVersionsHaveDistinctPresentableNames() {
        val newest = RellLspClientDescriptor.newest(project)
        for (version in RellVersionRegistry.supported.filter { it < RellVersionRegistry.max }) {
            assertFalse(newest.presentableName == RellLspClientDescriptor.versioned(project, version).presentableName)
        }
    }

    fun testProviderStartsTheNewestServerForNewestAndDefaultFiles() {
        assertEquals(RellVersionRegistry.max, startedVersionFor(rellFile("p-newest", "0.16.2")))
        assertEquals(RellVersionRegistry.max, startedVersionFor(rellFile("p-no-config", null)))
        assertEquals(RellVersionRegistry.max, startedVersionFor(rellFile("p-clamped", "0.17.0")))
    }

    fun testProviderStartsNothingForCeasedVersionsAndForeignFiles() {
        assertNull("Below the floor no server may start", startedVersionFor(rellFile("p-ceased", "0.14.5")))
        assertNull(
            "Non-Rell files get no server",
            startedVersionFor(myFixture.addFileToProject("p-foreign/readme.md", "hi").virtualFile),
        )
    }

    fun testProviderStartsAVersionedServerOnlyWhenItsRuntimeIsReady() {
        val version = RellVersion(0, 16, 1)
        val file = rellFile("p-versioned", "0.16.1")

        assertNull("Runtime not downloaded: must not start", startedVersionFor(file))

        // isRuntimeReady validates the marker against the current lockfile, so the fake marker
        // must carry the real expected content.
        val marker = RellLspRuntimeManager.getInstance().cachedRuntimeDir(version).resolve(".complete")
        marker.parent.createDirectories()
        marker.writeText(RellLspLockfile.load(version).joinToString("\n") { "${it.gav} ${it.sha256}" })

        try {
            RellVersionResolver.getInstance(project).dropCaches()
            assertEquals("Runtime ready: must start", version, startedVersionFor(file))
        } finally {
            marker.deleteIfExists()
        }
    }

    /** Runs the provider's routing for [file] and returns the Rell version it started, if any. */
    private fun startedVersionFor(file: VirtualFile): RellVersion? {
        var started: LspClientDescriptor? = null
        val starter = object : LspIntegrationProvider.LspClientStarter {
            override fun ensureClientStarted(descriptor: LspClientDescriptor) {
                started = descriptor
            }
        }
        RellLspIntegrationProvider().route(project, file, starter)
        return (started as? RellLspClientDescriptor)?.version
    }

    private fun rellFile(dir: String, rellVersion: String?): VirtualFile {
        if (rellVersion != null) {
            myFixture.addFileToProject("$dir/chromia.yml", "compile:\n  rellVersion: \"$rellVersion\"\n")
        }
        return myFixture.addFileToProject("$dir/src/main.rell", "module;\n").virtualFile
    }
}
