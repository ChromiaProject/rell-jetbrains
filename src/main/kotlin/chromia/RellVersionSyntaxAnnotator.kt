package net.postchain.rellide.jetbrains.chromia

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile

/**
 * Runs the version-exact ANTLR parser for files governed by an older supported Rell version and
 * reports its syntax errors — syntax that only entered the grammar after the declared version.
 *
 * The editor PSI always uses the newest grammar (a superset), so this annotator is the only place
 * where "valid in the newest Rell but not in the project's Rell" surfaces client-side; the
 * version-matched language server reports the same at compiler level.
 */
class RellVersionSyntaxAnnotator :
    ExternalAnnotator<RellVersionSyntaxAnnotator.FileContent, RellVersionSyntaxAnnotator.VersionErrors>() {

    data class FileContent(val text: String, val version: RellVersion, val configName: String)

    data class VersionErrors(
        val version: RellVersion,
        val configName: String,
        val errors: List<VersionedRellParsers.SyntaxError>,
    )

    override fun collectInformation(file: PsiFile): FileContent? {
        val virtualFile = file.virtualFile ?: return null
        val resolution = RellVersionResolver.getInstance(file.project).resolve(virtualFile)
        // Covers Conflicting too: the chosen settings file's version governs highlighting.
        val version = resolution.effectiveVersion ?: return null
        if (version >= RellVersionRegistry.max || !VersionedRellParsers.supports(version)) return null
        return FileContent(file.text, version, resolution.configFile?.name ?: RellVersionResolver.CHROMIA_YML)
    }

    override fun doAnnotate(collectedInfo: FileContent?): VersionErrors? {
        val info = collectedInfo ?: return null
        return VersionErrors(info.version, info.configName, VersionedRellParsers.parse(info.version, info.text))
    }

    override fun apply(file: PsiFile, annotationResult: VersionErrors?, holder: AnnotationHolder) {
        val result = annotationResult ?: return
        val document = file.viewProvider.document ?: return
        for (error in result.errors) {
            val range = rangeOf(document, error) ?: continue
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Not valid in Rell ${result.version} (declared in ${result.configName}): ${error.message}",
            ).range(range).create()
        }
    }

    private fun rangeOf(document: Document, error: VersionedRellParsers.SyntaxError): TextRange? {
        if (error.line !in 1..document.lineCount) return null
        val lineStart = document.getLineStartOffset(error.line - 1)
        val lineEnd = document.getLineEndOffset(error.line - 1)
        // ANTLR's CodePointCharStream counts code points; Document offsets are UTF-16 units.
        val lineText = document.getText(TextRange(lineStart, lineEnd))
        val startInLine = codePointsToUtf16(lineText, 0, error.column)
        val start = lineStart + startInLine
        val end = (lineStart + codePointsToUtf16(lineText, startInLine, error.length)).coerceAtMost(document.textLength)
        if (end > start) return TextRange(start, end)
        // Zero-width location (e.g. at EOF): highlight the preceding character if there is one.
        return if (start > 0) TextRange(start - 1, start) else null
    }

    private fun codePointsToUtf16(text: String, fromUtf16Index: Int, codePoints: Int): Int = try {
        text.offsetByCodePoints(fromUtf16Index, codePoints)
    } catch (_: IndexOutOfBoundsException) {
        text.length
    }
}
