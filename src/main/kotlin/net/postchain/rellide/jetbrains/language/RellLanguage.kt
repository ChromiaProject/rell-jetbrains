package net.postchain.rellide.jetbrains.language

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.LanguageFileType


class RellLanguage : Language("Rell") {
    override fun isCaseSensitive() = true

    companion object {
        val INSTANCE = RellLanguage()
    }
}