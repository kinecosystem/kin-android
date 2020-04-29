package org.kin.sdk.base.network.api

import org.kin.sdk.base.models.KinAccount

/**
 * An API for the SDK to delegate [KinAccount] registration
 * with the Kin Blockchain to developers.
 */
interface KinAccountCreationApi {

    data class CreateAccountRequest(val accountId: KinAccount.Id)

    data class CreateAccountResponse(val result: Result, val account: KinAccount? = null) {
        sealed class Result(val value: Int) {
            object UpgradeRequiredError : Result(-3)
            data class TransientFailure(val error: Throwable) : Result(-2)
            data class UndefinedError(val error: Throwable) : Result(-1)
            object Ok : Result(0)
            object Exists : Result(1)
            object Unavailable : Result(2)
        }
    }

    /**
     * Developers are expected to call their back-end's to register
     * this address with the main-net Kin Blockchain.
     *
     * Note: [FriendBotApi] via the [DefaultAccountCreationAPI]
     *       can be used for test-net
     */
    fun createAccount(
        request: CreateAccountRequest,
        onCompleted: (CreateAccountResponse) -> Unit
    )
}
