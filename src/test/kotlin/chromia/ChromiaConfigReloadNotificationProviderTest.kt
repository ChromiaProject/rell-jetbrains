package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class ChromiaConfigReloadNotificationProviderTest : BasePlatformTestCase() {

    private val provider = ChromiaConfigReloadNotificationProvider()

    override fun setUp() {
        super.setUp()
        RellVersionResolver.getInstance(project).dropCaches()
    }

    fun testNoBarWhileDocumentMatchesDisk() {
        val config = configFile("clean", "0.16.0")
        assertNull(provider.collectNotificationData(project, config))
    }

    fun testBarAppearsWhenEditedVersionDiffersAndDisappearsOnSave() {
        val config = configFile("edited", "0.16.0")
        val document = FileDocumentManager.getInstance().getDocument(config)!!

        WriteCommandAction.runWriteCommandAction(project) {
            document.setText("compile:\n  rellVersion: \"0.16.1\"\n")
        }
        assertNotNull("Unsaved version change must show the reload bar", provider.collectNotificationData(project, config))

        runWriteAction { FileDocumentManager.getInstance().saveDocument(document) }
        assertNull("Saving applies the change; the bar must go away", provider.collectNotificationData(project, config))
    }

    fun testNoBarForNonVersionEdits() {
        val config = configFile("other-edit", "0.16.0")
        val document = FileDocumentManager.getInstance().getDocument(config)!!

        WriteCommandAction.runWriteCommandAction(project) {
            document.setText("compile:\n  rellVersion: \"0.16.0\"\n  source: rell/src\n")
        }
        assertNull(
            "Edits that keep rellVersion unchanged must not show the bar",
            provider.collectNotificationData(project, config),
        )
    }

    fun testNoBarOnOtherFiles() {
        val other = myFixture.addFileToProject("misc/config.yml", "compile:\n  rellVersion: \"0.16.0\"\n").virtualFile
        myFixture.openFileInEditor(other)
        val document = FileDocumentManager.getInstance().getDocument(other)!!
        WriteCommandAction.runWriteCommandAction(project) {
            document.setText("compile:\n  rellVersion: \"0.16.1\"\n")
        }
        assertNull(provider.collectNotificationData(project, other))
    }

    fun testExtractDeclaredVersion() {
        assertEquals("0.16.1", ChromiaConfigQuickFix.extractDeclaredVersion("compile:\n  rellVersion: \"0.16.1\"\n"))
        assertEquals("0.16.1", ChromiaConfigQuickFix.extractDeclaredVersion("compile:\n  rellVersion: '0.16.1'\n"))
        assertEquals("0.16.1", ChromiaConfigQuickFix.extractDeclaredVersion("compile:\n  rellVersion: 0.16.1 # x\n"))
        assertNull(ChromiaConfigQuickFix.extractDeclaredVersion("compile:\n  source: src\n"))
        assertNull(ChromiaConfigQuickFix.extractDeclaredVersion("# rellVersion: 0.16.1\ncompile:\n  source: src\n"))
        assertNull(ChromiaConfigQuickFix.extractDeclaredVersion("compile:\n  rellVersion:\n"))
    }

    private fun configFile(dir: String, version: String): VirtualFile {
        val file = myFixture.addFileToProject("$dir/chromia.yml", "compile:\n  rellVersion: \"$version\"\n").virtualFile
        myFixture.openFileInEditor(file)
        return file
    }
}
