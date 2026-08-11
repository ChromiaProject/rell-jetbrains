package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationProvider
import net.postchain.rellide.jetbrains.language.RellFileType.Companion.RELL_EXTENSION
import java.util.function.Function
import javax.swing.JComponent

/**
 * Suggests `chr install` on a `.rell` file whose current diagnostics include an unresolved module
 * that looks like a not-yet-installed library dependency rather than a typo
 *
 * @see [ChromiaMissingLibDetector]
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
        val settingsFileName = RellVersionResolver.getInstance(project).governingConfigFile(file)?.name
            ?: ChromiaSettingsFiles.CHROMIA_YML
        panel.text = "Module '$modulePath' was not found. It may belong to a library declared in " +
                "$settingsFileName that has not been installed yet."
        panel.createActionLabel("Run chr install") { ChromiaInstallCommand.run(project, file) }
        return panel
    }
}
