package net.postchain.rellide.jetbrains.editorconfig

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationProvider
import com.intellij.ui.EditorNotifications
import java.util.function.Function
import javax.swing.JComponent

/**
 * Reload bar on `.rell_lint` and `.rell_format` editors, mirroring
 * [net.postchain.rellide.jetbrains.chromia.ChromiaConfigReloadNotificationProvider]: the language
 * server reads these configs from disk and reloads on saved-file change events, so an edited
 * buffer takes effect only once it is saved. The bar appears while the buffer has unsaved changes
 * and offers a one-click save; saving any other way applies the change just the same.
 */
class RellConfigReloadNotificationProvider : EditorNotificationProvider {

    override fun collectNotificationData(
        project: Project,
        file: VirtualFile,
    ): Function<in FileEditor, out JComponent?>? {
        if (file.name !in RELL_CONFIG_FILE_NAMES) return null
        val documentManager = FileDocumentManager.getInstance()
        val document = documentManager.getCachedDocument(file) ?: return null
        if (!documentManager.isDocumentUnsaved(document)) return null

        return Function { _ ->
            val panel = EditorNotificationPanel(EditorNotificationPanel.Status.Info)
            panel.text = "${file.name} changed. Changes apply when the file is saved"
            panel.createActionLabel("Save and apply") {
                ApplicationManager.getApplication().runWriteAction {
                    documentManager.saveDocument(document)
                }
                EditorNotifications.getInstance(project).updateNotifications(file)
            }
            panel
        }
    }
}

/** Refreshes the reload bar as the user types in a Rell config file. */
class RellConfigDocumentTracker : ProjectActivity {

    override suspend fun execute(project: Project) {
        EditorFactory.getInstance().eventMulticaster.addDocumentListener(
            object : DocumentListener {
                override fun documentChanged(event: DocumentEvent) {
                    val file = FileDocumentManager.getInstance().getFile(event.document) ?: return
                    if (file.name !in RELL_CONFIG_FILE_NAMES) return
                    if (!project.isDisposed) {
                        EditorNotifications.getInstance(project).updateNotifications(file)
                    }
                }
            },
            project,
        )
    }
}
