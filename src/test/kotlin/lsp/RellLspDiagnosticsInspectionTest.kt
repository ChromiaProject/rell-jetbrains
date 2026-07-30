package net.postchain.rellide.jetbrains.lsp

import com.intellij.codeInspection.InspectionManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.eclipse.lsp4j.*

class RellLspDiagnosticsInspectionTest : BasePlatformTestCase() {
    fun testBatchModeReportsCachedServerDiagnostics() {
        val psiFile = myFixture.addFileToProject("src/main.rell", "module;\nbroken line\n")
        // The server derives URIs with java.net.URI, the lookup starts from the VirtualFile path;
        // publish in the server's form to prove the two meet in the cache.
        RellLspDiagnosticsCache.getInstance(project).record(
            PublishDiagnosticsParams(
                "file://${psiFile.virtualFile.path}",
                listOf(diagnostic("cannot resolve", line = 1, start = 0, end = 6, DiagnosticSeverity.Error)),
            )
        )

        val problems = RellLspDiagnosticsInspection()
            .checkFile(psiFile, InspectionManager.getInstance(project), false)

        assertEquals(1, problems!!.size)
        assertEquals("cannot resolve", problems[0].descriptionTemplate)
        assertEquals(
            "broken",
            problems[0].psiElement.text.substring(
                problems[0].textRangeInElement!!.startOffset,
                problems[0].textRangeInElement!!.endOffset
            )
        )
    }

    fun testOnTheFlyModeReportsNothingToAvoidDuplicatingEditorHighlighting() {
        val psiFile = myFixture.addFileToProject("src/fly.rell", "module;\n")
        RellLspDiagnosticsCache.getInstance(project).record(
            PublishDiagnosticsParams(
                "file://${psiFile.virtualFile.path}",
                listOf(diagnostic("some error", line = 0, start = 0, end = 6, DiagnosticSeverity.Error)),
            )
        )

        assertNull(RellLspDiagnosticsInspection().checkFile(psiFile, InspectionManager.getInstance(project), true))
    }

    fun testEmptyPublishClearsTheFile() {
        val psiFile = myFixture.addFileToProject("src/fixed.rell", "module;\n")
        val uri = "file://${psiFile.virtualFile.path}"
        val cache = RellLspDiagnosticsCache.getInstance(project)
        cache.record(
            PublishDiagnosticsParams(
                uri,
                listOf(diagnostic("stale", line = 0, start = 0, end = 6, DiagnosticSeverity.Error))
            )
        )
        cache.record(PublishDiagnosticsParams(uri, emptyList()))

        assertNull(RellLspDiagnosticsInspection().checkFile(psiFile, InspectionManager.getInstance(project), false))
    }

    private fun diagnostic(message: String, line: Int, start: Int, end: Int, severity: DiagnosticSeverity): Diagnostic =
        Diagnostic(Range(Position(line, start), Position(line, end)), message).also { it.severity = severity }
}
