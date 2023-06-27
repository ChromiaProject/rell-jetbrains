package net.postchain.rellide.jetbrains.language.psi

import com.intellij.psi.tree.IElementType
import net.postchain.rellide.jetbrains.language.RellLanguage
import org.jetbrains.annotations.NonNls

class RellTokenType(debugName: @NonNls String) :
    IElementType(debugName, RellLanguage.INSTANCE) {
    override fun toString(): String {
        return "RellTokenType." + super.toString()
    }
}