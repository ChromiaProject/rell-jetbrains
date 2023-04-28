package net.postchain.rellide.jetbrains.language.psi

import com.intellij.psi.tree.IElementType
import net.postchain.rellide.jetbrains.language.RellLanguage
import org.jetbrains.annotations.NonNls


class RellElementType(debugName: @NonNls String) :
    IElementType(debugName, RellLanguage.INSTANCE)