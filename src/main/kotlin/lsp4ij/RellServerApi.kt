package net.postchain.rellide.jetbrains.lsp4ij

import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest
import org.eclipse.lsp4j.services.LanguageServer
import java.net.URI
import java.util.concurrent.CompletableFuture

interface RellServerApi : LanguageServer {
    @JsonRequest("rell/invalidateCaches")
    fun invalidateCache(): CompletableFuture<Boolean>

    @JsonRequest("rell/listTestFiles")
    fun getTestFiles(workspaceUri: String): CompletableFuture<List<RellTestFile>>

    @JsonRequest("rell/getTestFile")
    fun getTestFile(workspaceUri: String): CompletableFuture<RellTestFile?>

    @JsonRequest("rell/listTestCases")
    fun listTestCases(testFileUri: String): CompletableFuture<List<RellTestCase>>

    @JsonRequest("rell/addToProject")
    fun addToProject(params: AddToProjectParams): CompletableFuture<Void>
}


data class RellTestCase(val name: String, val range: Range, val uri: String)

data class RellTestFile(
    val uri: URI,
    val moduleName: String? = null,
    val canResolveChildren: Boolean = true,
    val testCases: List<RellTestCase> = listOf(),
)

data class TemplateOptions(
    val includeDevContainer: Boolean = false,
)

data class AddToProjectParams(
    val targetDirUri: String,
    val options: TemplateOptions,
)
