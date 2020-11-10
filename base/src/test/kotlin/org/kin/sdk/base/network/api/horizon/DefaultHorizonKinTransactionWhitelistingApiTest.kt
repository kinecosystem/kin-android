package org.kin.sdk.base.network.api.horizon

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.kin.sdk.base.network.api.KinTransactionWhitelistingApi
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.tools.TestUtils
import org.kin.stellarfork.codec.Base64

class DefaultHorizonKinTransactionWhitelistingApiTest {

    lateinit var sut: KinTransactionWhitelistingApi

    @Before
    fun setUp() {

        sut = DefaultHorizonKinTransactionWhitelistingApi()
    }

    @Test
    fun isWhitelistingAvailable() {
        assertEquals(sut.isWhitelistingAvailable, false)
    }

    @Test
    fun whitelistTransaction() {
        val signedTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAAAAAAAA\n" +
                    "AAABAAAAAAAAAAEAAAAAIQcuqQ76FFMsBE6P9dhaJfbYuXBrsX8Q38U9xreKIC8AAAAAAAAAAAC7\n" +
                    "ruAAAAAAAAAAAcIY7/sAAABA6Qs1HI1B40fJNBc0RR0R7WfLDqKgniTGcT7yWa5ogAlEHwIuX54f\n" +
                    "HPv+sqKmCXa9JRadOmnPxi0/24UGFuUrDw==",
            KinTransaction.RecordType.InFlight(System.currentTimeMillis())
        )
        val signedTransactionBase64String =
            Base64.encodeBase64String(signedTransaction.bytesValue)!!

        sut.whitelistTransaction(
            KinTransactionWhitelistingApi.WhitelistTransactionRequest(
                signedTransactionBase64String
            )
        ) {
            assertEquals(
                signedTransactionBase64String,
                it.base64EncodedWhitelistedTransactionEnvelopeBytes
            )
        }
    }
}
