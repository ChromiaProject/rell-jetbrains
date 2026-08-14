package net.postchain.rellide.jetbrains.lsp

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspClient
import com.intellij.platform.lsp.api.LspIntegrationProvider
import com.intellij.platform.lsp.api.lsWidget.LspClientWidgetItem
import net.postchain.rellide.jetbrains.language.RellFileType.Companion.RELL_EXTENSION
import net.postchain.rellide.jetbrains.language.RellIcons
import net.postchain.rellide.jetbrains.settings.RellPluginSettingsConfigurable

/**
 * Starts the bundled language server for every Rell file (docs/COMPATIBILITY.md). One server serves
 * the whole workspace whatever version its settings files declare: it reads `compile.rellVersion`
 * itself and compiles each project against that version.
 */
class RellLspIntegrationProvider : LspIntegrationProvider {
    override fun fileOpened(
        project: Project,
        file: VirtualFile,
        clientStarter: LspIntegrationProvider.LspClientStarter,
    ) {
        // The platform enumerates providers under the test runner too (cf. the Vue exclusion in
        // gradle.properties), and a client started there spawns a real server process and restarts
        // the daemon mid-highlighting. Routing stays directly testable via [route].
        if (ApplicationManager.getApplication().isUnitTestMode) return
        route(project, file, clientStarter)
    }

    internal fun route(project: Project, file: VirtualFile, clientStarter: LspIntegrationProvider.LspClientStarter) {
        if (file.extension != RELL_EXTENSION) return
        clientStarter.ensureClientStarted(RellLspClientDescriptor.bundled(project))
    }

    override fun createWidgetItem(lspClient: LspClient, currentFile: VirtualFile?): LspClientWidgetItem =
        LspClientWidgetItem(lspClient, currentFile, RellIcons.FILE, RellPluginSettingsConfigurable::class.java)
}
