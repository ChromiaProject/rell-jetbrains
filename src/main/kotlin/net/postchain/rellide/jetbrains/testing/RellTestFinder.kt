package net.postchain.rellide.jetbrains.testing

import com.intellij.openapi.components.service
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.testIntegration.TestFinder
import groovyjarjarantlr4.v4.parse.ATNBuilder
import net.postchain.rellide.jetbrains.services.RellProjectService

class RellTestFinder : TestFinder {
//    override fun findTestsInFile(psiFile: PsiFile): Collection<PsiElement> {
//        val tests = mutableListOf<PsiElement>()
//
//        // Traverse the file to find test methods/classes
//        psiFile.accept(object : PsiRecursiveElementVisitor() {
//            override fun visitElement(element: PsiElement) {
//                if (isTestMethod(element) || isTestClass(element)) {
//                    tests.add(element)
//                }
//                super.visitElement(element)
//            }
//        })
//
//        return tests
//    }
//
//    private fun isTestMethod(element: PsiElement): Boolean {
//        // Implement your test detection logic
//        // For example, check for specific annotations, naming patterns, etc.
//        return element is PsiMethod &&
//                element.hasAnnotation("com.yourframework.Test")
//    }
//
//    private fun isTestClass(element: PsiElement): Boolean {
//        // Similar logic for test classes
//        return element is PsiClass &&
//                element.methods.any { isTestMethod(it) }
//    }

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