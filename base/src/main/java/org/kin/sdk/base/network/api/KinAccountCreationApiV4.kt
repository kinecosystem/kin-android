package org.kin.sdk.base.network.api

import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.solana.Transaction

interface KinAccountCreationApiV4 {
    data class CreateAccountRequest(val transaction: Transaction)

    data class CreateAccountResponse(val result: Result, val account: KinAccount? = null) {
        sealed class Result(val value: Int) {
            object UpgradeRequiredError : Result(-3)
            data class TransientFailure(val error: Throwable) : Result(-2)
            data class UndefinedError(val error: Throwable) : Result(-1)
            object Ok : Result(0)
            object Exists : Result(1)
            object PayerRequired : Result(2)
            object BadNonce : Result(3)
        }
    }

    fun createAccount(
        request: CreateAccountRequest,
        onCompleted: (CreateAccountResponse) -> Unit
    )
}
