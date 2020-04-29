package org.kin.sdk.base.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.kin.sdk.base.tools.TestUtils
import org.kin.stellarfork.codec.Hex
import kotlin.test.assertTrue

class KinPaymentTest {

    companion object {
        val account1 = TestUtils.newSigningKinAccount()
        val account2 = TestUtils.newSigningKinAccount()
    }

    @Test
    fun testEquality() {
        assertEquals(
            KinPayment(
                KinPayment.Id(TransactionHash(Hex.decodeHex("aaff")), 0),
                KinPayment.Status.InFlight,
                account1.id,
                account2.id,
                KinAmount(25),
                QuarkAmount(100),
                KinMemo.NONE,
                12345L,
                null
            ),
            KinPayment(
                KinPayment.Id(TransactionHash(Hex.decodeHex("aaff")), 0),
                KinPayment.Status.InFlight,
                account1.id,
                account2.id,
                KinAmount(25),
                QuarkAmount(100),
                KinMemo.NONE,
                12345L,
                null
            )
        )
        assertEquals(
            KinPayment.Id(TransactionHash(Hex.decodeHex("aaff")), 0),
            KinPayment.Id(TransactionHash(Hex.decodeHex("aaff")), 0)
        )
        assertTrue(
            KinPayment.Id(TransactionHash(Hex.decodeHex("aaff")), 0).value.contentEquals(
                KinPayment.Id(TransactionHash(Hex.decodeHex("aaff")), 0).value
            )
        )
        assertNotEquals(
            KinPayment.Id(TransactionHash(Hex.decodeHex("aaff")), 0),
            KinPayment.Id(TransactionHash(Hex.decodeHex("bbff")), 0)
        )

        assertNotEquals(
            KinPayment(
                KinPayment.Id(TransactionHash(Hex.decodeHex("aaff")), 0),
                KinPayment.Status.InFlight,
                account1.id,
                account2.id,
                KinAmount(25),
                QuarkAmount(100),
                KinMemo.NONE,
                12345L,
                null
            ),
            KinPayment(
                KinPayment.Id(TransactionHash(Hex.decodeHex("aaff")), 0),
                KinPayment.Status.Error(KinPayment.Status.Error.Reason(0, "Blarg")),
                account1.id,
                account2.id,
                KinAmount(25),
                QuarkAmount(100),
                KinMemo.NONE,
                12345L,
                null
            )
        )

        assertNotEquals(
            KinPayment.Status.InFlight,
            KinPayment.Status.Error(KinPayment.Status.Error.Reason(0, "Blarg"))
        )
    }
}
