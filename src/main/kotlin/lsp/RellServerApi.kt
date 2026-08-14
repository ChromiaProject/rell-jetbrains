package net.postchain.rellide.jetbrains.lsp

import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest
import org.eclipse.lsp4j.services.LanguageServer
import java.net.URI
import java.util.concurrent.CompletableFuture

interface RellServerApi : LanguageServer {
    @JsonRequest("rell/invalidateCaches")
    fun invalidateCache(): CompletableFuture<Boolean>

    @JsonRequest("rell/getTestFile")
    fun getTestFile(workspaceUri: String): CompletableFuture<RellTestFile?>

    @JsonRequest("rell/addToProject")
    fun addToProject(params: AddToProjectParams): CompletableFuture<Void>

    /**
     * Tells the server which Chromia settings files the user has chosen, so switching a directory
     * between its `chromia.yml` and a sibling re-analyses the sources against the newly active
     * file's `compile.rellVersion` and `compile.source`. Answers `true` if it re-indexed.
     */
    @JsonRequest("rell/setSettingsFiles")
    fun setSettingsFiles(params: SetSettingsFilesParams): CompletableFuture<Boolean>
}


data class RellTestCase(val name: String, val range: Range, val uri: String)

data class RellTestFile(
    val uri: URI,
    val moduleName: String? = null,
    val testCases: List<RellTestCase> = listOf(),
)

data class TemplateOptions(
    val includeDevContainer: Boolean = false,
)

data class AddToProjectParams(
    val targetDirUri: String,
    val options: TemplateOptions,
)

/**
 * Only the settings files that are *not* the `chromia.yml` of their own directory need listing — the
 * server discovers those by name. An empty list means "use name-based discovery everywhere", which
 * is what switching a directory back to its `chromia.yml` amounts to.
 */
data class SetSettingsFilesParams(
    val configFileUris: List<String>,
)
