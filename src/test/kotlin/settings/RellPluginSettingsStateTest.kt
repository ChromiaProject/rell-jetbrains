package net.postchain.rellide.jetbrains.settings

import net.postchain.rellide.jetbrains.chromia.RellVersion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RellPluginSettingsStateTest {
    private val chrOutput = """
        chr version 0.33.2
        rell version 0.16.1
        postchain version 3.49.16
        EIF version 0.32.6
        Java version 21.0.11
    """.trimIndent()

    private fun state(command: String = "/opt/chr") = RellPluginSettingsState().apply {
        chromiaCliCommand = command
    }

    @Test
    fun parsesRellVersionFromRecordedOutput() {
        val state = state()
        state.recordChrVersionOutput("/opt/chr", chrOutput)
        assertTrue(state.chrVersionInfoIsCurrent())
        assertEquals(RellVersion(0, 16, 1), state.reportedMaxRellVersion())
    }

    @Test
    fun recordWithoutOverrideKeysOnEffectiveCommand() {
        val state = state()
        state.recordChrVersionOutput(null, chrOutput)
        assertEquals("/opt/chr", state.chrVersionCommand)
        assertEquals(RellVersion(0, 16, 1), state.reportedMaxRellVersion())
    }

    @Test
    fun staleInfoIsIgnoredWhenCommandChanges() {
        val state = state()
        state.recordChrVersionOutput("/opt/chr", chrOutput)
        state.chromiaCliCommand = "/other/chr"
        assertFalse(state.chrVersionInfoIsCurrent())
        assertNull(state.reportedMaxRellVersion())
    }

    @Test
    fun noRecordedRunMeansNoVersion() {
        assertFalse(state().chrVersionInfoIsCurrent())
        assertNull(state().reportedMaxRellVersion())
    }

    @Test
    fun outputWithoutRellLineMeansNoVersion() {
        val state = state()
        state.recordChrVersionOutput("/opt/chr", "chr version 0.33.2")
        assertNull(state.reportedMaxRellVersion())
    }

    @Test
    fun malformedRellVersionMeansNoVersion() {
        val state = state()
        state.recordChrVersionOutput("/opt/chr", "rell version aboba")
        assertNull(state.reportedMaxRellVersion())
    }
}
