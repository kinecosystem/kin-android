package org.kin.sdk.base.network.api

interface KinTransactionWhitelistingApi {
    val isWhitelistingAvailable: Boolean

    data class WhitelistTransactionRequest(val base64EncodedTransactionEnvelopeBytes: String)

    data class WhitelistTransactionResponse(
        val result: Result,
        val base64EncodedWhitelistedTransactionEnvelopeBytes: String
    ) {
        sealed class Result(val value: Int) {
            object UpgradeRequiredError : Result(-3)
            data class TransientFailure(val error: Throwable) : Result(-2)
            data class UndefinedError(val error: Throwable) : Result(-1)
            object Ok : Result(0)
            object WhitelistingDisabled : Result(1)
            object FailedToWhitelist : Result(2)
        }
    }

    fun whitelistTransaction(
        request: WhitelistTransactionRequest,
        onCompleted: (WhitelistTransactionResponse) -> Unit
    )
}
