package net.postchain.rellide.jetbrains.chromia

import com.intellij.openapi.vfs.VirtualFile

class ChromiaSettingsChooserTest : RellVersionAwareTestCase() {

    fun testChoiceListsEveryClaimantAndTheGoverningOne() {
        val source = atbashLayout("pick")
        val choice = ChromiaSettingsChooser.choiceFor(project, source)!!

        assertEquals("pick", choice.directory.name)

        assertEquals(
            listOf("atbash.yml", "atbash_dev.yml", "atbash_private.yml"),
            choice.claimants.map { it.configFile.name },
        )

        assertEquals("atbash.yml", choice.governingName)
    }

    fun testSwitchingTheActiveFileMovesTheGoverningOne() {
        val source = atbashLayout("switch")
        val choice = ChromiaSettingsChooser.choiceFor(project, source)!!

        ChromiaActiveSettings.getInstance(project).setActive(choice.directory.path, "atbash_dev.yml")
        try {
            val after = ChromiaSettingsChooser.choiceFor(project, source)!!
            assertEquals("atbash_dev.yml", after.governingName)
            assertEquals(
                "The list itself does not change, only which entry governs",
                choice.claimants.map { it.configFile.name },
                after.claimants.map { it.configFile.name },
            )
        } finally {
            ChromiaActiveSettings.getInstance(project).setActive(choice.directory.path, null)
        }
    }

    fun testNoChoiceForFilesNoSettingsFileGoverns() {
        val orphan = myFixture.addFileToProject("orphan/src/main.rell", "module;\n").virtualFile
        assertNull(ChromiaSettingsChooser.choiceFor(project, orphan))
        assertNull(ChromiaSettingsChooser.choiceFor(project, null))
    }

    fun testVersionDescriptionsExplainWhatEachFileWouldRun() {
        val source = atbashLayout("describe")
        val byName = ChromiaSettingsChooser.choiceFor(project, source)!!.claimants.associateBy { it.configFile.name }

        assertEquals("Rell 0.16.2", ChromiaSettingsChooser.describeVersion(byName.getValue("atbash.yml")))
        assertEquals(
            "Rell 0.14.15, unsupported",
            ChromiaSettingsChooser.describeVersion(byName.getValue("atbash_private.yml")),
        )
    }

    // The active entry carries no marker: it is the initially selected row, and the status bar
    // already names it.
    fun testItemTextBracketsTheVersionAndMarksNothingActive() {
        val source = atbashLayout("label")
        val choice = ChromiaSettingsChooser.choiceFor(project, source)!!
        val labels = choice.claimants.map { ChromiaSettingsChooser.itemText(it) }

        assertEquals(
            listOf(
                "atbash.yml (Rell 0.16.2)",
                "atbash_dev.yml (Rell 0.16.2)",
                "atbash_private.yml (Rell 0.14.15, unsupported)",
            ),
            labels,
        )
        assertFalse("No entry may advertise itself as active", labels.any { it.contains("active") })
    }

    fun testClampedAndMissingVersionsAreDescribed() {
        myFixture.addFileToProject("desc2/newer.yml", settingsYml("0.99.0"))
        myFixture.addFileToProject("desc2/bare.yml", "blockchains:\n  c:\n    module: main\n")
        val source = myFixture.addFileToProject("desc2/src/main.rell", "module;\n").virtualFile
        val byName = ChromiaSettingsChooser.choiceFor(project, source)!!.claimants.associateBy { it.configFile.name }

        assertEquals(
            "Rell 0.99.0, using ${RellVersionRegistry.max}",
            ChromiaSettingsChooser.describeVersion(byName.getValue("newer.yml")),
        )
        assertEquals(
            "no rellVersion, using ${RellVersionRegistry.max}",
            ChromiaSettingsChooser.describeVersion(byName.getValue("bare.yml")),
        )
    }

    // The popup ends with a link to Settings | Tools | Rell, the way IDE widget popups do.
    fun testPopupOffersEverySettingsFilePlusTheSettingsLink() {
        val source = atbashLayout("popup")
        val choice = ChromiaSettingsChooser.choiceFor(project, source)!!
        val popup = ChromiaSettingsChooser.createPopup(project, choice)

        val rows = popup.listStep.values.map { popup.listStep.getTextFor(it) }
        assertEquals(
            listOf(
                "atbash.yml (Rell 0.16.2)",
                "atbash_dev.yml (Rell 0.16.2)",
                "atbash_private.yml (Rell 0.14.15, unsupported)",
                "Rell Settings…",
            ),
            rows,
        )
    }

    private fun atbashLayout(dir: String): VirtualFile {
        for ((name, version) in mapOf(
            "atbash.yml" to "0.16.2",
            "atbash_dev.yml" to "0.16.2",
            "atbash_private.yml" to "0.14.15",
        )) {
            myFixture.addFileToProject("$dir/$name", settingsYml(version))
        }
        return myFixture.addFileToProject("$dir/src/main.rell", "module;\n").virtualFile
    }

    private fun settingsYml(version: String) =
        "blockchains:\n  my_chain:\n    module: main\ncompile:\n  rellVersion: \"$version\"\n"
}
