package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range
import java.io.File

class ChromiaMissingLibDetectorTest : BasePlatformTestCase() {

    private lateinit var tempRoot: File
    private lateinit var contentRoot: File
    private lateinit var vContentRoot: VirtualFile
    private lateinit var resolver: RellVersionResolver

    override fun setUp() {
        super.setUp()
        tempRoot = FileUtil.createTempDirectory("chromia-missing-lib", null)
        contentRoot = File(tempRoot, "content").apply { mkdirs() }
        vContentRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(contentRoot)
            ?: error("VFS refresh failed for $contentRoot")
        PsiTestUtil.addContentRoot(myFixture.module, vContentRoot)
        resolver = RellVersionResolver.getInstance(project)
        resolver.dropCaches()
    }

    override fun tearDown() {
        try {
            PsiTestUtil.removeContentEntry(myFixture.module, vContentRoot)
            FileUtil.delete(tempRoot)
        } finally {
            super.tearDown()
        }
    }

    fun testMissingWholeSubmoduleIsFlagged() {
        file("proj/chromia.yml", ymlWithLibs())
        file("proj/src/lib/ai_inference/module.rell")
        val source = file("proj/src/lib/ai_inference/main.rell")

        val modulePath = ChromiaMissingLibDetector.missingLibModule(
            project,
            source,
            moduleNotFound("lib.hybridcompute.test.helpers"),
        )

        assertEquals("lib.hybridcompute.test.helpers", modulePath)
    }

    fun testTypoInsideExistingModuleIsNotFlagged() {
        file("proj/chromia.yml", ymlWithLibs())
        file("proj/src/lib/ai_inference/test/helpers.rell")
        val source = file("proj/src/lib/ai_inference/test/main.rell")

        val modulePath = ChromiaMissingLibDetector.missingLibModule(
            project,
            source,
            moduleNotFound("lib.ai_inference.test.mistyped_helper"),
        )

        assertNull(modulePath)
    }

    fun testNoLibsDeclaredIsNotFlagged() {
        file("proj/chromia.yml", yml())
        val source = file("proj/src/main.rell")

        val modulePath = ChromiaMissingLibDetector.missingLibModule(
            project,
            source,
            moduleNotFound("lib.hybridcompute"),
        )

        assertNull(modulePath)
    }

    fun testNonMatchingDiagnosticMessageIsIgnored() {
        file("proj/chromia.yml", ymlWithLibs())
        val source = file("proj/src/main.rell")

        val diagnostic = Diagnostic(Range(Position(0, 0), Position(0, 1)), "Something else entirely")
        assertNull(ChromiaMissingLibDetector.missingLibModule(project, source, diagnostic))
    }

    private fun moduleNotFound(modulePath: String): Diagnostic =
        Diagnostic(Range(Position(0, 0), Position(0, 1)), "Module '$modulePath' not found")

    private fun yml() = "compile:\n  rellVersion: \"0.16.1\"\n  source: src\n"

    private fun ymlWithLibs() = "compile:\n  rellVersion: \"0.16.1\"\n  source: src\nlibs:\n  ft4:\n    version: \"1.1\"\n"

    private fun file(relPath: String, content: String = ""): VirtualFile {
        val f = File(contentRoot, relPath)
        f.parentFile.mkdirs()
        f.writeText(content)
        return LocalFileSystem.getInstance().refreshAndFindFileByIoFile(f)
            ?: error("VFS refresh failed for $f")
    }
}
