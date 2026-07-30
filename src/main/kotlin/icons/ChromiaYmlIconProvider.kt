package net.postchain.rellide.jetbrains.icons

import com.intellij.ide.IconProvider
import com.intellij.openapi.components.serviceIfCreated
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import net.postchain.rellide.jetbrains.chromia.ChromiaSettingsFiles
import net.postchain.rellide.jetbrains.chromia.RellVersionResolver
import net.postchain.rellide.jetbrains.language.RellIcons
import javax.swing.Icon

/**
 * Chromia icon on settings files: `chromia.yml` by name, alternate names when the resolver has
 * already qualified them as settings files. Cache-only peek — icon providers run on hot paths and
 * must never parse.
 */
class ChromiaYmlIconProvider : IconProvider(), DumbAware {
    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        val file = element.containingFile ?: return null
        val name = file.name
        if (ChromiaSettingsFiles.isDefaultName(name)) return RellIcons.CHROMIA_ICON_FILE
        if (!ChromiaSettingsFiles.isYmlName(name)) return null

        val virtualFile = file.originalFile.virtualFile ?: return null
        val resolver = element.project.serviceIfCreated<RellVersionResolver>() ?: return null
        return if (resolver.isKnownCandidate(virtualFile.path)) RellIcons.CHROMIA_ICON_FILE else null
    }
}
