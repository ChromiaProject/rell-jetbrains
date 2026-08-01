package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFile

class ChromiaConfigReloadNotificationProviderTest : RellVersionAwareTestCase() {

    private val provider = ChromiaConfigReloadNotificationProvider()

    fun testNoBarWhileDocumentMatchesDisk() {
        val config = configFile("clean", "0.16.1")
        assertNull(provider.collectNotificationData(project, config))
    }

    fun testBarAppearsWhenEditedVersionDiffersAndDisappearsOnSave() {
        val config = configFile("edited", "0.16.1")
        val document = FileDocumentManager.getInstance().getDocument(config)!!

        WriteCommandAction.runWriteCommandAction(project) {
            document.setText("compile:\n  rellVersion: \"0.16.2\"\n")
        }
        assertNotNull(
            "Unsaved version change must show the reload bar",
            provider.collectNotificationData(project, config)
        )

        runWriteAction { FileDocumentManager.getInstance().saveDocument(document) }
        assertNull("Saving applies the change; the bar must go away", provider.collectNotificationData(project, config))
    }

    fun testNoBarForNonVersionEdits() {
        val config = configFile("other-edit", "0.16.1")
        val document = FileDocumentManager.getInstance().getDocument(config)!!

        WriteCommandAction.runWriteCommandAction(project) {
            document.setText("compile:\n  rellVersion: \"0.16.1\"\n  source: rell/src\n")
        }
        assertNull(
            "Edits that keep rellVersion unchanged must not show the bar",
            provider.collectNotificationData(project, config),
        )
    }

    fun testBarAppearsOnAlternateSettingsFile() {
        val config = myFixture.addFileToProject(
            "alternate/atbash.yml",
            "blockchains:\n  my_chain:\n    module: main\ncompile:\n  rellVersion: \"0.16.1\"\n",
        ).virtualFile
        myFixture.openFileInEditor(config)
        val document = FileDocumentManager.getInstance().getDocument(config)!!

        WriteCommandAction.runWriteCommandAction(project) {
            document.setText("blockchains:\n  my_chain:\n    module: main\ncompile:\n  rellVersion: \"0.16.2\"\n")
        }
        assertNotNull(
            "A qualifying alternate settings file must get the reload bar",
            provider.collectNotificationData(project, config),
        )
    }

    fun testNoBarOnOtherFiles() {
        val other = myFixture.addFileToProject("misc/config.yml", "compile:\n  rellVersion: \"0.16.1\"\n").virtualFile
        myFixture.openFileInEditor(other)
        val document = FileDocumentManager.getInstance().getDocument(other)!!
        WriteCommandAction.runWriteCommandAction(project) {
            document.setText("compile:\n  rellVersion: \"0.16.2\"\n")
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
