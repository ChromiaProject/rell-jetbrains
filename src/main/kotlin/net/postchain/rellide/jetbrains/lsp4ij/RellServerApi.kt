package net.postchain.rellide.jetbrains.lsp4ij

import org.eclipse.lsp4j.jsonrpc.services.JsonRequest
import org.eclipse.lsp4j.services.LanguageServer
import java.util.concurrent.CompletableFuture

interface RellServerApi : LanguageServer {

    @JsonRequest("rell/invalidateCaches")
    fun invalidateCache(): CompletableFuture<Boolean>

}