package net.postchain.rellide.jetbrains.language.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import net.postchain.rellide.jetbrains.language.RellFileType
import net.postchain.rellide.jetbrains.language.RellLanguage


class RellFile(viewProvider: FileViewProvider) :
    PsiFileBase(viewProvider, RellLanguage.INSTANCE) {
    override fun getFileType(): FileType {
        return RellFileType.INSTANCE
    }

    override fun toString(): String {
        return "Rell File"
    }
}
