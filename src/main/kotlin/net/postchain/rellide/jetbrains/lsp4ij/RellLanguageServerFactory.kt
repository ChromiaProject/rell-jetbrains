package net.postchain.rellide.jetbrains.lsp4ij

import com.intellij.openapi.project.Project;
import com.redhat.devtools.lsp4ij.LanguageServerFactory;
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider;
import org.eclipse.lsp4j.services.LanguageServer;

class RellLanguageServerFactory : LanguageServerFactory {
    override fun createConnectionProvider(project: Project): StreamConnectionProvider {
        return RellLanguageServer(project)
    }

    override fun createLanguageClient(project: Project): LanguageClientImpl {
        return RellLanguageClient(project)
    }

    override fun getServerInterface(): Class<out LanguageServer> {
        return RellServerApi::class.java
    }
}
