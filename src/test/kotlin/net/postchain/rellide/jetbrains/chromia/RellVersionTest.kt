package net.postchain.rellide.jetbrains.chromia

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RellVersionTest {

    @Test
    fun parsesStrictThreeComponentVersions() {
        assertEquals(RellVersion(0, 16, 1), RellVersion.parse("0.16.1"))
        assertEquals(RellVersion(1, 0, 0), RellVersion.parse("1.0.0"))
        assertEquals(RellVersion(0, 0, 0), RellVersion.parse("0.0.0"))
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
