package net.postchain.rellide.jetbrains.lsp4ij

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerItem
import com.redhat.devtools.lsp4ij.LanguageServerManager
import com.redhat.devtools.lsp4ij.ServerStatus

const val RELL_LANGUAGE_SEVER_ID = "rellLanguageServer"

/**
 * Returns the running Rell [LanguageServerItem], or `null` if the server is not started.
 *
 * The [rellLanguageServerIsRunning] guard prevents IDE freezes: without it, [LanguageServerManager.getLanguageServer]
 * returns a future that blocks indefinitely when the server is not yet running. [getServerStatus] is
 * a non-blocking cached read that correctly reflects restarts and stops.
 *
 * Alternatives considered: caching the future (rejected — stale after server restart), Lease API
 * (rejected — overkill). See https://github.com/redhat-developer/lsp4ij/blob/main/docs/DeveloperGuide.md
 */
fun getRellLanguageServerItem(project: Project): LanguageServerItem? {
    if (!rellLanguageServerIsRunning(project)) return null
    return LanguageServerManager.getInstance(project)
            .getLanguageServer(RELL_LANGUAGE_SEVER_ID)
            .get()
}

fun rellLanguageServerIsRunning(project: Project): Boolean =
    getRellLanguageServerStatus(project) == ServerStatus.started

fun getRellLanguageServerStatus(project: Project): ServerStatus? {
    return LanguageServerManager.getInstance(project).getServerStatus(RELL_LANGUAGE_SEVER_ID)
}