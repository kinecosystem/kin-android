package org.kin.sdk.base.network.api.rest

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
