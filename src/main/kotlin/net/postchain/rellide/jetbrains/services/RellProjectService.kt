package net.postchain.rellide.jetbrains.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.progress.runBlockingCancellable
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import kotlinx.coroutines.future.await
import net.postchain.rellide.jetbrains.lsp4ij.RellServerApi
import net.postchain.rellide.jetbrains.lsp4ij.RellTestFile
import net.postchain.rellide.jetbrains.lsp4ij.getRellLanguageServerItem
import net.postchain.rellide.jetbrains.util.normalizedUri

@Service(Service.Level.PROJECT)
class RellProjectService(val project: Project) {
    companion object {
        const val RELL_LANGUAGE_SERVER_ID = "rellLanguageServer"
        private const val CACHE_DURATION_MS = 1000L
    }

    private val testFilesCache = TimedCache<String, RellTestFile>(CACHE_DURATION_MS)

    fun getTestFile(fileUri: String): RellTestFile? {
        testFilesCache.get(fileUri)?.let { return it }
        val result = runBlockingCancellable {
            getRellLanguageServerItem(project)?.let { lsItem ->
                val rellServer = lsItem.server as RellServerApi
                rellServer.getTestFile(fileUri).await()
            }
        }
        if (result != null) testFilesCache.put(fileUri, result)
        return result
    }

    fun getTestFile(virtualFile: VirtualFile): RellTestFile? {
        val fileUri = virtualFile.normalizedUri()
        return getTestFile(fileUri)
    }


    fun isTest(element: PsiElement): Boolean {
        val virtualFile = element.containingFile?.virtualFile ?: return false
        return getTestFile(virtualFile) != null
    }
}
