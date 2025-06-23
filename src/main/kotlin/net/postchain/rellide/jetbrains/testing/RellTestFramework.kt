package net.postchain.rellide.jetbrains.testing

import com.intellij.ide.fileTemplates.FileTemplateDescriptor
import com.intellij.lang.Language
import com.intellij.openapi.module.Module
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import com.intellij.testIntegration.TestFramework
import javax.swing.Icon

class RellTestFramework : TestFramework {
    override fun getName(): @NlsSafe String {
        TODO("Not yet implemented")
    }

    override fun getIcon(): Icon {
        TODO("Not yet implemented")
    }

    override fun isLibraryAttached(module: Module): Boolean {
        TODO("Not yet implemented")
    }

    override fun getLibraryPath(): String? {
        TODO("Not yet implemented")
    }

    override fun getDefaultSuperClass(): String? {
        TODO("Not yet implemented")
    }

    override fun isTestClass(clazz: PsiElement): Boolean {
        TODO("Not yet implemented")
    }

    override fun isPotentialTestClass(clazz: PsiElement): Boolean {
        TODO("Not yet implemented")
    }

    override fun findSetUpMethod(clazz: PsiElement): PsiElement? {
        TODO("Not yet implemented")
    }

    override fun findTearDownMethod(clazz: PsiElement): PsiElement? {
        TODO("Not yet implemented")
    }

    override fun findOrCreateSetUpMethod(clazz: PsiElement): PsiElement? {
        TODO("Not yet implemented")
    }

    override fun getSetUpMethodFileTemplateDescriptor(): FileTemplateDescriptor? {
        TODO("Not yet implemented")
    }

    override fun getTearDownMethodFileTemplateDescriptor(): FileTemplateDescriptor? {
        TODO("Not yet implemented")
    }

    override fun getTestMethodFileTemplateDescriptor(): FileTemplateDescriptor {
        TODO("Not yet implemented")
    }

    override fun isIgnoredMethod(element: PsiElement?): Boolean {
        TODO("Not yet implemented")
    }

    override fun isTestMethod(element: PsiElement?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getLanguage(): Language {
        TODO("Not yet implemented")
    }
}