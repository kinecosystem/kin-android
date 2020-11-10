package org.kin.sdk.base.network.api

import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount

interface KinAccountApiV4 {

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

    data class ResolveTokenAccountsRequest(val accountId: KinAccount.Id)

    data class ResolveTokenAccountsResponse(val result: Result, val accounts: List<Key.PublicKey>) {
        sealed class Result(val value: Int) {
            object UpgradeRequiredError : Result(-3)
            data class TransientFailure(val error: Throwable) : Result(-2)
            data class UndefinedError(val error: Throwable) : Result(-1)
            object Ok : Result(0)
        }
    }

    fun getAccount(
        request: GetAccountRequest,
        onCompleted: (GetAccountResponse) -> Unit
    )

    fun resolveTokenAcounts(
        request: ResolveTokenAccountsRequest,
        onCompleted: (ResolveTokenAccountsResponse) -> Unit
    )
}
