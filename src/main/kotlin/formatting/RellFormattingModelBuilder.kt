package net.postchain.rellide.jetbrains.formatting

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings
import net.postchain.rellide.jetbrains.language.RellLanguage


class RellFormattingModelBuilder : FormattingModelBuilder {
    override fun createModel(context: FormattingContext): FormattingModel {
        val element = context.psiElement
        val settings = context.codeStyleSettings
        val spacingBuilder = createSpacingBuilder(settings)

        val containingFile = element.containingFile
        val block =
            RellFormattingBlock(element.node, null, Indent.getNoneIndent(), null, settings, spacingBuilder)

        return FormattingModelProvider.createFormattingModelForPsiFile(containingFile, block, settings)
    }

    override fun getRangeAffectingIndent(file: PsiFile, offset: Int, elementAtOffset: ASTNode): TextRange? {
        return null
    }

    companion object {
        fun createSpacingBuilder(settings: CodeStyleSettings): SpacingBuilder {
            return SpacingBuilder(settings, RellLanguage.INSTANCE)
        }
    }
}
