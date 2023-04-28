package net.postchain.rellide.jetbrains.language

import com.intellij.lang.Language


class RellLanguage : Language("Rell") {
    companion object {
        val INSTANCE = RellLanguage()
    }
}