package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RellVersionEditorNotificationProviderTest : BasePlatformTestCase() {

    private val provider = RellVersionEditorNotificationProvider()

    override fun setUp() {
        super.setUp()
        RellVersionResolver.getInstance(project).dropCaches()
    }

    fun testBannerShownForUnsupportedVersion() {
        val file = rellFile("ceased", "0.14.5")
        assertNotNull("0.14.5 (the chr template default) must show the cease banner", collect(file))
    }

    fun testBannerShownForClampedVersion() {
        val file = rellFile("clamped", "0.17.0")
        assertNotNull("Unknown newer versions must show the update-plugin banner", collect(file))
    }

    fun testNoBannerForSupportedOrDefaultVersions() {
        assertNull(collect(rellFile("newest", "0.16.1")))
        assertNull(collect(rellFile("older", "0.16.0")))
        assertNull(collect(rellFile("no-config", null)))
    }

    fun testNoBannerForNonRellFiles() {
        myFixture.addFileToProject("other/chromia.yml", yml("0.14.5"))
        val readme = myFixture.addFileToProject("other/README.md", "docs").virtualFile
        assertNull(collect(readme))
    }

    fun testQuickFixRewritesDeclaredVersion() {
        for ((name, declaration) in mapOf(
            "double-quoted" to "\"0.14.5\"",
            "single-quoted" to "'0.14.5'",
            "bare" to "0.14.5",
            "trailing-comment" to "0.14.5 # legacy",
        )) {
            val config = myFixture.addFileToProject(
                "fix-$name/chromia.yml",
                "# rellVersion: 0.9.9 must not be touched\ncompile:\n  rellVersion: $declaration\n  source: src\n",
            ).virtualFile
            val source = myFixture.addFileToProject("fix-$name/src/main.rell", "module;\n").virtualFile

            assertTrue(RellVersionResolver.getInstance(project).resolve(source) is RellVersionResolution.Unsupported)

            ChromiaConfigQuickFix.setDeclaredRellVersion(project, config, RellVersionRegistry.max)

            val text = FileDocumentManager.getInstance().getDocument(config)!!.text
            assertTrue(
                "case $name: expected rewritten version in: $text",
                text.contains("rellVersion: \"${RellVersionRegistry.max}\""),
            )
            assertTrue("case $name: source key must survive", text.contains("source: src"))
            assertTrue(
                "case $name: commented-out declarations must not be rewritten",
                text.contains("# rellVersion: 0.9.9 must not be touched"),
            )
            assertEquals(
                "case $name: resolver must now see the newest version",
                RellVersionRegistry.max,
                RellVersionResolver.getInstance(project).resolve(source).effectiveVersion,
            )
        }
    }

    private fun collect(file: VirtualFile) = provider.collectNotificationData(project, file)

    private fun yml(version: String) = "compile:\n  rellVersion: \"$version\"\n"

    private fun rellFile(dir: String, rellVersion: String?): VirtualFile {
        if (rellVersion != null) {
            myFixture.addFileToProject("$dir/chromia.yml", yml(rellVersion))
        }
        return myFixture.addFileToProject("$dir/src/main.rell", "module;\n").virtualFile
    }
}
