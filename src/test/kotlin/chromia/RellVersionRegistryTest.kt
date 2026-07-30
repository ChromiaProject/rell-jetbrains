package net.postchain.rellide.jetbrains.chromia

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RellVersionRegistryTest {

    @Test
    fun registryMatchesCompatibilityPolicy() {
        assertEquals(expected = RellVersion(0, 16, 1), actual = RellVersionRegistry.floor)
        assertTrue(RellVersionRegistry.max >= RellVersionRegistry.floor)
        assertTrue(RellVersionRegistry.isSupported(RellVersionRegistry.floor))
        assertTrue(RellVersionRegistry.isSupported(RellVersionRegistry.max))
        assertEquals(
            expected = RellVersionRegistry.supported,
            actual = RellVersionRegistry.supported.sorted().distinct(),
        )
    }
}
