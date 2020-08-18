package org.kin.sdk.base.network.api.horizon

import org.kin.sdk.base.network.api.KinAccountCreationApi
import org.kin.sdk.base.stellar.models.ApiConfig

class DefaultHorizonKinAccountCreationApi(
    private val environment: ApiConfig,
    private val friendBotApi: KinFriendBotApi
) : KinAccountCreationApi {
    override fun createAccount(
        request: KinAccountCreationApi.CreateAccountRequest,
        onCompleted: (KinAccountCreationApi.CreateAccountResponse) -> Unit
    ) {
        when (environment) {
            ApiConfig.TestNetHorizon -> friendBotApi.createAccount(
                request,
                onCompleted
            )
            else -> {

                /**
                 * Developers are expected to call their back-end's to register
                 * this address with the main-net Kin Blockchain
                 */

                onCompleted(
                    KinAccountCreationApi.CreateAccountResponse(
                        KinAccountCreationApi.CreateAccountResponse.Result.Unavailable
                    )
                )
            }
        }
    }
}
