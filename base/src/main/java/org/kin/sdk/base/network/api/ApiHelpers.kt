package org.kin.sdk.base.network.api

import org.kin.sdk.base.stellar.models.KinTransaction

fun KinTransaction.ResultCode?.toSubmitTransactionResult(): KinTransactionApi.SubmitTransactionResponse.Result {
    return when (this) {
        KinTransaction.ResultCode.Success -> KinTransactionApi.SubmitTransactionResponse.Result.Ok
        KinTransaction.ResultCode.BadSequenceNumber -> KinTransactionApi.SubmitTransactionResponse.Result.BadSequenceNumber
        KinTransaction.ResultCode.InsufficientBalance -> KinTransactionApi.SubmitTransactionResponse.Result.InsufficientBalance
        KinTransaction.ResultCode.InsufficientFee -> KinTransactionApi.SubmitTransactionResponse.Result.InsufficientFee
        KinTransaction.ResultCode.NoAccount -> KinTransactionApi.SubmitTransactionResponse.Result.NoAccount
        KinTransaction.ResultCode.Failed,
        KinTransaction.ResultCode.TooEarly,
        KinTransaction.ResultCode.TooLate,
        KinTransaction.ResultCode.MissingOperation,
        KinTransaction.ResultCode.BadAuth,
        KinTransaction.ResultCode.BadAuthExtra,
        KinTransaction.ResultCode.InternalError,
        null -> KinTransactionApi.SubmitTransactionResponse.Result.UndefinedError(Exception("Transaction ResultCode: $this"))
    }
}
