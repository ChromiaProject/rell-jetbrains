package net.postchain.rellide.jetbrains.chromia

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RellVersionRegistryTest {

    @Test
    fun registryMatchesCompatibilityPolicy() {
        assertEquals(RellVersion(0, 16, 0), RellVersionRegistry.floor)
        assertTrue(RellVersionRegistry.max >= RellVersionRegistry.floor)
        assertTrue(RellVersionRegistry.isSupported(RellVersionRegistry.floor))
        assertTrue(RellVersionRegistry.isSupported(RellVersionRegistry.max))
        assertEquals(RellVersionRegistry.supported, RellVersionRegistry.supported.sorted().distinct())
    }
}
