package org.kin.sdk.base.network.api

import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.tools.Observer

interface KinAccountApi {

    data class GetAccountRequest(val accountId: KinAccount.Id)

    data class GetAccountResponse(val result: Result, val account: KinAccount? = null) {
        sealed class Result(val value: Int) {
            object UpgradeRequiredError : Result(-3)
            data class TransientFailure(val error: Throwable) : Result(-2)
            data class UndefinedError(val error: Throwable) : Result(-1)
            object Ok : Result(0)
            object NotFound : Result(1)
        }
    }

    fun getAccount(
        request: GetAccountRequest,
        onCompleted: (GetAccountResponse) -> Unit
    )
}

