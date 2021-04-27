package org.kin.sdk.base.network.api

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.kin.sdk.base.network.api.KinTransactionApiV4.SubmitTransactionResponse.Result
import org.kin.sdk.base.stellar.models.KinTransaction.ResultCode

class ApiHelpersKtTest {

    @Test
    fun toSubmitTransactionResult() {
        assertEquals(Result.Ok, ResultCode.Success.toSubmitTransactionResultV4())
        assertEquals(
            Result.BadSequenceNumber,
            ResultCode.BadSequenceNumber.toSubmitTransactionResultV4()
        )
        assertEquals(
            Result.InsufficientBalance,
            ResultCode.InsufficientBalance.toSubmitTransactionResultV4()
        )
        assertEquals(Result.InsufficientFee, ResultCode.InsufficientFee.toSubmitTransactionResultV4())
        assertEquals(Result.NoAccount, ResultCode.NoAccount.toSubmitTransactionResultV4())
        assertTrue(ResultCode.Failed.toSubmitTransactionResultV4() is Result.UndefinedError)
        assertTrue(ResultCode.TooEarly.toSubmitTransactionResultV4() is Result.UndefinedError)
        assertTrue(ResultCode.TooLate.toSubmitTransactionResultV4() is Result.UndefinedError)
        assertTrue(ResultCode.MissingOperation.toSubmitTransactionResultV4() is Result.UndefinedError)
        assertTrue(ResultCode.BadAuth.toSubmitTransactionResultV4() is Result.UndefinedError)
        assertTrue(ResultCode.BadAuthExtra.toSubmitTransactionResultV4() is Result.UndefinedError)
        assertTrue(ResultCode.InternalError.toSubmitTransactionResultV4() is Result.UndefinedError)
        assertTrue(null.toSubmitTransactionResultV4() is Result.UndefinedError)
    }
}
