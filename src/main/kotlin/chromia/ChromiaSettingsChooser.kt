package net.postchain.rellide.jetbrains.chromia

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.ui.popup.ListSeparator
import com.intellij.openapi.ui.popup.PopupStep
import com.intellij.openapi.ui.popup.util.BaseListPopupStep
import com.intellij.openapi.vfs.VirtualFile
import net.postchain.rellide.jetbrains.language.RellIcons
import net.postchain.rellide.jetbrains.settings.openRellSettings
import javax.swing.Icon

/**
 * The settings files that govern one file, and the popup for switching between them — shared by
 * the status-bar widget and the version banners so both offer the same list and write the same
 * choice.
 */
object ChromiaSettingsChooser {
    class Choice(
        val directory: VirtualFile,
        val claimants: List<RellSettingsClaimant>,
        val governing: VirtualFile?,
        /** Whether the claiming settings files disagree on the version — no banner says so. */
        val conflicting: Boolean = false,
    ) {
        val governingName: String?
            get() = governing?.name
    }

    fun choiceFor(project: Project, file: VirtualFile?): Choice? {
        if (file == null || !file.isValid) return null
        val resolver = RellVersionResolver.getInstance(project)
        val claimants = resolver.claimants(file)
        if (claimants.isEmpty()) return null
        // Every claimant of a file comes from the same directory (see RellVersionResolver), which
        // is the directory the active choice is keyed by.
        val directory = claimants.first().configFile.parent ?: return null
        val resolution = resolver.resolve(file)
        return Choice(
            directory,
            claimants,
            resolution.configFile,
            conflicting = resolution is RellVersionResolution.Conflicting,
        )
    }

    /**
     * A popup listing [choice]'s settings files with the Rell version each one would apply, marking
     * the governing one. Choosing an entry makes it active for its directory, which re-resolves
     * every file it governs.
     */
    /** One row of the popup: a settings file to switch to, or the trailing link to plugin settings. */
    private sealed interface Entry {
        data class File(val claimant: RellSettingsClaimant) : Entry
        data object OpenSettings : Entry
    }

    fun createPopup(project: Project, choice: Choice, title: String = "Chromia Settings File"): ListPopup {
        val entries = choice.claimants.map { Entry.File(it) } + Entry.OpenSettings
        val step = object : BaseListPopupStep<Entry>(title, entries) {
            // The active file needs no marker: it is the initially selected row, and the status
            // bar names it anyway.
            override fun getTextFor(value: Entry): String = when (value) {
                is Entry.File -> itemText(value.claimant)
                Entry.OpenSettings -> "Rell Settings…"
            }

            override fun getIconFor(value: Entry): Icon? = when (value) {
                is Entry.File -> RellIcons.CHROMIA_ICON_FILE
                Entry.OpenSettings -> AllIcons.General.Settings
            }

            /** Sets the settings link apart from the files, as IDE widget popups do. */
            override fun getSeparatorAbove(value: Entry): ListSeparator? =
                if (value == Entry.OpenSettings) ListSeparator() else null

            override fun getDefaultOptionIndex(): Int =
                choice.claimants.indexOfFirst { it.configFile == choice.governing }.coerceAtLeast(0)

            override fun onChosen(selectedValue: Entry, finalChoice: Boolean): PopupStep<*>? {
                when (selectedValue) {
                    // Opening a modal dialog from inside onChosen would fight the closing popup.
                    Entry.OpenSettings -> return doFinalStep { openRellSettings(project) }

                    is Entry.File -> if (selectedValue.claimant.configFile != choice.governing) {
                        ChromiaActiveSettings.getInstance(project).setActive(
                            choice.directory.path,
                            selectedValue.claimant.configFile.name,
                        )
                    }
                }
                return FINAL_CHOICE
            }
        }
        return JBPopupFactory.getInstance().createListPopup(step)
    }

    /** How one settings file is listed: `atbash.yml (Rell 0.16.2)`. */
    fun itemText(claimant: RellSettingsClaimant): String =
        "${claimant.configFile.name} (${describeVersion(claimant)})"

    /** The Rell version a settings file declares, or the bundled one it falls back to. */
    fun describeVersion(claimant: RellSettingsClaimant): String =
        if (claimant.declared == null) {
            "no rellVersion, using ${BundledRellVersion.version}"
        } else {
            "Rell ${claimant.declared}"
        }
}
