package net.postchain.rellide.jetbrains.lsp

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.platform.lsp.api.LspClient
import com.intellij.platform.lsp.api.LspClientManager
import com.intellij.platform.lsp.api.LspServerState
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException
import java.util.concurrent.CompletableFuture

/** All Rell language-server clients of [project] that are up and running. */
fun runningRellLspClients(project: Project): List<LspClient> = LspClientManager.getInstance(project)
    .getClients(RellLspIntegrationProvider::class.java)
    .filter { it.state == LspServerState.Running }

/**
 * The running language-server client — the one that serves plugin-level requests (tests, templates,
 * cache invalidation) — or `null` if it is not running.
 */
fun getRellLspClient(project: Project): LspClient? = runningRellLspClients(project).firstOrNull()

fun rellLanguageServerIsRunning(project: Project): Boolean = getRellLspClient(project) != null

/**
 * Sends a custom Rell request (see [RellServerApi]) to this client and awaits the response.
 * A server-side error (e.g. the server cannot answer while `chromia.yml` holds a malformed
 * `compile.rellVersion`) yields null instead of an exception — these requests back UI features
 * like line markers, where a failed lookup must degrade to "no answer", not an IDE error.
 */
suspend fun <Response> LspClient.rellRequest(request: (RellServerApi) -> CompletableFuture<Response>): Response? =
    try {
        sendRequest { request(it as RellServerApi) }
    } catch (e: ResponseErrorException) {
        Logger.getInstance("#net.postchain.rellide.jetbrains.lsp.RellLspClients")
            .warn("Rell language server failed a custom request: ${e.responseError?.message}")
        null
    }
