package org.kin.sdk.base.network.api.horizon

import org.kin.sdk.base.network.api.KinAccountCreationApi

interface KinFriendBotApi {
    fun createAccount(
        request: KinAccountCreationApi.CreateAccountRequest,
        onCompleted: (KinAccountCreationApi.CreateAccountResponse) -> Unit
    )

    fun fundAccount(
        request: KinAccountCreationApi.CreateAccountRequest,
        onCompleted: (KinAccountCreationApi.CreateAccountResponse) -> Unit
    )
}
