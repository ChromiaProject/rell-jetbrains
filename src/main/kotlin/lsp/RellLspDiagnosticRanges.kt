package net.postchain.rellide.jetbrains.lsp

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.Position

/** Converts LSP diagnostic positions (line/character) to editor offsets, shared by anything that reads [RellLspDiagnosticsCache]. */
object RellLspDiagnosticRanges {
    fun textRange(document: Document, diagnostic: Diagnostic): TextRange? {
        val start = offset(document, diagnostic.range.start) ?: return null
        val end = offset(document, diagnostic.range.end) ?: return null
        if (start > end) return null
        // A zero-length range highlights nothing in inspection results; widen it to one character.
        return if (start == end && end < document.textLength) TextRange(start, end + 1) else TextRange(start, end)
    }

    private fun offset(document: Document, position: Position): Int? {
        if (position.line >= document.lineCount) return document.textLength.takeIf { document.lineCount > 0 }
        val lineStart = document.getLineStartOffset(position.line)
        val lineEnd = document.getLineEndOffset(position.line)
        return (lineStart + position.character).coerceAtMost(lineEnd)
    }
}
