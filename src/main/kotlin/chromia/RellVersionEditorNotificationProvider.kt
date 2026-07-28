package net.postchain.rellide.jetbrains.chromia

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationProvider
import com.intellij.ui.EditorNotifications
import java.util.function.Function
import javax.swing.JComponent

/**
 * Editor banners for the two abnormal version resolutions (docs/COMPATIBILITY.md):
 *
 * - below the compatibility floor: hard cease — an error banner with a one-click quick-fix that
 *   bumps `compile.rellVersion` (fresh `chr create-rell-dapp` projects pin 0.14.5, so this is the
 *   first thing many users see);
 * - declared newer than this plugin build knows: the newest supported toolchain runs instead, and
 *   a warning banner suggests updating the plugin.
 */
class RellVersionEditorNotificationProvider : EditorNotificationProvider {
    override fun collectNotificationData(
        project: Project,
        file: VirtualFile,
    ): Function<in FileEditor, out JComponent?>? {
        if (file.extension != RELL_EXTENSION) return null
        return when (val resolution = RellVersionResolver.getInstance(project).resolve(file)) {
            is RellVersionResolution.Unsupported -> Function { _ -> unsupportedPanel(project, resolution) }
            is RellVersionResolution.Clamped -> Function { _ -> clampedPanel(resolution) }
            is RellVersionResolution.Supported -> null
        }
    }

    private fun unsupportedPanel(project: Project, resolution: RellVersionResolution.Unsupported): JComponent {
        val panel = EditorNotificationPanel(EditorNotificationPanel.Status.Error)
        panel.text = "Rell ${resolution.declared} is not supported by this plugin " +
                "(minimum ${RellVersionRegistry.floor}). Completion, navigation, and diagnostics are disabled."
        panel.createActionLabel("Set rellVersion to ${RellVersionRegistry.max} in chromia.yml") {
            ChromiaConfigQuickFix.setDeclaredRellVersion(project, resolution.configFile, RellVersionRegistry.max)
            EditorNotifications.getInstance(project).updateAllNotifications()
        }
        panel.createActionLabel("About Rell compatibility") { BrowserUtil.browse(COMPATIBILITY_DOC_URL) }
        return panel
    }

    private fun clampedPanel(resolution: RellVersionResolution.Clamped): JComponent {
        val panel = EditorNotificationPanel(EditorNotificationPanel.Status.Warning)
        panel.text = "chromia.yml declares Rell ${resolution.declared}, which this plugin version does not " +
                "know: using ${resolution.effective}. Update the plugin for exact support."
        panel.createActionLabel("About Rell compatibility") { BrowserUtil.browse(COMPATIBILITY_DOC_URL) }
        return panel
    }

    companion object {
        private const val RELL_EXTENSION = "rell"
        private const val COMPATIBILITY_DOC_URL =
            "https://bitbucket.org/chromawallet/rell-jetbrains/src/main/docs/COMPATIBILITY.md"
    }
}

object ChromiaConfigQuickFix {
    private val LOG = logger<ChromiaConfigQuickFix>()

    // Line-anchored so commented-out declarations (# rellVersion: …) never match, and horizontal
    // whitespace only so the match cannot cross lines.
    private val RELL_VERSION_VALUE =
        Regex("""(?m)^([ \t]*rellVersion[ \t]*:[ \t]*)("[^"\r\n]*"|'[^'\r\n]*'|[^ \t#\r\n]+)""")

    /**
     * The `rellVersion` value declared in [configText], unquoted; null when absent. Textual
     * extraction only — used to compare an unsaved editor buffer against the applied on-disk
     * state, not for resolution.
     */
    fun extractDeclaredVersion(configText: String): String? =
        RELL_VERSION_VALUE.find(configText)?.groupValues?.get(2)
            ?.trim('"', '\'')
            ?.takeIf { it.isNotBlank() }

    /**
     * Rewrites the `rellVersion` value in [configFile] and saves it, so the resolver (which reads
     * the on-disk state, like the toolchain) picks the change up immediately.
     */
    fun setDeclaredRellVersion(project: Project, configFile: VirtualFile, newVersion: RellVersion) {
        WriteCommandAction.runWriteCommandAction(project, "Set Rell Version", null, {
            val document = FileDocumentManager.getInstance().getDocument(configFile)
            if (document == null) {
                LOG.warn("No document for ${configFile.path}")
                return@runWriteCommandAction
            }
            val match = RELL_VERSION_VALUE.find(document.text)
            if (match == null) {
                LOG.warn("No rellVersion key found in ${configFile.path}")
                return@runWriteCommandAction
            }
            val replacement = "${match.groupValues[1]}\"$newVersion\""
            document.replaceString(match.range.first, match.range.last + 1, replacement)
            FileDocumentManager.getInstance().saveDocument(document)
        })
    }
}
