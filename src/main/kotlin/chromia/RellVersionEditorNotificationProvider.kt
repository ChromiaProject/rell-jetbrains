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
import com.intellij.ui.HyperlinkLabel
import net.postchain.rellide.jetbrains.language.RellFileType.Companion.RELL_EXTENSION
import java.util.function.Function
import javax.swing.JComponent

/**
 * Editor banners for the abnormal version resolutions (docs/COMPATIBILITY.md):
 *
 * - below the compatibility floor: hard cease — an error banner with a one-click quick-fix that
 *   bumps `compile.rellVersion` (fresh `chr create-rell-dapp` projects pin 0.14.5, so this is the
 *   first thing many users see), plus switch actions when sibling settings files stay in scope;
 * - declared newer than this plugin build knows: the newest supported toolchain runs instead, and
 *   a warning banner suggests updating the plugin;
 * - several settings files claim the file but disagree on the version: the active one governs,
 *   and a warning banner offers switching to each other in-scope settings file;
 * - a below-floor settings file is out-scored by an in-scope sibling: an info banner explains
 *   which file actually governs.
 */
class RellVersionEditorNotificationProvider : EditorNotificationProvider {
    override fun collectNotificationData(
        project: Project,
        file: VirtualFile,
    ): Function<in FileEditor, out JComponent?>? {
        val resolver = RellVersionResolver.getInstance(project)
        if (ChromiaSettingsFiles.isYmlName(file.name)) return settingsFileNotification(project, file, resolver)
        if (file.extension != RELL_EXTENSION) return null
        return when (val resolution = resolver.resolve(file)) {
            is RellVersionResolution.Unsupported ->
                Function { _ -> unsupportedPanel(project, resolution, resolver.claimants(file)) }

            is RellVersionResolution.Clamped ->
                Function { _ -> clampedPanel(resolution.configFile.name, resolution.declared, resolution.effective) }

            // No banner when the file still gets a working toolchain and the only news is which of
            // several settings files supplied it, or that a sibling is unusable. Every such
            // configuration builds fine under `chr -s`; the plugin merely had to choose one, and
            // the status-bar widget both shows the choice and changes it. A sibling declaring an
            // unsupported version is flagged on that file's own editor instead.
            is RellVersionResolution.Conflicting -> null

            is RellVersionResolution.Supported -> null
        }
    }

    /**
     * The same version verdict, shown on the settings file that declares it — a `rellVersion` the
     * plugin cannot serve is most likely to be noticed (and fixed) while editing the file it is
     * written in, and a variant that governs no open `.rell` file would otherwise say nothing at
     * all.
     */
    private fun settingsFileNotification(
        project: Project,
        file: VirtualFile,
        resolver: RellVersionResolver,
    ): Function<in FileEditor, out JComponent?>? {
        val claimant = resolver.evaluateConfig(file) ?: return null
        val declared = claimant.declared ?: return null
        val effective = claimant.effectiveVersion
        return when {
            effective == null -> Function { _ -> unsupportedConfigPanel(project, file, declared) }
            effective != declared -> Function { _ -> clampedPanel(file.name, declared, effective) }
            else -> null
        }
    }

    private fun unsupportedConfigPanel(project: Project, configFile: VirtualFile, declared: RellVersion): JComponent {
        val panel = EditorNotificationPanel(EditorNotificationPanel.Status.Error)
        panel.text = "${configFile.name} declares Rell $declared, which this plugin does not support " +
                "(minimum ${RellVersionRegistry.floor}). Files built with it get no language server."
        panel.createActionLabel("Set rellVersion to ${RellVersionRegistry.max}") {
            ChromiaConfigQuickFix.setDeclaredRellVersion(project, configFile, RellVersionRegistry.max)
            EditorNotifications.getInstance(project).updateAllNotifications()
        }
        panel.addCompatibilityDocLink()
        return panel
    }

    private fun unsupportedPanel(
        project: Project,
        resolution: RellVersionResolution.Unsupported,
        claimants: List<RellSettingsClaimant>,
    ): JComponent {
        val configName = resolution.configFile.name
        val panel = EditorNotificationPanel(EditorNotificationPanel.Status.Error)
        panel.text = "Rell ${resolution.declared} is not supported by this plugin " +
                "(minimum ${RellVersionRegistry.floor}). Completion, navigation, and diagnostics are disabled."
        panel.createActionLabel("Set rellVersion to ${RellVersionRegistry.max} in $configName") {
            ChromiaConfigQuickFix.setDeclaredRellVersion(project, resolution.configFile, RellVersionRegistry.max)
            EditorNotifications.getInstance(project).updateAllNotifications()
        }
        addSwitchAction(panel, project, resolution.configFile, claimants)
        panel.addCompatibilityDocLink()
        return panel
    }

    /** The same warning whether it is shown on the `.rell` file or on the settings file declaring the version. */
    private fun clampedPanel(configName: String, declared: RellVersion, effective: RellVersion): JComponent {
        val panel = EditorNotificationPanel(EditorNotificationPanel.Status.Warning)
        panel.text = "$configName declares Rell $declared, which this plugin version does not know: " +
                "using $effective. Update the plugin for exact support."
        panel.addCompatibilityDocLink()
        return panel
    }

    private fun EditorNotificationPanel.addCompatibilityDocLink() {
        createActionLabel("About Rell compatibility") { BrowserUtil.browse(COMPATIBILITY_DOC_URL) }
    }

    /**
     * A single link opening the settings-file chooser, rather than one link per file: a project
     * with four deployment variants would otherwise push the banner's own message off-screen. The
     * chooser is the same popup the status-bar widget shows.
     */
    private fun addSwitchAction(
        panel: EditorNotificationPanel,
        project: Project,
        governing: VirtualFile?,
        claimants: List<RellSettingsClaimant>,
    ) {
        val alternatives = claimants.filter { it.configFile != governing }
        if (alternatives.isEmpty()) return
        val directory = claimants.first().configFile.parent ?: return
        val choice = ChromiaSettingsChooser.Choice(directory, claimants, governing)

        if (alternatives.size == 1) {
            val only = alternatives.single()
            panel.createActionLabel("Use ${only.configFile.name}") {
                ChromiaActiveSettings.getInstance(project).setActive(directory.path, only.configFile.name)
            }
            return
        }

        var label: HyperlinkLabel? = null
        label = panel.createActionLabel("Switch settings file…") {
            ChromiaSettingsChooser.createPopup(project, choice).showUnderneathOf(label ?: panel)
        }
    }

    companion object {
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
