package net.postchain.rellide.jetbrains.editorconfig

import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RellConfigReloadNotificationProviderTest : BasePlatformTestCase() {

    private val provider = RellConfigReloadNotificationProvider()

    fun testNoBarWhileDocumentMatchesDisk() {
        val config = configFile(".rell_lint")
        assertNull(provider.collectNotificationData(project, config))
    }

    fun testBarAppearsOnUnsavedEditAndDisappearsOnSave() {
        val config = configFile(".rell_lint")
        val document = FileDocumentManager.getInstance().getDocument(config)!!

        WriteCommandAction.runWriteCommandAction(project) {
            document.setText("[*.rell]\nrule_prefer_empty=false\n")
        }
        assertNotNull("Unsaved config edit must show the reload bar", provider.collectNotificationData(project, config))

        runWriteAction { FileDocumentManager.getInstance().saveDocument(document) }
        assertNull("Saving applies the change; the bar must go away", provider.collectNotificationData(project, config))
    }

    fun testBarOnFormatterConfigToo() {
        val config = configFile(".rell_format")
        val document = FileDocumentManager.getInstance().getDocument(config)!!

        WriteCommandAction.runWriteCommandAction(project) {
            document.setText("[*.rell]\nmax_line_width=100\n")
        }
        assertNotNull(provider.collectNotificationData(project, config))
    }

    fun testNoBarOnOtherFiles() {
        val other = myFixture.addFileToProject("some.editorconfig", "[*]\nindent_size=2\n").virtualFile
        myFixture.openFileInEditor(other)
        val document = FileDocumentManager.getInstance().getDocument(other)!!
        WriteCommandAction.runWriteCommandAction(project) {
            document.setText("[*]\nindent_size=4\n")
        }
        assertNull(provider.collectNotificationData(project, other))
    }

    private fun configFile(name: String): VirtualFile {
        val file = myFixture.addFileToProject(name, "[*.rell]\nrule_unused_variable=true\n").virtualFile
        myFixture.openFileInEditor(file)
        return file
    }
}
