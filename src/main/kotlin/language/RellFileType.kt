package net.postchain.rellide.jetbrains.language

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon


class RellFileType private constructor() : LanguageFileType(RellLanguage.INSTANCE) {
    override fun getName(): String = "Rell file"
    override fun getDescription(): String = "Rell language file"
    override fun getDefaultExtension(): String = RELL_EXTENSION
    override fun getIcon(): Icon = RellIcons.FILE

    @Suppress("CompanionObjectInExtension")
    companion object {
        const val RELL_EXTENSION = "rell"

        @JvmField
        val INSTANCE = RellFileType()
    }
}
