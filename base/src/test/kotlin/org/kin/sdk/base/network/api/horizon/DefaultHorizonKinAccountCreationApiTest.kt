package org.kin.sdk.base.network.api.horizon

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.kin.sdk.base.network.api.KinAccountCreationApi
import org.kin.sdk.base.network.api.KinAccountCreationApi.CreateAccountRequest
import org.kin.sdk.base.network.api.KinAccountCreationApi.CreateAccountResponse
import org.kin.sdk.base.network.api.KinAccountCreationApi.CreateAccountResponse.Result
import org.kin.sdk.base.stellar.models.ApiConfig
import org.kin.sdk.base.tools.TestUtils
import kotlin.test.assertEquals

class DefaultHorizonKinAccountCreationApiTest {

    companion object {
        val account = TestUtils.newKinAccount()
    }

    lateinit var sut: KinAccountCreationApi

    lateinit var friendBotApi: KinFriendBotApi

    @Test
    fun createAccount_success_testnet() {

        friendBotApi = mock {}

        doAnswer {
            val callback: (CreateAccountResponse) -> Unit = it.getArgument(0)
            callback(CreateAccountResponse(Result.Ok, account))
        }.whenever(friendBotApi).createAccount(eq(CreateAccountRequest(account.id)), any())

        sut = DefaultHorizonKinAccountCreationApi(ApiConfig.TestNetHorizon, friendBotApi)

        sut.createAccount(CreateAccountRequest(TestUtils.newKinAccount().id)) {
            assertEquals(it.result, Result.Ok)
        }
    }

    @Test
    fun createAccount_disabled_for_mainnet() {

        friendBotApi = mock {}

        doAnswer {
            val callback: (CreateAccountResponse) -> Unit = it.getArgument(0)
            callback(CreateAccountResponse(Result.Ok, account))
        }.whenever(friendBotApi).createAccount(eq(CreateAccountRequest(account.id)), any())

        sut = DefaultHorizonKinAccountCreationApi(ApiConfig.MainNetHorizon, friendBotApi)

        sut.createAccount(CreateAccountRequest(TestUtils.newKinAccount().id)) {
            assertEquals(it.result, Result.Unavailable)
        }
    }
}
