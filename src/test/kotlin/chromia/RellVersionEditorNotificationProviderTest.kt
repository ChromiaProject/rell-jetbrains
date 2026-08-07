package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFile

class RellVersionEditorNotificationProviderTest : RellVersionAwareTestCase() {

    private val provider = RellVersionEditorNotificationProvider()

    fun testBannerShownForUnsupportedVersion() {
        val file = rellFile("ceased", "0.14.5")
        assertNotNull("0.14.5 (the chr template default) must show the cease banner", collect(file))
        assertNotNull(
            "0.16.0 (dropped in plugin 0.4.2) must show the cease banner",
            collect(rellFile("dropped", "0.16.0")),
        )
    }

    fun testBannerShownForClampedVersion() {
        val file = rellFile("clamped", "0.17.0")
        assertNotNull("Unknown newer versions must show the update-plugin banner", collect(file))
    }

    fun testNoBannerForSupportedOrDefaultVersions() {
        assertNull(collect(rellFile("newest", "${RellVersionRegistry.max}")))
        assertNull(collect(rellFile("older", "0.16.1")))
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

    // A file that still gets a working toolchain gets no banner, however many settings files
    // claim it and however much they disagree: that is the status-bar widget's job, and a
    // permanent bar on every .rell editor is noise. The disagreement is still resolved and
    // reported through the resolution itself.
    fun testNoBannerForConflictingSettingsFiles() {
        myFixture.addFileToProject("conflict/a.yml", settingsYml("0.16.1"))
        myFixture.addFileToProject("conflict/b.yml", settingsYml("0.16.2"))
        val source = myFixture.addFileToProject("conflict/src/main.rell", "module;\n").virtualFile

        assertTrue(
            RellVersionResolver.getInstance(project).resolve(source) is RellVersionResolution.Conflicting,
        )
        assertNull("Disagreement is shown in the status bar, not as a banner", collect(source))
        assertTrue(
            "The widget must be able to report the disagreement",
            ChromiaSettingsChooser.choiceFor(project, source)!!.conflicting,
        )
    }

    fun testNoBannerForIgnoredBelowFloorSettingsFile() {
        myFixture.addFileToProject("ignored/chromia.yml", yml("0.16.6"))
        val legacy = myFixture.addFileToProject("ignored/legacy.yml", settingsYml("0.14.15")).virtualFile
        val source = myFixture.addFileToProject("ignored/src/main.rell", "module;\n").virtualFile

        assertEquals(RellVersionRegistry.max, RellVersionResolver.getInstance(project).resolve(source).effectiveVersion)
        assertNull("The working file is not nagged about a sibling", collect(source))
        assertNotNull("The unusable settings file is flagged on its own editor", collect(legacy))
    }

    fun testNoBannerWhenSettingsFilesAgree() {
        myFixture.addFileToProject("agree/chromia.yml", yml("0.16.2"))
        myFixture.addFileToProject("agree/alt.yml", settingsYml("0.16.2"))
        val source = myFixture.addFileToProject("agree/src/main.rell", "module;\n").virtualFile
        assertNull(collect(source))
    }

    // The real Atbash-Dashboard/contracts layout: four settings files sharing one src/, three on
    // a supported version and one legacy deployment variant below the floor.
    fun testAtbashLayoutFlagsTheBelowFloorVariant() {
        for ((name, version) in mapOf(
            "atbash.yml" to "0.16.2",
            "atbash_dev.yml" to "0.16.2",
            "atbash_dev_private.yml" to "0.16.2",
            "atbash_private.yml" to "0.14.15",
        )) {
            myFixture.addFileToProject("contracts/$name", settingsYml(version))
        }
        val source = myFixture.addFileToProject("contracts/src/main/main.rell", "module;\n").virtualFile

        val resolution = RellVersionResolver.getInstance(project).resolve(source)
        assertEquals(
            "atbash.yml governs: first by name among the in-scope files",
            "atbash.yml",
            resolution.configFile?.name,
        )
        assertEquals(RellVersion(0, 16, 2), resolution.effectiveVersion)
        assertNull("Working .rell files carry no banner about sibling variants", collect(source))
        assertNotNull(
            "The below-floor variant is surfaced on its own editor, not on every .rell file",
            collect(myFixture.findFileInTempDir("contracts/atbash_private.yml")),
        )
    }

    fun testBannerOnTheSettingsFileDeclaringAnUnsupportedVersion() {
        val config = myFixture.addFileToProject("on-yml/atbash_private.yml", settingsYml("0.14.15")).virtualFile
        assertNotNull(
            "The settings file that declares the unsupported version must say so itself",
            collect(config),
        )
        assertNotNull(
            "Newer-than-known versions warn on the settings file too",
            collect(myFixture.addFileToProject("on-yml-new/x.yml", settingsYml("0.99.0")).virtualFile),
        )
        assertNull(
            "A supported version needs no banner",
            collect(myFixture.addFileToProject("on-yml-ok/y.yml", settingsYml("0.16.2")).virtualFile),
        )
        assertNull(
            "A yml that is not a Chromia settings file is none of our business",
            collect(myFixture.addFileToProject("on-yml-other/ci.yml", "jobs:\n  build:\n").virtualFile),
        )
    }

    private fun collect(file: VirtualFile) = provider.collectNotificationData(project, file)

    private fun yml(version: String) = "compile:\n  rellVersion: \"$version\"\n"

    private fun settingsYml(version: String) =
        "blockchains:\n  my_chain:\n    module: main\n" + yml(version)

    private fun rellFile(dir: String, rellVersion: String?): VirtualFile {
        if (rellVersion != null) {
            myFixture.addFileToProject("$dir/chromia.yml", yml(rellVersion))
        }
        return myFixture.addFileToProject("$dir/src/main.rell", "module;\n").virtualFile
    }
}
