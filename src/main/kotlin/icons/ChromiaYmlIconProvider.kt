package net.postchain.rellide.jetbrains.icons

import com.intellij.ide.IconProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import net.postchain.rellide.jetbrains.language.RellIcons
import javax.swing.Icon

class ChromiaYmlIconProvider : IconProvider(), DumbAware {
    override fun getIcon(element: PsiElement, flags: Int): Icon? = if (element.containingFile?.name == "chromia.yml") {
        RellIcons.CHROMIA_ICON_FILE
    } else {
        null
    }
}
