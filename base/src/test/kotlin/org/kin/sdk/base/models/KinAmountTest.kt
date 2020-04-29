package org.kin.sdk.base.models

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigInteger

class KinAmountTest {

    @Test
    fun testAddition() {
        val expected = KinAmount(10)
        val calculated = KinAmount(1) + KinAmount(BigInteger.valueOf(7)) + KinAmount("2")
        assertEquals(expected, calculated)
        assertEquals(expected.hashCode(), calculated.hashCode())
    }

    @Test
    fun testAddition_withNegative() {
        val expected = KinAmount(10)
        val calculated = KinAmount(14.0) + KinAmount(BigInteger.valueOf(-4))
        assertEquals(expected, calculated)
        assertEquals(expected.hashCode(), calculated.hashCode())
    }

    @Test
    fun testSubtraction() {
        val expected = KinAmount(10)
        val calculated = KinAmount(14.0) - KinAmount(BigInteger.valueOf(4))
        assertEquals(expected, calculated)
        assertEquals(expected.hashCode(), calculated.hashCode())
    }
}
