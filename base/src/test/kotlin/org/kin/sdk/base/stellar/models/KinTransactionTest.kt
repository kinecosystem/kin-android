package org.kin.sdk.base.stellar.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class KinTransactionTest {
    @Test
    fun testRecordTypeEquality() {
        val timestamp = System.currentTimeMillis()
        assertEquals(
            KinTransaction.RecordType.InFlight(timestamp),
            KinTransaction.RecordType.InFlight(timestamp)
        )
        assertEquals(
            KinTransaction.RecordType.InFlight(timestamp).hashCode(),
            KinTransaction.RecordType.InFlight(timestamp).hashCode()
        )
        val inFlightRecord = KinTransaction.RecordType.InFlight(timestamp)
        assertEquals(inFlightRecord, inFlightRecord)


        val resultCode =
            KinTransaction.ResultCode.Success
        assertEquals(
            KinTransaction.RecordType.Acknowledged(timestamp, resultCode),
            KinTransaction.RecordType.Acknowledged(timestamp, resultCode)
        )
        assertEquals(
            KinTransaction.RecordType.Acknowledged(timestamp, resultCode).hashCode(),
            KinTransaction.RecordType.Acknowledged(timestamp, resultCode).hashCode()
        )
        val acknowledgedRecord = KinTransaction.RecordType.Acknowledged(timestamp, resultCode)
        assertEquals(acknowledgedRecord, acknowledgedRecord)


        val pagingToken = KinTransaction.PagingToken("some_token")
        assertEquals(
            KinTransaction.RecordType.Historical(timestamp, resultCode, pagingToken),
            KinTransaction.RecordType.Historical(timestamp, resultCode, pagingToken)
        )
        assertEquals(
            KinTransaction.RecordType.Historical(timestamp, resultCode, pagingToken).hashCode(),
            KinTransaction.RecordType.Historical(timestamp, resultCode, pagingToken).hashCode()
        )
        val historicalRecord =
            KinTransaction.RecordType.Historical(timestamp, resultCode, pagingToken)
        assertEquals(historicalRecord, historicalRecord)

        assertNotEquals(inFlightRecord, acknowledgedRecord)
        assertNotEquals(historicalRecord, acknowledgedRecord)
    }
}
