package net.postchain.rellide.jetbrains.language

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon


class RellFileType private constructor() : LanguageFileType(RellLanguage.INSTANCE) {
    override fun getName(): String {
        return "Rell file"
    }

    override fun getDescription(): String {
        return "Rell language file"
    }

    override fun getDefaultExtension(): String {
        return "rell"
    }

    override fun getIcon(): Icon? {
        return RellIcons.FILE
    }

    companion object {
        val INSTANCE = RellFileType()
    }
}
