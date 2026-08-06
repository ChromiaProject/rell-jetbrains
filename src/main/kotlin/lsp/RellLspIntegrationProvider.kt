package net.postchain.rellide.jetbrains.lsp

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspClient
import com.intellij.platform.lsp.api.LspIntegrationProvider
import com.intellij.platform.lsp.api.lsWidget.LspClientWidgetItem
import net.postchain.rellide.jetbrains.chromia.*
import net.postchain.rellide.jetbrains.language.RellFileType.Companion.RELL_EXTENSION
import net.postchain.rellide.jetbrains.language.RellIcons
import net.postchain.rellide.jetbrains.settings.RellPluginSettingsConfigurable

/**
 * Starts the language server of the Rell version each file resolves to (docs/COMPATIBILITY.md):
 * the newest supported version runs from the bundled runtime; an older supported version runs from
 * its downloaded runtime — until that download completes, the file has no server at all. Files
 * below the compatibility floor never get one — that is the hard cease.
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
        val version = routedRellVersion(project, file) ?: return

        if (version == RellVersionRegistry.max) {
            clientStarter.ensureClientStarted(RellLspClientDescriptor.newest(project))
            return
        }

        val runtimes = RellLspRuntimeManager.getInstance()
        if (runtimes.isRuntimeReady(version)) {
            clientStarter.ensureClientStarted(RellLspClientDescriptor.versioned(project, version))
        } else {
            runtimes.ensureRuntimeAsync(version, project)
        }
    }

    override fun createWidgetItem(lspClient: LspClient, currentFile: VirtualFile?): LspClientWidgetItem =
        LspClientWidgetItem(lspClient, currentFile, RellIcons.FILE, RellPluginSettingsConfigurable::class.java)
}

/**
 * The supported Rell version whose toolchain serves [file] (declared, clamped, or defaulted), or
 * `null` below the compatibility floor.
 */
internal fun routedRellVersion(project: Project, file: VirtualFile): RellVersion? {
    val resolution = RellVersionResolver.getInstance(project).resolve(file)
    return if (resolution is RellVersionResolution.Unsupported) null else resolution.effectiveVersion
}
