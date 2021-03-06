package org.kin.sdk.base.network.api

import org.kin.sdk.base.stellar.models.KinTransaction

fun KinTransaction.ResultCode?.toSubmitTransactionResultV4(): KinTransactionApiV4.SubmitTransactionResponse.Result {
    return when (this) {
        KinTransaction.ResultCode.Success -> KinTransactionApiV4.SubmitTransactionResponse.Result.Ok
        KinTransaction.ResultCode.BadSequenceNumber -> KinTransactionApiV4.SubmitTransactionResponse.Result.BadSequenceNumber
        KinTransaction.ResultCode.InsufficientBalance -> KinTransactionApiV4.SubmitTransactionResponse.Result.InsufficientBalance
        KinTransaction.ResultCode.InsufficientFee -> KinTransactionApiV4.SubmitTransactionResponse.Result.InsufficientFee
        KinTransaction.ResultCode.NoAccount -> KinTransactionApiV4.SubmitTransactionResponse.Result.NoAccount
        KinTransaction.ResultCode.Failed,
        KinTransaction.ResultCode.TooEarly,
        KinTransaction.ResultCode.TooLate,
        KinTransaction.ResultCode.MissingOperation,
        KinTransaction.ResultCode.BadAuth,
        KinTransaction.ResultCode.BadAuthExtra,
        KinTransaction.ResultCode.InternalError,
        null -> KinTransactionApiV4.SubmitTransactionResponse.Result.UndefinedError(Exception("Transaction ResultCode: $this"))
    }
}
