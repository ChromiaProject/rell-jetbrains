package net.postchain.rellide.jetbrains

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.components.service
import com.intellij.psi.xml.XmlFile
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.PsiErrorElementUtil
import net.postchain.rellide.jetbrains.services.RellProjectService
import org.w3c.dom.Document
import java.nio.file.Files
import java.nio.file.Paths
import javax.xml.parsers.DocumentBuilderFactory

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class RellPluginTest : BasePlatformTestCase() {

    fun testXMLFile() {
        val psiFile = myFixture.configureByText(XmlFileType.INSTANCE, "<foo>bar</foo>")
        val xmlFile = assertInstanceOf(psiFile, XmlFile::class.java)

        assertFalse(PsiErrorElementUtil.hasErrors(project, xmlFile.virtualFile))

        assertNotNull(xmlFile.rootTag)

        xmlFile.rootTag?.let {
            assertEquals("foo", it.name)
            assertEquals("bar", it.value.text)
        }
    }

    fun testRename() {
        myFixture.testRename("foo.xml", "foo_after.xml", "a2")
    }

    fun testProjectService() {
        val projectService = project.service<RellProjectService>()

        assertNotSame(projectService.getRandomNumber(), projectService.getRandomNumber())
    }

    fun `test change-notes contain current version`() {
        val pluginXmlURI = javaClass.classLoader.getResource("META-INF/plugin.xml")?.toURI()

        val pluginXmlPath = pluginXmlURI?.let { Paths.get(it) } ?: fail("missing plugin.xml under META-INF")
        val document = parseXml(pluginXmlPath.toString())

        val versionNode = document.getElementsByTagName("version").item(0)
        val currentVersion = versionNode?.textContent!!

        val changeNotesNode = document.getElementsByTagName("change-notes").item(0)
        val changeNotesContent = changeNotesNode?.textContent!!

        assertTrue(
                "<change-notes> should contain the current version: $currentVersion",
                changeNotesContent.contains(currentVersion)
        )
    }

    private fun parseXml(filePath: String): Document {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        return builder.parse(Files.newInputStream(Paths.get(filePath)))
    }

    override fun getTestDataPath() = "src/test/testData/rename"
}
