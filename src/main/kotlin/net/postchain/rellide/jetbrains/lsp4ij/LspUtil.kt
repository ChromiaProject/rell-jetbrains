package net.postchain.rellide.jetbrains.lsp4ij

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerItem
import com.redhat.devtools.lsp4ij.LanguageServerManager
import com.redhat.devtools.lsp4ij.ServerStatus

const val RELL_LANGUAGE_SEVER_ID = "rellLanguageServer"

fun getRellLanguageServerItem(project: Project): LanguageServerItem? {
    return LanguageServerManager.getInstance(project)
            .getLanguageServer(RELL_LANGUAGE_SEVER_ID)
            .get()
}

fun rellLanguageServerIsRunning(project: Project): Boolean =
    getRellLanguageServerStatus(project) == ServerStatus.started

fun getRellLanguageServerStatus(project: Project): ServerStatus? {
    return LanguageServerManager.getInstance(project).getServerStatus(RELL_LANGUAGE_SEVER_ID)
}