package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import com.redhat.devtools.lsp4ij.client.features.LSPClientFeatures
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider
import net.postchain.rellide.jetbrains.lsp4ij.RellLanguageClient
import net.postchain.rellide.jetbrains.lsp4ij.RellLanguageServer
import net.postchain.rellide.jetbrains.lsp4ij.RellLspClientFeatures
import net.postchain.rellide.jetbrains.lsp4ij.RellServerApi
import org.eclipse.lsp4j.services.LanguageServer

/**
 * Factory for the language server of one specific older supported Rell version, launched from the
 * downloaded runtime in `<system>/rell-lsp/<version>/` (see [RellLspRuntimeManager]). Identical to
 * [net.postchain.rellide.jetbrains.lsp4ij.RellLanguageServerFactory] except for the classpath.
 */
abstract class RellVersionedLanguageServerFactory(private val version: RellVersion) : LanguageServerFactory {

    override fun createConnectionProvider(project: Project): StreamConnectionProvider =
        RellLanguageServer(project, RellLspRuntimeManager.getInstance().cachedRuntimeDir(version))

    override fun createLanguageClient(project: Project): LanguageClientImpl = RellLanguageClient(project)

    override fun getServerInterface(): Class<out LanguageServer> = RellServerApi::class.java

    override fun createClientFeatures(): LSPClientFeatures = RellLspClientFeatures()
}

class Rell0160LanguageServerFactory : RellVersionedLanguageServerFactory(RellVersion(0, 16, 0))
