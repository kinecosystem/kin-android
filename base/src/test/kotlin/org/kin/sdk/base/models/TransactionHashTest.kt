package org.kin.sdk.base.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.kin.stellarfork.codec.Hex

class TransactionHashTest {

    @Test
    fun testEquality() {
        assertEquals(
            TransactionHash(Hex.Companion.decodeHex("aaff")),
            TransactionHash(Hex.Companion.decodeHex("aaff"))
        )
        assertEquals(
            TransactionHash(Hex.Companion.decodeHex("aaff")).hashCode(),
            TransactionHash(Hex.Companion.decodeHex("aaff")).hashCode()
        )
        assertNotEquals(
            TransactionHash(Hex.Companion.decodeHex("aaff")),
            TransactionHash(Hex.Companion.decodeHex("bbff"))
        )
    }
}
