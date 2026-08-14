package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspClientDescriptor
import com.intellij.platform.lsp.api.LspIntegrationProvider
import net.postchain.rellide.jetbrains.lsp.RellLspClientDescriptor
import net.postchain.rellide.jetbrains.lsp.RellLspIntegrationProvider
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

class RellLspRoutingTest : RellVersionAwareTestCase() {

    // Guards plugin.xml against losing the LSP integration: the provider that starts the bundled
    // server for every Rell file must stay registered.
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

    // An `order` naming an extension id that does not exist is dropped silently, leaving the
    // widget wherever the platform happens to put it.
    fun testSettingsWidgetIsOrderedAgainstARealPlatformWidget() {
        val pluginXml = javaClass.classLoader.getResourceAsStream("META-INF/plugin.xml")
            ?: error("plugin.xml not on test classpath")
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pluginXml)

        val factories = document.getElementsByTagName("statusBarWidgetFactory")
        val ours = (0 until factories.length).map { factories.item(it) as Element }
            .single { it.getAttribute("id") == ChromiaSettingsStatusBarWidget.WIDGET_ID }

        // Platform ids from PlatformLangPlugin.xml; the position widget is "Position", not
        // "positionWidget".
        val knownPlatformWidgetIds = setOf("Position", "LineSeparator", "Encoding", "ReadOnlyAttribute")
        val anchor = ours.getAttribute("order").substringAfter("before ").substringAfter("after ").trim()
        assertTrue("order anchors an unknown widget id: $anchor", anchor in knownPlatformWidgetIds)
    }

    fun testDescriptorServesEveryRellFileWhateverVersionItDeclares() {
        val descriptor = RellLspClientDescriptor.bundled(project)
        assertTrue(descriptor.isSupportedFile(rellFile("no-config", null)))
        assertTrue(descriptor.isSupportedFile(rellFile("newest", "0.16.6")))
        assertTrue(descriptor.isSupportedFile(rellFile("older", "0.16.1")))
        assertTrue(
            "A version predating the old compatibility floor is served like any other",
            descriptor.isSupportedFile(rellFile("ancient", "0.14.5")),
        )
        assertTrue(
            "A version newer than the bundled one is served; the server clamps it",
            descriptor.isSupportedFile(rellFile("newer-than-bundled", "0.17.0")),
        )
    }

    fun testProviderStartsTheServerForEveryRellFile() {
        assertTrue(startedFor(rellFile("p-newest", "0.16.6")))
        assertTrue(startedFor(rellFile("p-no-config", null)))
        assertTrue(startedFor(rellFile("p-older", "0.16.1")))
        assertTrue("No declared version leaves a file unserved", startedFor(rellFile("p-ancient", "0.14.5")))
    }

    fun testProviderStartsNothingForForeignFiles() {
        assertFalse(
            "Non-Rell files get no server",
            startedFor(myFixture.addFileToProject("p-foreign/readme.md", "hi").virtualFile),
        )
    }

    fun testConflictingSettingsFilesResolveToTheChosenVersion() {
        myFixture.addFileToProject(
            "p-conflict/a.yml",
            "blockchains:\n  my_chain:\n    module: main\ncompile:\n  rellVersion: \"0.16.1\"\n",
        )
        myFixture.addFileToProject(
            "p-conflict/b.yml",
            "blockchains:\n  my_chain:\n    module: main\ncompile:\n  rellVersion: \"0.16.6\"\n",
        )
        val file = myFixture.addFileToProject("p-conflict/src/main.rell", "module;\n").virtualFile

        assertEquals(
            "The default choice (newest in-scope version) governs",
            RellVersion(0, 16, 6),
            RellVersionResolver.getInstance(project).resolve(file).effectiveVersion,
        )

        val directory = file.parent.parent.path
        ChromiaActiveSettings.getInstance(project).setActive(directory, "a.yml")
        try {
            assertEquals(
                "The active settings file governs once chosen",
                RellVersion(0, 16, 1),
                RellVersionResolver.getInstance(project).resolve(file).effectiveVersion,
            )
        } finally {
            ChromiaActiveSettings.getInstance(project).setActive(directory, null)
        }
    }

    /** Runs the provider's routing for [file] and reports whether it started the server. */
    private fun startedFor(file: VirtualFile): Boolean {
        var started: LspClientDescriptor? = null
        val starter = object : LspIntegrationProvider.LspClientStarter {
            override fun ensureClientStarted(descriptor: LspClientDescriptor) {
                started = descriptor
            }
        }
        RellLspIntegrationProvider().route(project, file, starter)
        return started is RellLspClientDescriptor
    }

    private fun rellFile(dir: String, rellVersion: String?): VirtualFile {
        if (rellVersion != null) {
            myFixture.addFileToProject("$dir/chromia.yml", "compile:\n  rellVersion: \"$rellVersion\"\n")
        }
        return myFixture.addFileToProject("$dir/src/main.rell", "module;\n").virtualFile
    }
}
