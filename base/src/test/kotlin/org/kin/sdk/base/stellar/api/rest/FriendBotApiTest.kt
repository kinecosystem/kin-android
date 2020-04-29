package org.kin.sdk.base.stellar.api.rest

import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.network.api.FriendBotApi
import org.kin.sdk.base.network.api.KinAccountCreationApi.CreateAccountRequest
import org.kin.sdk.base.network.api.KinAccountCreationApi.CreateAccountResponse
import org.kin.sdk.base.tools.TestUtils
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FriendBotApiTest {

    companion object {
        val account =
            TestUtils.fromAccountId("GAMAJHQ6NNTSVZWESSI23IZOLKRF4GCA2MNU3GG37U4SMEX3EOSCYH22")
    }

    lateinit var sut: FriendBotApi

    lateinit var okHttpClient: OkHttpClient
    lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        okHttpClient = OkHttpClient()

        mockWebServer = MockWebServer()
            .apply { start() }

        sut = FriendBotApi(
            okHttpClient,
            mockWebServer.url("/").url().toString()
        )
    }

    @Test
    fun createAccount_success() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    "{\n" +
                            "\"_links\": {\n" +
                            "\"transaction\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions/bd5bf79887c0302f1043794ef5c2754b06d06fa0a4833d81066e5985c2a0b80f\"\n" +
                            "}\n" +
                            "},\n" +
                            "\"hash\": \"bd5bf79887c0302f1043794ef5c2754b06d06fa0a4833d81066e5985c2a0b80f\",\n" +
                            "\"ledger\": 5819838,\n" +
                            "\"envelope_xdr\": \"AAAAACpCXwaqhTNr8kig8asLD3dUIXDo5PKy5wP1byRrTwDWAAAAZAAAAdUAAJDFAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAGASeHmtnKubElJGtoy5aol4YQNMbTZjb/TkmEvsjpCwAAAAAO5rKAAAAAAAAAAABa08A1gAAAEANXkWnpnvCBRmX5wCJ4PflKpJROUtb+OntretTaMToNdUukmjKc2Yb/DsEWQ1e3R2ZlSMkazX0P4nv6p00YbYL\",\n" +
                            "\"result_xdr\": \"AAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAA=\",\n" +
                            "\"result_meta_xdr\": \"AAAAAAAAAAEAAAADAAAAAABYzb4AAAAAAAAAABgEnh5rZyrmxJSRraMuWqJeGEDTG02Y2/05JhL7I6QsAAAAADuaygAAWM2+AAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAwBYzb4AAAAAAAAAACpCXwaqhTNr8kig8asLD3dUIXDo5PKy5wP1byRrTwDWDdz+O6shf+QAAAHVAACQxQAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAQBYzb4AAAAAAAAAACpCXwaqhTNr8kig8asLD3dUIXDo5PKy5wP1byRrTwDWDdz+O2+GteQAAAHVAACQxQAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAA\"\n" +
                            "}"
                )
        )

        sut.createAccount(CreateAccountRequest(account.id)) {
            assertEquals(CreateAccountResponse.Result.Ok, it.result)
            assertNotNull(it.account)
        }
    }

    @Test
    fun createAccount_failure() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody(
                    ""
                )
        )

        sut.createAccount(CreateAccountRequest(account.id)) {
            assertTrue(it.result is CreateAccountResponse.Result.TransientFailure)
            assertNull(it.account)
        }
    }

    @Test
    fun fundAccount_success() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    "{\n" +
                            "\"_links\": {\n" +
                            "\"transaction\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions/1850d385091ef563c1fd8439c753d619be09b86e7d8782ac11f8b5859658aabb\"\n" +
                            "}\n" +
                            "},\n" +
                            "\"hash\": \"1850d385091ef563c1fd8439c753d619be09b86e7d8782ac11f8b5859658aabb\",\n" +
                            "\"ledger\": 5819892,\n" +
                            "\"envelope_xdr\": \"AAAAACpCXwaqhTNr8kig8asLD3dUIXDo5PKy5wP1byRrTwDWAAAAZAAAAdUAAJDGAAAAAAAAAAAAAAABAAAAAAAAAAEAAAAAGASeHmtnKubElJGtoy5aol4YQNMbTZjb/TkmEvsjpCwAAAAAAAAAADuaygAAAAAAAAAAAWtPANYAAABA2W0msiiWYvSk0ulBfrnGCxhFtz/OFEdrzS+hs1gG6Z7YrndRPOe8wR3qvhKCzDOCowCxz9xaRPT0ASZ4N0KYAw==\",\n" +
                            "\"result_xdr\": \"AAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=\",\n" +
                            "\"result_meta_xdr\": \"AAAAAAAAAAEAAAAEAAAAAwBYzb4AAAAAAAAAABgEnh5rZyrmxJSRraMuWqJeGEDTG02Y2/05JhL7I6QsAAAAADuaygAAWM2+AAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAQBYzfQAAAAAAAAAABgEnh5rZyrmxJSRraMuWqJeGEDTG02Y2/05JhL7I6QsAAAAAHc1lAAAWM2+AAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAwBYzfQAAAAAAAAAACpCXwaqhTNr8kig8asLD3dUIXDo5PKy5wP1byRrTwDWDdz+O2+GteQAAAHVAACQxgAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAQBYzfQAAAAAAAAAACpCXwaqhTNr8kig8asLD3dUIXDo5PKy5wP1byRrTwDWDdz+OzPr6+QAAAHVAACQxgAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAA\"\n" +
                            "}"
                )
        )

        sut.fundAccount(CreateAccountRequest(account.id)) {
            assertEquals(CreateAccountResponse.Result.Ok, it.result)
            assertNotNull(it.account)
            assertEquals(KinBalance(KinAmount(20000)), it.account!!.balance)
        }
    }

    @Test
    fun fundAccount_failure() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody(
                    ""
                )
        )

        sut.fundAccount(CreateAccountRequest(account.id)) {
            assertTrue(it.result is CreateAccountResponse.Result.TransientFailure)
            assertNull(it.account)
        }
    }
}
