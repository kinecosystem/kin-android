package org.kin.sdk.base.stellar.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.kin.sdk.base.stellar.models.KinTransaction.ResultCode
import org.kin.stellarfork.codec.Base64
import org.kin.stellarfork.xdr.Int64
import org.kin.stellarfork.xdr.TransactionResult
import org.kin.stellarfork.xdr.TransactionResultCode
import org.kin.stellarfork.xdr.XdrDataOutputStream
import java.io.ByteArrayOutputStream

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


        val resultXdrBytes: ByteArray =
            Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
        assertEquals(
            KinTransaction.RecordType.Acknowledged(timestamp, resultXdrBytes),
            KinTransaction.RecordType.Acknowledged(timestamp, resultXdrBytes)
        )
        assertEquals(
            KinTransaction.RecordType.Acknowledged(timestamp, resultXdrBytes).hashCode(),
            KinTransaction.RecordType.Acknowledged(timestamp, resultXdrBytes).hashCode()
        )
        val acknowledgedRecord = KinTransaction.RecordType.Acknowledged(timestamp, resultXdrBytes)
        assertEquals(acknowledgedRecord, acknowledgedRecord)


        val pagingToken = KinTransaction.PagingToken("some_token")
        assertEquals(
            KinTransaction.RecordType.Historical(timestamp, resultXdrBytes, pagingToken),
            KinTransaction.RecordType.Historical(timestamp, resultXdrBytes, pagingToken)
        )
        assertEquals(
            KinTransaction.RecordType.Historical(timestamp, resultXdrBytes, pagingToken).hashCode(),
            KinTransaction.RecordType.Historical(timestamp, resultXdrBytes, pagingToken).hashCode()
        )
        val historicalRecord =
            KinTransaction.RecordType.Historical(timestamp, resultXdrBytes, pagingToken)
        assertEquals(historicalRecord, historicalRecord)

        assertNotEquals(inFlightRecord, acknowledgedRecord)
        assertNotEquals(historicalRecord, acknowledgedRecord)
    }

    private fun createDummyResult(resultCode: TransactionResultCode): ByteArray {
        val transactionResult = TransactionResult().apply {
            result = TransactionResult.TransactionResultResult().apply {
                discriminant = resultCode
            }
            feeCharged = Int64().apply {
                int64 = 100
            }
            ext = TransactionResult.TransactionResultExt().apply {
                discriminant = 0
            }
        }
        val os = ByteArrayOutputStream()
        val outputStream = XdrDataOutputStream(os)
        TransactionResult.encode(outputStream, transactionResult)
        return os.toByteArray()
    }

    @Test
    fun testResultCodes() {
        assertEquals(
            ResultCode.Failed,
            KinTransaction.RecordType.parseResultCode(createDummyResult(TransactionResultCode.txFAILED))
        )
        assertEquals(
            ResultCode.TooEarly,
            KinTransaction.RecordType.parseResultCode(createDummyResult(TransactionResultCode.txTOO_EARLY))
        )
        assertEquals(
            ResultCode.TooLate,
            KinTransaction.RecordType.parseResultCode(createDummyResult(TransactionResultCode.txTOO_LATE))
        )
        assertEquals(
            ResultCode.MissingOperation,
            KinTransaction.RecordType.parseResultCode(createDummyResult(TransactionResultCode.txMISSING_OPERATION))
        )
        assertEquals(
            ResultCode.BadSequenceNumber,
            KinTransaction.RecordType.parseResultCode(createDummyResult(TransactionResultCode.txBAD_SEQ))
        )
        assertEquals(
            ResultCode.BadAuth,
            KinTransaction.RecordType.parseResultCode(createDummyResult(TransactionResultCode.txBAD_AUTH))
        )
        assertEquals(
            ResultCode.InsufficientBalance,
            KinTransaction.RecordType.parseResultCode(createDummyResult(TransactionResultCode.txINSUFFICIENT_BALANCE))
        )
        assertEquals(
            ResultCode.NoAccount,
            KinTransaction.RecordType.parseResultCode(createDummyResult(TransactionResultCode.txNO_ACCOUNT))
        )
        assertEquals(
            ResultCode.InsufficientFee,
            KinTransaction.RecordType.parseResultCode(createDummyResult(TransactionResultCode.txINSUFFICIENT_FEE))
        )
        assertEquals(
            ResultCode.BadAuthExtra,
            KinTransaction.RecordType.parseResultCode(createDummyResult(TransactionResultCode.txBAD_AUTH_EXTRA))
        )
        assertEquals(
            ResultCode.InternalError,
            KinTransaction.RecordType.parseResultCode(createDummyResult(TransactionResultCode.txINTERNAL_ERROR))
        )
    }
}
