package net.postchain.rellide.jetbrains.lsp4ij

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import net.postchain.rellide.jetbrains.language.RellFileType

class RellEnterHandler : EnterHandlerDelegateAdapter() {
    private val indentAfterEnterChars = setOf('{', '(', '[')

    override fun postProcessEnter(file: PsiFile, editor: Editor, dataContext: DataContext): EnterHandlerDelegate.Result? {
        if (file.fileType != RellFileType.INSTANCE) {
            return EnterHandlerDelegate.Result.Continue
        }

        val document = editor.document
        val text = document.charsSequence
        val caretOffset = editor.caretModel.offset

        if (caretOffset !in 0..text.length) {
            return EnterHandlerDelegate.Result.Continue
        }

        if (shouldAddIndent(text, caretOffset, editor)) {
            doIndent(file, editor, caretOffset)
            return EnterHandlerDelegate.Result.Stop
        }

        return EnterHandlerDelegate.Result.Continue
    }

    private fun shouldAddIndent(text: CharSequence, caretOffset: Int, editor: Editor): Boolean {
        val (prevLineStartOffset, prevLineEndOffset) = getPrevLineOffsets(editor, caretOffset)
        // Scan backwards to find the last non-whitespace char
        val lastNonWhitespace = (prevLineEndOffset - 1 downTo prevLineStartOffset)
                .firstOrNull { it < text.length && !text[it].isWhitespace() }

        return lastNonWhitespace != null && text[lastNonWhitespace] in indentAfterEnterChars
    }


    private fun getPrevLineOffsets(editor: Editor, caretOffset: Int): Pair<Int, Int> {
        val prevLineNumber = editor.document.getLineNumber(caretOffset) - 1
        val prevLineStartOffset = editor.document.getLineStartOffset(prevLineNumber)
        val prevLineEndOffset = editor.document.getLineEndOffset(prevLineNumber)
        return prevLineStartOffset to prevLineEndOffset
    }

    private fun doIndent(file: PsiFile, editor: Editor, caretOffset: Int) {
        val (useTabs, spaceSize) = getIndentationSettings(file, editor)
        val indentString = if (useTabs) {
            "\t"
        } else {
            val tabSize = spaceSize
            " ".repeat(tabSize)
        }

        editor.document.insertString(caretOffset, indentString)
        editor.caretModel.moveToOffset(caretOffset + indentString.length)

        PsiDocumentManager.getInstance(file.project).commitDocument(editor.document)
    }

    fun getIndentationSettings(file: PsiFile, editor: Editor): Pair<Boolean, Int> {
        val project = file.project
        val settings = editor.settings
        val useTabs = settings.isUseTabCharacter(project)
        val indentSize = settings.getTabSize(project)

        return useTabs to indentSize
    }
}