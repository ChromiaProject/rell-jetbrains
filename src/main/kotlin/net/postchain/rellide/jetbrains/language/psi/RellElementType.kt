package net.postchain.rellide.jetbrains.language.psi

import com.intellij.psi.tree.IElementType
import net.postchain.rellide.jetbrains.language.RellLanguage


class RellElementType(debugName: String) :
    IElementType(debugName, RellLanguage.INSTANCE)