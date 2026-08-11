package net.postchain.rellide.jetbrains.chromia

import org.junit.Assert.assertEquals
import org.junit.Test

class ChromiaInstallCommandTest {

    @Test
    fun `default settings file needs no argument`() {
        assertEquals("chr install", ChromiaInstallCommand.commandFor("chromia.yml"))
        assertEquals("chr install", ChromiaInstallCommand.commandFor(null))
    }

    @Test
    fun `alternate settings file is passed explicitly`() {
        assertEquals("chr install --settings atbash_dev.yml", ChromiaInstallCommand.commandFor("atbash_dev.yml"))
    }
}
