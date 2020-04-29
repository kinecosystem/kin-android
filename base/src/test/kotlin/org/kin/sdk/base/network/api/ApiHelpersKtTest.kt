package org.kin.sdk.base.network.api

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.kin.sdk.base.network.api.KinTransactionApi.SubmitTransactionResponse.Result
import org.kin.sdk.base.stellar.models.KinTransaction.ResultCode

class ApiHelpersKtTest {

    @Test
    fun toSubmitTransactionResult() {
        assertEquals(Result.Ok, ResultCode.Success.toSubmitTransactionResult())
        assertEquals(
            Result.BadSequenceNumber,
            ResultCode.BadSequenceNumber.toSubmitTransactionResult()
        )
        assertEquals(
            Result.InsufficientBalance,
            ResultCode.InsufficientBalance.toSubmitTransactionResult()
        )
        assertEquals(Result.InsufficientFee, ResultCode.InsufficientFee.toSubmitTransactionResult())
        assertEquals(Result.NoAccount, ResultCode.NoAccount.toSubmitTransactionResult())
        assertTrue(ResultCode.Failed.toSubmitTransactionResult() is Result.UndefinedError)
        assertTrue(ResultCode.TooEarly.toSubmitTransactionResult() is Result.UndefinedError)
        assertTrue(ResultCode.TooLate.toSubmitTransactionResult() is Result.UndefinedError)
        assertTrue(ResultCode.MissingOperation.toSubmitTransactionResult() is Result.UndefinedError)
        assertTrue(ResultCode.BadAuth.toSubmitTransactionResult() is Result.UndefinedError)
        assertTrue(ResultCode.BadAuthExtra.toSubmitTransactionResult() is Result.UndefinedError)
        assertTrue(ResultCode.InternalError.toSubmitTransactionResult() is Result.UndefinedError)
        assertTrue(null.toSubmitTransactionResult() is Result.UndefinedError)
    }
}
