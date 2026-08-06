package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationProvider
import net.postchain.rellide.jetbrains.language.RellFileType.Companion.RELL_EXTENSION
import net.postchain.rellide.jetbrains.toolwindow.execution.ChromiaCommandExecutor
import java.util.function.Function
import javax.swing.JComponent

/**
 * Suggests `chr install` on a `.rell` file whose current diagnostics include an unresolved module
 * that looks like a not-yet-installed library dependency rather than a typo — see
 * [ChromiaMissingLibDetector].
 */
class ChromiaMissingLibNotificationProvider : EditorNotificationProvider {
    override fun collectNotificationData(
        project: Project,
        file: VirtualFile,
    ): Function<in FileEditor, out JComponent?>? {
        if (file.extension != RELL_EXTENSION) return null
        val modulePath = ChromiaMissingLibDetector.anyMissingLibModule(project, file) ?: return null
        return Function { _ -> panel(project, file, modulePath) }
    }

    private fun panel(project: Project, file: VirtualFile, modulePath: String): JComponent {
        val panel = EditorNotificationPanel(EditorNotificationPanel.Status.Warning)
        panel.text = "Module '$modulePath' was not found — it may belong to a library declared in " +
                "chromia.yml that has not been installed yet."
        panel.createActionLabel("Run chr install") {
            val configDirectory = RellVersionResolver.getInstance(project).governingConfigDirectory(file)
            ChromiaCommandExecutor(project).executeCommand("chr install", configDirectory?.path)
        }
        return panel
    }
}
