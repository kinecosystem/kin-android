package org.kin.sdk.base.network.services

import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.network.api.KinAccountCreationApi
import org.kin.sdk.base.network.api.horizon.KinFriendBotApi
import org.kin.sdk.base.tools.NetworkOperationsHandler
import org.kin.sdk.base.tools.Promise
import org.kin.sdk.base.tools.queueWork

class KinTestServiceImpl(
    private val networkOperationsHandler: NetworkOperationsHandler,
    private val friendBotApi: KinFriendBotApi
) : KinTestService {
    override fun fundAccount(accountId: KinAccount.Id): Promise<KinAccount> {
        return networkOperationsHandler.queueWork { respond ->
            friendBotApi.fundAccount(KinAccountCreationApi.CreateAccountRequest(accountId)) { response ->
                val error: Exception? = when (response.result) {
                    KinAccountCreationApi.CreateAccountResponse.Result.Ok,
                    KinAccountCreationApi.CreateAccountResponse.Result.Exists -> {
                        if (response.account != null) {
                            respond(response.account); null
                        } else KinService.FatalError.IllegalResponse
                    }
                    is KinAccountCreationApi.CreateAccountResponse.Result.TransientFailure -> KinService.FatalError.TransientFailure(
                        response.result.error
                    )
                    is KinAccountCreationApi.CreateAccountResponse.Result.UndefinedError -> KinService.FatalError.UnexpectedServiceError(
                        response.result.error
                    )
                    KinAccountCreationApi.CreateAccountResponse.Result.Unavailable -> KinService.FatalError.PermanentlyUnavailable
                    KinAccountCreationApi.CreateAccountResponse.Result.UpgradeRequiredError -> KinService.FatalError.SDKUpgradeRequired
                }
                error?.let { respond(it) }
            }
        }
    }
}
