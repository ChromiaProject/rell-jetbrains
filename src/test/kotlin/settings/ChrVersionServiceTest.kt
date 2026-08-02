package net.postchain.rellide.jetbrains.settings

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import net.postchain.rellide.jetbrains.chromia.RellVersion

class ChrVersionServiceTest : BasePlatformTestCase() {
    private lateinit var savedCliCommand: String
    private lateinit var savedVersionCommand: String
    private lateinit var savedVersionOutput: String

    override fun setUp() {
        super.setUp()
        val state = RellPluginSettingsState.instance
        savedCliCommand = state.chromiaCliCommand
        savedVersionCommand = state.chrVersionCommand
        savedVersionOutput = state.chrVersionOutput
    }

    override fun tearDown() {
        try {
            val state = RellPluginSettingsState.instance
            state.chromiaCliCommand = savedCliCommand
            state.chrVersionCommand = savedVersionCommand
            state.chrVersionOutput = savedVersionOutput
        } finally {
            super.tearDown()
        }
    }

    fun testProbeRecordsVersionInfoPassively() {
        val state = RellPluginSettingsState.instance
        // The trailing `#` swallows the `--version` argument the probe appends.
        state.chromiaCliCommand = "echo rell version 0.15.0 #"
        state.chrVersionCommand = ""
        state.chrVersionOutput = ""

        val service = ChrVersionService.getInstance()
        assertNull("First ask must miss and start the background probe", service.maxRellVersion())

        val deadline = System.currentTimeMillis() + 15_000
        while (!state.chrVersionInfoIsCurrent() && System.currentTimeMillis() < deadline) {
            Thread.sleep(50)
        }

        assertTrue("Probe did not record chr version info in time", state.chrVersionInfoIsCurrent())
        assertEquals(RellVersion(0, 15, 0), service.maxRellVersion())
    }
}
