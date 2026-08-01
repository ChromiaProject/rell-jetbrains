package net.postchain.rellide.jetbrains.testing

import com.intellij.execution.Location
import com.intellij.execution.PsiLocation
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope

/**
 * Test locator for Rell tests.
 * Helps navigate from test results to source code locations.
 */
class RellTestLocator : SMTestLocator {

    companion object {
        const val PROTOCOL = "rell_test"
    }

    override fun getLocation(
        protocol: String,
        path: String,
        project: Project,
        globalSearchScope: GlobalSearchScope,
    ): List<Location<*>> {
        if (protocol != PROTOCOL) {
            return emptyList()
        }

        val locations = mutableListOf<Location<*>>()

        // Parse the path which should be in format: file_path:line_number:function_name
        val parts = path.split(":")
        if (parts.isNotEmpty()) {
            val filePath = parts[0]
            val psiManager = PsiManager.getInstance(project)

            // Try to find file by absolute path first
            val virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath)
            if (virtualFile != null) {
                val psiFile = psiManager.findFile(virtualFile)
                if (psiFile != null) {
                    locations.add(PsiLocation(psiFile))
                }
            } else {
                // If not found by absolute path, try to find by filename
                val fileName = filePath.substringAfterLast("/")
                val virtualFiles = FilenameIndex.getVirtualFilesByName(fileName, globalSearchScope)
                for (virtualFile in virtualFiles) {
                    val psiFile = psiManager.findFile(virtualFile)
                    locations.add(PsiLocation(psiFile))
                }
            }
        }

        return locations
    }
}
