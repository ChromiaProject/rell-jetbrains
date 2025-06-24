package net.postchain.rellide.jetbrains.testing

import com.intellij.ide.fileTemplates.FileTemplateDescriptor
import com.intellij.lang.Language
import com.intellij.openapi.module.Module
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.testIntegration.TestFramework
import net.postchain.rellide.jetbrains.language.RellFileType
import net.postchain.rellide.jetbrains.language.RellIcons
import javax.swing.Icon

class RellTestFramework : TestFramework {
    companion object {
        val INSTANCE: RellTestFramework = RellTestFramework()
    }

    override fun getName(): String = "Rell Test"

    override fun getIcon(): Icon = RellIcons.FILE

    override fun isLibraryAttached(module: Module): Boolean = true

    override fun getLibraryPath(): String? = null

    override fun isTestClass(clazz: PsiElement): Boolean = false

    override fun isTestMethod(element: PsiElement): Boolean = false

    override fun getDefaultSuperClass(): String? = null

    override fun isPotentialTestClass(element: PsiElement): Boolean = false

    override fun findSetUpMethod(element: PsiElement): PsiElement? = null

    override fun findTearDownMethod(element: PsiElement): PsiElement? = null

    override fun findOrCreateSetUpMethod(element: PsiElement): PsiElement? = null

    override fun getSetUpMethodFileTemplateDescriptor(): FileTemplateDescriptor? = null

    override fun getTearDownMethodFileTemplateDescriptor(): FileTemplateDescriptor? = null

    override fun getTestMethodFileTemplateDescriptor(): FileTemplateDescriptor = FileTemplateDescriptor("Rell Test Method")

    override fun isIgnoredMethod(element: PsiElement): Boolean = false

    override fun getLanguage() = RellFileType.INSTANCE.language
}