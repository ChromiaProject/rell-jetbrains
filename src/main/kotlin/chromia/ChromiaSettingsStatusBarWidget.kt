package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.openapi.wm.impl.status.EditorBasedStatusBarPopup

/**
 * Status-bar widget naming the Chromia settings file that governs the current file, and switching
 * it in one click — the same role the encoding and line-separator widgets play for their file
 * properties. This is the fast path for projects with several settings files (one per deployment
 * network): the banners explain a problem, the widget just switches configuration.
 *
 * Hidden for files no settings file governs, so ordinary projects never see it.
 */
class ChromiaSettingsStatusBarWidget(project: Project) :
    EditorBasedStatusBarPopup(project, false), DumbAware {

    override fun ID(): String = WIDGET_ID

    override fun createInstance(project: Project): StatusBarWidget = ChromiaSettingsStatusBarWidget(project)

    override fun getWidgetState(file: VirtualFile?): WidgetState {
        val choice = ChromiaSettingsChooser.choiceFor(project, file) ?: return WidgetState.HIDDEN
        val governingName = choice.governingName ?: return WidgetState.HIDDEN
        val governing = choice.claimants.find { it.configFile == choice.governing }

        val described = governing?.let { ChromiaSettingsChooser.itemText(it) } ?: governingName
        val tooltip = buildString {
            append("Chromia settings file: ").append(described)
            if (choice.claimants.size > 1) {
                // The only place a version disagreement is reported: it needs no banner, because
                // the file still gets a working toolchain either way.
                append(if (choice.conflicting) ". Other settings files declare a different Rell version" else "")
                append(". Click to switch (").append(choice.claimants.size).append(" available)")
            }
        }
        return WidgetState(tooltip, governingName, true)
    }

    override fun createPopup(context: DataContext): ListPopup? {
        val choice = ChromiaSettingsChooser.choiceFor(project, getSelectedFile()) ?: return null
        return ChromiaSettingsChooser.createPopup(project, choice)
    }

    override fun install(statusBar: StatusBar) {
        super.install(statusBar)
        // Switching the active file, editing a rellVersion, or adding a settings file all change
        // what this widget shows.
        project.messageBus.connect(this).subscribe(RellVersionResolver.TOPIC, ChromiaConfigListener { update() })
    }

    companion object {
        const val WIDGET_ID: String = "RellChromiaSettingsFile"
    }
}

class ChromiaSettingsStatusBarWidgetFactory : StatusBarWidgetFactory {
    override fun getId(): String = ChromiaSettingsStatusBarWidget.WIDGET_ID

    override fun getDisplayName(): String = "Chromia Settings File"

    override fun createWidget(project: Project): StatusBarWidget = ChromiaSettingsStatusBarWidget(project)
}
