package net.postchain.rellide.jetbrains.lsp

import com.intellij.openapi.project.Project
import com.intellij.platform.lsp.api.LspClient
import com.intellij.platform.lsp.api.LspClientManager
import com.intellij.platform.lsp.api.LspServerState
import net.postchain.rellide.jetbrains.chromia.RellVersionRegistry
import java.util.concurrent.CompletableFuture

/** All Rell language-server clients of [project] that are up and running, any Rell version. */
fun runningRellLspClients(project: Project): List<LspClient> = LspClientManager.getInstance(project)
    .getClients(RellLspIntegrationProvider::class.java)
    .filter { it.state == LspServerState.Running }

/**
 * The running client of the newest supported Rell version — the bundled toolchain that serves
 * plugin-level requests (tests, templates, cache invalidation) — or `null` if it is not running.
 */
fun getRellLspClient(project: Project): LspClient? = runningRellLspClients(project).find {
    (it.descriptor as? RellLspClientDescriptor)?.version == RellVersionRegistry.max
}

fun rellLanguageServerIsRunning(project: Project): Boolean = getRellLspClient(project) != null

/** Sends a custom Rell request (see [RellServerApi]) to this client and awaits the response. */
suspend fun <Response> LspClient.rellRequest(request: (RellServerApi) -> CompletableFuture<Response>): Response? =
    sendRequest { request(it as RellServerApi) }
