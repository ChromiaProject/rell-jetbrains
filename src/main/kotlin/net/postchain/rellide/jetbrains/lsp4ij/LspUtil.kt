package net.postchain.rellide.jetbrains.lsp4ij

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerItem
import com.redhat.devtools.lsp4ij.LanguageServerManager

fun getRellLanguageServerItem(project: Project): LanguageServerItem? {
    return LanguageServerManager.getInstance(project)
            .getLanguageServer("rellLanguageServer")
            .get()
}
