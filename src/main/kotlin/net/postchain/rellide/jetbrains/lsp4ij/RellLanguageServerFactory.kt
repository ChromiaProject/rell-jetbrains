package net.postchain.rellide.jetbrains.lsp4ij

import com.intellij.openapi.project.Project;
import com.redhat.devtools.lsp4ij.LanguageServerFactory;
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider;

class RellLanguageServerFactory : LanguageServerFactory {
    companion object {
        const val USE_SOCKET_PROPERTY = "rell.lsp.useSocket"
    }

    override fun createConnectionProvider(project: Project): StreamConnectionProvider {
        val useSocket = System.getProperty(USE_SOCKET_PROPERTY, "false").toBoolean()

        return if (useSocket) {
            RellSocketLanguageServer(project)
        } else {
            RellLanguageServer(project)
        }
    }

    override fun createLanguageClient(project: Project): LanguageClientImpl {
        return RellLanguageClient(project)
    }
}
