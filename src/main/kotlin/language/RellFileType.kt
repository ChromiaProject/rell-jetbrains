package net.postchain.rellide.jetbrains.language

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon


class RellFileType private constructor() : LanguageFileType(RellLanguage.INSTANCE) {
    override fun getName(): String = "Rell file"
    override fun getDescription(): String = "Rell language file"
    override fun getDefaultExtension(): String = "rell"
    override fun getIcon(): Icon = RellIcons.FILE

    @Suppress("CompanionObjectInExtension")
    companion object {
        @JvmField
        val INSTANCE = RellFileType()
    }
}
