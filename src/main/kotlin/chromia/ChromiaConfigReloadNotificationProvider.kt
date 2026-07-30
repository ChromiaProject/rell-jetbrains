package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.serviceIfCreated
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
 * Gradle-style reload bar on Chromia settings file editors (`chromia.yml` and qualifying
 * alternates): appears while the buffer declares a different `compile.rellVersion` than the
 * on-disk state the toolchain currently uses, and offers a one-click save — saving is what
 * triggers [ChromiaConfigChangeListener]'s reload chain (cache drop, re-highlight,
 * language-server re-routing). Saving any other way applies the change just the same; the bar
 * only makes the pending state visible.
 */
class ChromiaConfigReloadNotificationProvider : EditorNotificationProvider {

    override fun collectNotificationData(
        project: Project,
        file: VirtualFile,
    ): Function<in FileEditor, out JComponent?>? {
        if (!isTrackedSettingsFile(project, file)) return null
        val documentManager = FileDocumentManager.getInstance()
        val document = documentManager.getCachedDocument(file) ?: return null
        if (!documentManager.isDocumentUnsaved(document)) return null

        val edited = ChromiaConfigQuickFix.extractDeclaredVersion(document.text)
        val applied = RellVersionResolver.getInstance(project).declaredVersion(file)
        if (edited == applied) return null

        return Function { _ ->
            val panel = EditorNotificationPanel(EditorNotificationPanel.Status.Info)
            panel.text = "Rell version changed. The language server reloads when the file is saved"
            panel.createActionLabel("Save and reload") {
                ApplicationManager.getApplication().runWriteAction {
                    documentManager.saveDocument(document)
                }
                EditorNotifications.getInstance(project).updateNotifications(file)
            }
            panel
        }
    }
}

/**
 * Whether the reload bar tracks [file]: `chromia.yml` by name, other `*.yml` only when they
 * qualify as Chromia settings files (parsed and cached by the resolver).
 */
internal fun isTrackedSettingsFile(project: Project, file: VirtualFile): Boolean = when {
    ChromiaSettingsFiles.isDefaultName(file.name) -> true
    !ChromiaSettingsFiles.isYmlName(file.name) -> false
    else -> RellVersionResolver.getInstance(project).isSettingsCandidate(file)
}

/**
 * Refreshes the reload bar as the user types in a Chromia settings file, mirroring how the Gradle
 * "load changes" bar reacts to build-file edits. Alternate settings files are matched against the
 * resolver's cache only — the provider's own gate parses and caches them on the first
 * notification pass, so no parse runs per keystroke.
 */
class ChromiaConfigDocumentTracker : ProjectActivity {
    override suspend fun execute(project: Project) {
        EditorFactory.getInstance().eventMulticaster.addDocumentListener(
            object : DocumentListener {
                override fun documentChanged(event: DocumentEvent) {
                    val file = FileDocumentManager.getInstance().getFile(event.document) ?: return
                    if (!ChromiaSettingsFiles.isYmlName(file.name)) return

                    val tracked = ChromiaSettingsFiles.isDefaultName(file.name) ||
                            project.serviceIfCreated<RellVersionResolver>()?.isKnownCandidate(file.path) == true

                    if (tracked && !project.isDisposed) {
                        EditorNotifications.getInstance(project).updateNotifications(file)
                    }
                }
            },
            project,
        )
    }
}
