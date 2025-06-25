package net.postchain.rellide.jetbrains.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.redhat.devtools.lsp4ij.LanguageServerManager
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import net.postchain.rellide.jetbrains.language.psi.RellXFunctionDef
import net.postchain.rellide.jetbrains.lsp4ij.RellServerApi
import net.postchain.rellide.jetbrains.lsp4ij.RellTestCase
import net.postchain.rellide.jetbrains.lsp4ij.RellTestFile

@Service(Service.Level.PROJECT)
class RellProjectService(val project: Project) {
    companion object {
        const val RELL_LANGUAGE_SERVER_ID = "rellLanguageServer"
        private const val CACHE_DURATION_MS = 1000L
    }

    private val testCasesCache = TimedCache<String, List<RellTestCase>>(CACHE_DURATION_MS)
    private val testFilesCache = TimedCache<String, List<RellTestFile>>(CACHE_DURATION_MS)

    fun listTestCases(fileUri: String): List<RellTestCase> {
        return testCasesCache.getOrPut(fileUri) {
            runBlocking {
                LanguageServerManager.getInstance(project).getLanguageServer(RELL_LANGUAGE_SERVER_ID).get()?.let { lsItem ->
                    val rellServer = lsItem.server as RellServerApi
                    rellServer.listTestCases(fileUri).await()
                }
            } ?: emptyList()
        }
    }

    fun getTestFiles(workspaceUri: String): List<RellTestFile> {
        return testFilesCache.getOrPut(workspaceUri) {
            runBlocking {
                LanguageServerManager.getInstance(project).getLanguageServer(RELL_LANGUAGE_SERVER_ID).get()?.let { lsItem ->
                    val rellServer = lsItem.server as RellServerApi
                    rellServer.getTestFiles(workspaceUri).await()
                }
            } ?: emptyList()
        }
    }

    fun getTestCase(element: PsiElement): RellTestCase? {
        if (element !is RellXFunctionDef) {
            return null
        }

        val virtualFile = element.containingFile?.virtualFile ?: return null
        val fileUri = virtualFile.url.replace("file:///", "file:/")
        val testCases = listTestCases(fileUri)

        return testCases.firstOrNull { it.name == element.xQualifiedName?.text }
    }

    fun isTest(element: PsiElement): Boolean {
        return getTestCase(element) != null
    }

    fun getTestFile(virtualFile: VirtualFile): RellTestFile? {
        val fileUri = virtualFile.url.replace("file:///", "file:/")
        val testFiles = getTestFiles(fileUri)
        return testFiles.firstOrNull { it.uri.toString() == fileUri }
    }
}
