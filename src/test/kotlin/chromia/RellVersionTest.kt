package net.postchain.rellide.jetbrains.chromia

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RellVersionTest {
    @Test
    fun parsesStrictThreeComponentVersions() {
        assertEquals(expected = RellVersion(0, 16, 1), actual = RellVersion.parse("0.16.1"))
        assertEquals(expected = RellVersion(1, 0, 0), actual = RellVersion.parse("1.0.0"))
        assertEquals(expected = RellVersion(0, 0, 0), actual = RellVersion.parse("0.0.0"))
    }

    @Test
    fun rejectsAnythingElse() {
        assertNull(RellVersion.parse(""))
        assertNull(RellVersion.parse("0.16"))
        assertNull(RellVersion.parse("0.16.1.2"))
        assertNull(RellVersion.parse("0.16.01"))
        assertNull(RellVersion.parse("0.16.1-SNAPSHOT"))
        assertNull(RellVersion.parse("v0.16.1"))
        assertNull(RellVersion.parse("banana"))
        assertNull(RellVersion.parse(" 0.16.1"))
        assertNull(RellVersion.parse("0.16.99999999999999"))
    }

    @Test
    fun comparesNumerically() {
        assertTrue(RellVersion(0, 16, 0) < RellVersion(0, 16, 1))
        assertTrue(RellVersion(0, 16, 9) < RellVersion(0, 17, 0))
        assertTrue(RellVersion(0, 17, 0) < RellVersion(1, 0, 0))
        assertTrue(RellVersion(0, 9, 9) < RellVersion(0, 16, 0))
    }

    @Test
    fun rendersAsPlainVersionString() {
        assertEquals("0.16.1", RellVersion(0, 16, 1).toString())
    }
}
