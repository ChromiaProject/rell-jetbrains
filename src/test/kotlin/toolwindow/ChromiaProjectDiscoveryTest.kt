package net.postchain.rellide.jetbrains.toolwindow

import com.intellij.openapi.util.io.FileUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import net.postchain.rellide.jetbrains.chromia.ChromiaActiveSettings
import net.postchain.rellide.jetbrains.chromia.RellVersion
import net.postchain.rellide.jetbrains.toolwindow.project.ChromiaProjectDiscovery
import java.io.File

class ChromiaProjectDiscoveryTest : BasePlatformTestCase() {
    private lateinit var baseDir: File

    override fun setUp() {
        super.setUp()
        baseDir = FileUtil.createTempDirectory("chromia-discovery", null)
    }

    override fun tearDown() {
        try {
            FileUtil.delete(baseDir)
        } finally {
            super.tearDown()
        }
    }

    fun testDiscoversDefaultAndAlternateSettingsFiles() {
        write("plain/chromia.yml", "compile:\n  rellVersion: \"0.16.2\"\n")
        write("contracts/atbash.yml", settingsYml("0.14.15"))
        write("contracts/atbash_dev.yml", settingsYml("0.14.15"))
        write("contracts/build.yml", "not-a-settings-file: true\n")
        write("empty/readme.md", "no configs here")

        val projects = discover()

        assertEquals(listOf("contracts", "plain"), projects.map { it.name })

        val contracts = projects.single { it.name == "contracts" }
        assertEquals(listOf("atbash.yml", "atbash_dev.yml"), contracts.settingsFiles)
        assertEquals("Same version everywhere: first by name wins", "atbash.yml", contracts.activeSettingsFile)
        assertEquals(File(baseDir, "contracts/atbash.yml").absolutePath, contracts.configFile)
        assertEquals(RellVersion(0, 14, 15), contracts.activeDeclaredVersion)

        val plain = projects.single { it.name == "plain" }
        assertEquals(listOf("chromia.yml"), plain.settingsFiles)
        assertEquals("chromia.yml", plain.activeSettingsFile)
        assertEquals(RellVersion(0, 16, 2), plain.activeDeclaredVersion)
    }

    fun testActiveSelectionDrivesDiscovery() {
        write("contracts/atbash.yml", settingsYml("0.16.1"))
        write("contracts/atbash_dev.yml", settingsYml("0.16.1"))
        val directory = File(baseDir, "contracts").absolutePath

        ChromiaActiveSettings.getInstance(project).setActive(directory, "atbash_dev.yml")
        try {
            val contracts = discover().single()
            assertEquals("atbash_dev.yml", contracts.activeSettingsFile)
            assertEquals(File(directory, "atbash_dev.yml").absolutePath, contracts.configFile)
        } finally {
            ChromiaActiveSettings.getInstance(project).setActive(directory, null)
        }
    }

    fun testChromiaYmlWinsTheDefaultChoice() {
        write("mixed/chromia.yml", "compile:\n  rellVersion: \"0.16.1\"\n")
        write("mixed/newer.yml", settingsYml("0.16.2"))

        assertEquals("chromia.yml", discover().single().activeSettingsFile)
    }

    private fun discover() =
        ChromiaProjectDiscovery.discoverProjects(baseDir.absolutePath, ChromiaActiveSettings.getInstance(project))

    private fun settingsYml(version: String) =
        "blockchains:\n  my_chain:\n    module: main\ncompile:\n  rellVersion: \"$version\"\n"

    private fun write(relPath: String, content: String) {
        val file = File(baseDir, relPath)
        file.parentFile.mkdirs()
        file.writeText(content)
    }
}
