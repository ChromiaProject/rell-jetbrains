package net.postchain.rellide.jetbrains.actions

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidatorEx
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiDirectory
import net.postchain.rellide.jetbrains.language.RellIcons

private const val CAPTION = "Rell File"

class RellCreateFileAction : CreateFileFromTemplateAction(CAPTION, "", RellIcons.FILE), DumbAware {

    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?) = CAPTION

    override fun buildDialog(
        project: Project,
        directory: PsiDirectory,
        builder: CreateFileFromTemplateDialog.Builder,
    ) {
        builder.setTitle(CAPTION)
            .addKind("Rell File", RellIcons.FILE, "Rell File")
            .addKind("Rell Entity", RellIcons.FILE, "Rell Entity")
            .addKind("Rell Struct", RellIcons.FILE, "Rell Struct")
            .addKind("Rell Object", RellIcons.FILE, "Rell Object")
            .addKind("Rell Enum", RellIcons.FILE, "Rell Enum")
            .setValidator(object : InputValidatorEx {
                override fun checkInput(inputString: String): Boolean {
                    return getErrorText(inputString) == null
                }

                override fun canClose(inputString: String): Boolean {
                    return getErrorText(inputString) == null
                }

                override fun getErrorText(inputString: String): String? {
                    return if (!StringUtil.isEmpty(inputString) && FileUtil.sanitizeFileName(
                            inputString,
                            false
                        ) == inputString
                    )
                        null
                    else "'$inputString' is not a valid Rell file name"
                }
            })
    }
}
