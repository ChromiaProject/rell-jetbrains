package net.postchain.rellide.jetbrains.chromia

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class ChromiaActiveSettingsTest : BasePlatformTestCase() {

    fun testStateRoundTrip() {
        val settings = ChromiaActiveSettings.getInstance(project)
        settings.setActive("/proj/contracts", "atbash_dev.yml")
        try {
            val state = settings.state
            assertEquals("atbash_dev.yml", state.activeByDirectory["/proj/contracts"])

            val reloaded = ChromiaActiveSettings.State().apply {
                activeByDirectory = state.activeByDirectory.toMutableMap()
            }
            settings.loadState(reloaded)
            assertEquals("atbash_dev.yml", settings.activeFileName("/proj/contracts"))
        } finally {
            settings.setActive("/proj/contracts", null)
        }
    }

    // Discovery keys by java.io.File.absolutePath, the banners by VirtualFile.path; on Windows
    // those differ only in the separator, which would split the store in half.
    fun testKeysAreSeparatorAndTrailingSlashInsensitive() {
        val settings = ChromiaActiveSettings.getInstance(project)
        settings.setActive("C:\\proj\\contracts", "atbash.yml")
        try {
            assertEquals("atbash.yml", settings.activeFileName("C:/proj/contracts"))
            assertEquals("atbash.yml", settings.activeFileName("C:/proj/contracts/"))
        } finally {
            settings.setActive("C:/proj/contracts", null)
        }
        assertNull(settings.activeFileName("C:\\proj\\contracts"))
    }

    fun testClearingReturnsToDefaultRule() {
        val settings = ChromiaActiveSettings.getInstance(project)
        settings.setActive("/proj/contracts", "atbash.yml")
        settings.setActive("/proj/contracts", null)
        assertNull(settings.activeFileName("/proj/contracts"))
    }
}
