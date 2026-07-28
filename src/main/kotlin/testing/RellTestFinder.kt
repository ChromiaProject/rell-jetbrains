package net.postchain.rellide.jetbrains.testing

import com.intellij.openapi.components.service
import com.intellij.psi.PsiElement
import com.intellij.testIntegration.TestFinder
import net.postchain.rellide.jetbrains.services.RellProjectService

class RellTestFinder : TestFinder {
    override fun findSourceElement(from: PsiElement): PsiElement? {
        return from.containingFile
    }

    override fun findTestsForClass(element: PsiElement): Collection<PsiElement?> {
        return emptyList()
    }

    override fun findClassesForTest(element: PsiElement): Collection<PsiElement?> {
        return emptyList()
    }

    override fun isTest(element: PsiElement): Boolean {
        val projectService = element.project.service<RellProjectService>()
        return projectService.isTest(element)
    }
}