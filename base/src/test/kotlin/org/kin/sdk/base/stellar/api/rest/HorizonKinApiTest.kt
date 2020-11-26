package org.kin.sdk.base.stellar.api.rest

import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.TransactionHash
import org.kin.sdk.base.models.asKinPayments
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.network.api.KinAccountApi
import org.kin.sdk.base.network.api.KinTransactionApi
import org.kin.sdk.base.network.api.horizon.HorizonKinApi
import org.kin.sdk.base.stellar.models.ApiConfig
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.tools.DisposeBag
import org.kin.sdk.base.tools.TestUtils
import org.kin.sdk.base.tools.chunkForStream
import org.kin.sdk.base.tools.test
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.KinServer
import org.kin.stellarfork.codec.Base64
import org.kin.stellarfork.codec.Hex
import org.kin.stellarfork.responses.HttpResponseException
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HorizonKinApiTest {

    lateinit var sut: HorizonKinApi

    lateinit var server: KinServer

    lateinit var mockWebServer: MockWebServer

    lateinit var account: KinAccount

    @Before
    fun setUp() {
        account = TestUtils.newKinAccount()

        mockWebServer = MockWebServer()
            .apply { start() }

        sut = HorizonKinApi(
            ApiConfig.CustomConfig(
                mockWebServer.url("/").url().toString(),
                NetworkEnvironment.KinStellarTestNetKin3,
                5555
            ), OkHttpClient().newBuilder()
                .readTimeout(Duration.ofSeconds(2))
                .build()
        )
    }

    @Test
    fun getAccount() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    "{\n" +
                            "  \"_links\": {\n" +
                            "    \"self\": {\n" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ\"\n" +
                            "    },\n" +
                            "    \"transactions\": {\n" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ/transactions{?cursor,limit,order}\",\n" +
                            "      \"templated\": true\n" +
                            "    },\n" +
                            "    \"operations\": {\n" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ/operations{?cursor,limit,order}\",\n" +
                            "      \"templated\": true\n" +
                            "    },\n" +
                            "    \"payments\": {\n" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ/payments{?cursor,limit,order}\",\n" +
                            "      \"templated\": true\n" +
                            "    },\n" +
                            "    \"effects\": {\n" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ/effects{?cursor,limit,order}\",\n" +
                            "      \"templated\": true\n" +
                            "    },\n" +
                            "    \"offers\": {\n" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ/offers{?cursor,limit,order}\",\n" +
                            "      \"templated\": true\n" +
                            "    },\n" +
                            "    \"trades\": {\n" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ/trades{?cursor,limit,order}\",\n" +
                            "      \"templated\": true\n" +
                            "    },\n" +
                            "    \"data\": {\n" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ/data/{key}\",\n" +
                            "      \"templated\": true\n" +
                            "    }\n" +
                            "  },\n" +
                            "  \"id\": \"GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ\",\n" +
                            "  \"paging_token\": \"\",\n" +
                            "  \"account_id\": \"GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ\",\n" +
                            "  \"sequence\": \"24914078786912256\",\n" +
                            "  \"subentry_count\": 0,\n" +
                            "  \"last_modified_ledger\": 5800761,\n" +
                            "  \"thresholds\": {\n" +
                            "    \"low_threshold\": 0,\n" +
                            "    \"med_threshold\": 0,\n" +
                            "    \"high_threshold\": 0\n" +
                            "  },\n" +
                            "  \"flags\": {\n" +
                            "    \"auth_required\": false,\n" +
                            "    \"auth_revocable\": false,\n" +
                            "    \"auth_immutable\": false\n" +
                            "  },\n" +
                            "  \"balances\": [\n" +
                            "    {\n" +
                            "      \"balance\": \"0.00000\",\n" +
                            "      \"buying_liabilities\": \"0.00000\",\n" +
                            "      \"selling_liabilities\": \"0.00000\",\n" +
                            "      \"asset_type\": \"native\"\n" +
                            "    }\n" +
                            "  ],\n" +
                            "  \"signers\": [\n" +
                            "    {\n" +
                            "      \"public_key\": \"GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ\",\n" +
                            "      \"weight\": 1,\n" +
                            "      \"key\": \"GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ\",\n" +
                            "      \"type\": \"ed25519_public_key\"\n" +
                            "    }\n" +
                            "  ],\n" +
                            "  \"data\": {}\n" +
                            "}"
                )
        )

        val account =
            KinAccount(
                KeyPair.fromAccountId("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ")
                    .asPublicKey(),
                status = KinAccount.Status.Registered(
                    sequence = 24914078786912256
                )
            )

        sut.getAccount(
            KinAccountApi.GetAccountRequest(account.id)
        ) {
            assertEquals(
                KinAccountApi.GetAccountResponse(
                    KinAccountApi.GetAccountResponse.Result.Ok,
                    account
                ),
                it
            )
        }
    }

    @Test
    fun getAccount_invalid() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setBody(
                    "{\n" +
                            "\"type\": \"https://stellar.org/horizon-errors/bad_request\",\n" +
                            "\"title\": \"Bad Request\",\n" +
                            "\"status\": 400,\n" +
                            "\"detail\": \"The request you sent was invalid in some way\",\n" +
                            "\"extras\": {\n" +
                            "\"invalid_field\": \"account_id\",\n" +
                            "\"reason\": \"invalid address\"\n" +
                            "}\n" +
                            "}"
                )
        )

        sut.getAccount(
            KinAccountApi.GetAccountRequest(account.id)
        ) {
            assertTrue {
                (it.result as KinAccountApi.GetAccountResponse.Result.UndefinedError).error is HttpResponseException
            }
        }
    }

    @Test
    fun getAccount_not_found() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody(
                    "{\n" +
                            "\"type\": \"https://stellar.org/horizon-errors/not_found\",\n" +
                            "\"title\": \"Resource Missing\",\n" +
                            "\"status\": 404,\n" +
                            "\"detail\": \"The resource at the url requested was not found.  This is usually occurs for one of two reasons:  The url requested is not valid, or no data in our database could be found with the parameters provided.\"\n" +
                            "}"
                )
        )

        sut.getAccount(
            KinAccountApi.GetAccountRequest(account.id)
        ) {
            assertEquals(
                KinAccountApi.GetAccountResponse(KinAccountApi.GetAccountResponse.Result.NotFound),
                it
            )
        }
    }

    @Test
    fun getAccount_server_error() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("")
        )

        sut.getAccount(
            KinAccountApi.GetAccountRequest(account.id)
        ) {
            assertTrue {
                (it.result as KinAccountApi.GetAccountResponse.Result.TransientFailure).error is HttpResponseException
            }
        }
    }

    @Test
    fun getAccount_timeout_transientFailure() {
        sut.getAccount(
            KinAccountApi.GetAccountRequest(account.id)
        ) {
            assertTrue {
                it.result is KinAccountApi.GetAccountResponse.Result.TransientFailure
            }
        }
    }

    @Test
    fun getAccount_tooManyRequests_transientFailure() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(429)
                .setBody("")
        )

        sut.getAccount(
            KinAccountApi.GetAccountRequest(account.id)
        ) {
            assertTrue {
                it.result is KinAccountApi.GetAccountResponse.Result.TransientFailure
            }
        }
    }

    @Test
    fun getAccount_unknown() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    "invalid_body"
                )
        )

        sut.getAccount(
            KinAccountApi.GetAccountRequest(account.id)
        ) {
            assertTrue {
                it.result is KinAccountApi.GetAccountResponse.Result.UndefinedError
            }
        }
    }


    @Test
    fun getAccount_serverGone_upgradeRequired() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(410)
        )

        val account =
            KinAccount(
                KeyPair.fromAccountId("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ")
                    .asPublicKey(),
                status = KinAccount.Status.Registered(
                    sequence = 24914078786912256
                )
            )

        sut.getAccount(
            KinAccountApi.GetAccountRequest(account.id)
        ) {
            assertEquals(
                KinAccountApi.GetAccountResponse(
                    KinAccountApi.GetAccountResponse.Result.UpgradeRequiredError
                ),
                it
            )
        }
    }

    @Test
    fun submitTransaction_success() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    "{\n" +
                            "  \"_links\": {\n" +
                            "    \"transaction\": {\n" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/transactions/e96d5f659320e8ec3aed5e24b8c291e681471765a61d759531a9c87c2649d365\"\n" +
                            "    }\n" +
                            "  },\n" +
                            "  \"hash\": \"e96d5f659320e8ec3aed5e24b8c291e681471765a61d759531a9c87c2649d365\",\n" +
                            "  \"ledger\": 5964517,\n" +
                            "  \"envelope_xdr\": \"AAAAAGshW4r7m/oTwQrZll7dOpku++OwSkIyJ4aD9fQrpd9EAAAAZABbAiYAAAAEAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAAAAAAAAAZLVAAAAAAAAAAAErpd9EAAAAQGacLpTEZ73cFUq1t26rURlQqptNs0I+ihiuA3EyfPAl8Da7jMGrwSPm1oRbmelCKKrbFjD4+WARaEx1PGpT4QU=\",\n" +
                            "  \"result_xdr\": \"AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=\",\n" +
                            "  \"result_meta_xdr\": \"AAAAAAAAAAEAAAAEAAAAAwBbAuUAAAAAAAAAAGshW4r7m/oTwQrZll7dOpku++OwSkIyJ4aD9fQrpd9EAAAAADr1/PAAWwImAAAABAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAQBbAuUAAAAAAAAAAGshW4r7m/oTwQrZll7dOpku++OwSkIyJ4aD9fQrpd9EAAAAADqRR7AAWwImAAAABAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAwBbAjsAAAAAAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAADwqN5QAWdv4AAAAAwAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAQBbAuUAAAAAAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAADyO7NQAWdv4AAAAAwAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAA\"\n" +
                            "}"
                )
        )

        val expected =
            TestUtils.kinTransactionFromXdr(
                "AAAAAGshW4r7m/oTwQrZll7dOpku++OwSkIyJ4aD9fQrpd9EAAAAZABbAiYAAAAEAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAAAAAAAAAZLVAAAAAAAAAAAErpd9EAAAAQGacLpTEZ73cFUq1t26rURlQqptNs0I+ihiuA3EyfPAl8Da7jMGrwSPm1oRbmelCKKrbFjD4+WARaEx1PGpT4QU=",
                KinTransaction.RecordType.Acknowledged(
                    1586995974811,
                    Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
                )
            )

        sut.submitTransaction(KinTransactionApi.SubmitTransactionRequest(expected.bytesValue)) {
            assertEquals(KinTransactionApi.SubmitTransactionResponse.Result.Ok, it.result)
            assertEquals(expected.paymentOperations, it.transaction?.paymentOperations)
        }
    }

    @Test
    fun submitTransaction_internalServerError() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody(
                    ""
                )
        )

        sut.submitTransaction(
            KinTransactionApi.SubmitTransactionRequest(
                Base64().decode("AAAAAGshW4r7m/oTwQrZll7dOpku++OwSkIyJ4aD9fQrpd9EAAAAZABbAiYAAAAEAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAAAAAAAAAZLVAAAAAAAAAAAErpd9EAAAAQGacLpTEZ73cFUq1t26rURlQqptNs0I+ihiuA3EyfPAl8Da7jMGrwSPm1oRbmelCKKrbFjD4+WARaEx1PGpT4QU=")!!
            )
        ) {
            assertTrue(it.result is KinTransactionApi.SubmitTransactionResponse.Result.TransientFailure)
            assertNull(it.transaction)
        }
    }

    @Test
    fun submitTransaction_timeout() {
        sut.submitTransaction(
            KinTransactionApi.SubmitTransactionRequest(
                Base64().decode("AAAAAGshW4r7m/oTwQrZll7dOpku++OwSkIyJ4aD9fQrpd9EAAAAZABbAiYAAAAEAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAAAAAAAAAZLVAAAAAAAAAAAErpd9EAAAAQGacLpTEZ73cFUq1t26rURlQqptNs0I+ihiuA3EyfPAl8Da7jMGrwSPm1oRbmelCKKrbFjD4+WARaEx1PGpT4QU=")!!
            )
        ) {
            assertTrue(it.result is KinTransactionApi.SubmitTransactionResponse.Result.TransientFailure)
            assertNull(it.transaction)
        }
    }

    @Test
    fun submitTransaction_tooManyRequests() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(429)
                .setBody(
                    ""
                )
        )

        sut.submitTransaction(
            KinTransactionApi.SubmitTransactionRequest(
                Base64().decode("AAAAAGshW4r7m/oTwQrZll7dOpku++OwSkIyJ4aD9fQrpd9EAAAAZABbAiYAAAAEAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAAAAAAAAAZLVAAAAAAAAAAAErpd9EAAAAQGacLpTEZ73cFUq1t26rURlQqptNs0I+ihiuA3EyfPAl8Da7jMGrwSPm1oRbmelCKKrbFjD4+WARaEx1PGpT4QU=")!!
            )
        ) {
            assertTrue(it.result is KinTransactionApi.SubmitTransactionResponse.Result.TransientFailure)
            assertNull(it.transaction)
        }
    }

    @Test
    fun submitTransaction_serverGone() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(410)
                .setBody(
                    ""
                )
        )

        sut.submitTransaction(
            KinTransactionApi.SubmitTransactionRequest(
                Base64().decode("AAAAAGshW4r7m/oTwQrZll7dOpku++OwSkIyJ4aD9fQrpd9EAAAAZABbAiYAAAAEAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAAAAAAAAAZLVAAAAAAAAAAAErpd9EAAAAQGacLpTEZ73cFUq1t26rURlQqptNs0I+ihiuA3EyfPAl8Da7jMGrwSPm1oRbmelCKKrbFjD4+WARaEx1PGpT4QU=")!!
            )
        ) {
            assertTrue(it.result is KinTransactionApi.SubmitTransactionResponse.Result.UpgradeRequiredError)
            assertNull(it.transaction)
        }
    }

    @Test
    fun submitTransaction_undefinedError() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(480)
                .setBody(
                    ""
                )
        )

        sut.submitTransaction(
            KinTransactionApi.SubmitTransactionRequest(
                Base64().decode("AAAAAGshW4r7m/oTwQrZll7dOpku++OwSkIyJ4aD9fQrpd9EAAAAZABbAiYAAAAEAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAAAAAAAAAZLVAAAAAAAAAAAErpd9EAAAAQGacLpTEZ73cFUq1t26rURlQqptNs0I+ihiuA3EyfPAl8Da7jMGrwSPm1oRbmelCKKrbFjD4+WARaEx1PGpT4QU=")!!
            )
        ) {
            assertTrue(it.result is KinTransactionApi.SubmitTransactionResponse.Result.UndefinedError)
            assertNull(it.transaction)
        }
    }

    @Test
    fun submitTransaction_undefinedError2() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    "invalid_body"
                )
        )

        sut.submitTransaction(
            KinTransactionApi.SubmitTransactionRequest(
                Base64().decode("AAAAAGshW4r7m/oTwQrZll7dOpku++OwSkIyJ4aD9fQrpd9EAAAAZABbAiYAAAAEAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAAAAAAAAAZLVAAAAAAAAAAAErpd9EAAAAQGacLpTEZ73cFUq1t26rURlQqptNs0I+ihiuA3EyfPAl8Da7jMGrwSPm1oRbmelCKKrbFjD4+WARaEx1PGpT4QU=")!!
            )
        ) {
            assertTrue(it.result is KinTransactionApi.SubmitTransactionResponse.Result.UndefinedError)
            assertNull(it.transaction)
        }
    }

    @Test
    fun getTransaction_success() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    "{\n" +
                            "\"_links\": {\n" +
                            "\"self\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions/91484c283c10bdc3fe45e3295e2df3993e03343d250d54b98039fb614139657b\"\n" +
                            "},\n" +
                            "\"account\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GDS3RPC4HBRWJXXS6HPRV4K56SOGSTLP6YX2RZ4YI6RQ57PJIYUVXEIL\"\n" +
                            "},\n" +
                            "\"ledger\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/ledgers/5944808\"\n" +
                            "},\n" +
                            "\"operations\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions/91484c283c10bdc3fe45e3295e2df3993e03343d250d54b98039fb614139657b/operations{?cursor,limit,order}\",\n" +
                            "\"templated\": true\n" +
                            "},\n" +
                            "\"effects\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions/91484c283c10bdc3fe45e3295e2df3993e03343d250d54b98039fb614139657b/effects{?cursor,limit,order}\",\n" +
                            "\"templated\": true\n" +
                            "},\n" +
                            "\"precedes\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions?order=asc&cursor=25532755941003264\"\n" +
                            "},\n" +
                            "\"succeeds\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions?order=desc&cursor=25532755941003264\"\n" +
                            "}\n" +
                            "},\n" +
                            "\"id\": \"91484c283c10bdc3fe45e3295e2df3993e03343d250d54b98039fb614139657b\",\n" +
                            "\"paging_token\": \"25532755941003264\",\n" +
                            "\"hash\": \"91484c283c10bdc3fe45e3295e2df3993e03343d250d54b98039fb614139657b\",\n" +
                            "\"ledger\": 5944808,\n" +
                            "\"created_at\": \"2020-04-14T17:58:04Z\",\n" +
                            "\"source_account\": \"GDS3RPC4HBRWJXXS6HPRV4K56SOGSTLP6YX2RZ4YI6RQ57PJIYUVXEIL\",\n" +
                            "\"source_account_sequence\": \"25293131125620739\",\n" +
                            "\"fee_paid\": 100,\n" +
                            "\"operation_count\": 1,\n" +
                            "\"envelope_xdr\": \"AAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAZABZ2/gAAAADAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAADJhY8ODfklc4sHXp+xFywL6OxdHaWwljtepeFe9KOUOAAAAAAAAAAAAIAsgAAAAAAAAAAHpRilbAAAAQN1YkzWbQdhatwAHZW4dlfVo61cbHfFFY5I6UOcnrwgMZ5bN+iaCMi6V8tEjxnKjP9BjLGJnbvg7d9iYCcQiWg4=\",\n" +
                            "\"result_xdr\": \"AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=\",\n" +
                            "\"result_meta_xdr\": \"AAAAAAAAAAEAAAAEAAAAAwBakvkAAAAAAAAAADJhY8ODfklc4sHXp+xFywL6OxdHaWwljtepeFe9KOUOAAAAADovnoQAWY8TAAAABwAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAQBategAAAAAAAAAADJhY8ODfklc4sHXp+xFywL6OxdHaWwljtepeFe9KOUOAAAAADpPqaQAWY8TAAAABwAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAwBategAAAAAAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAADuiafQAWdv4AAAAAwAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAQBategAAAAAAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAADuCXtQAWdv4AAAAAwAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAA\",\n" +
                            "\"fee_meta_xdr\": \"AAAAAgAAAAMAWpL5AAAAAAAAAADluLxcOGNk3vLx3xrxXfScaU1v9i+o55hHow796UYpWwAAAAA7ompYAFnb+AAAAAIAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAEAWrXoAAAAAAAAAADluLxcOGNk3vLx3xrxXfScaU1v9i+o55hHow796UYpWwAAAAA7omn0AFnb+AAAAAMAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAA==\",\n" +
                            "\"memo_type\": \"none\",\n" +
                            "\"signatures\": [\n" +
                            "\"3ViTNZtB2Fq3AAdlbh2V9WjrVxsd8UVjkjpQ5yevCAxnls36JoIyLpXy0SPGcqM/0GMsYmdu+Dt32JgJxCJaDg==\"\n" +
                            "],\n" +
                            "\"valid_after\": \"1970-01-01T00:00:00Z\"\n" +
                            "}"
                )
        )

        val transactionHash = TransactionHash(
            Hex.decodeHex("91484c283c10bdc3fe45e3295e2df3993e03343d250d54b98039fb614139657b")
        )

        val expected =
            TestUtils.kinTransactionFromXdr(
                "AAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAZABZ2/gAAAADAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAADJhY8ODfklc4sHXp+xFywL6OxdHaWwljtepeFe9KOUOAAAAAAAAAAAAIAsgAAAAAAAAAAHpRilbAAAAQN1YkzWbQdhatwAHZW4dlfVo61cbHfFFY5I6UOcnrwgMZ5bN+iaCMi6V8tEjxnKjP9BjLGJnbvg7d9iYCcQiWg4=",
                KinTransaction.RecordType.Historical(
                    1586887084000,
                    Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                    KinTransaction.PagingToken("25532755941003264")
                )
            )
        sut.getTransaction(KinTransactionApi.GetTransactionRequest(transactionHash)) {
            assertEquals(KinTransactionApi.GetTransactionResponse.Result.Ok, it.result)
            assertEquals(expected, it.transaction)
        }
    }

    @Test
    fun getTransaction_notFound() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody(
                    ""
                )
        )

        val transactionHash = TransactionHash(
            Hex.decodeHex("91484c283c10bdc3fe45e3295e2df3993e03343d250d54b98039fb614139657b")
        )
        sut.getTransaction(KinTransactionApi.GetTransactionRequest(transactionHash)) {
            assertEquals(KinTransactionApi.GetTransactionResponse.Result.NotFound, it.result)
            assertNull(it.transaction)
        }
    }

    @Test
    fun getTransaction_internalServerError() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody(
                    ""
                )
        )

        val transactionHash = TransactionHash(
            Hex.decodeHex("91484c283c10bdc3fe45e3295e2df3993e03343d250d54b98039fb614139657b")
        )
        sut.getTransaction(KinTransactionApi.GetTransactionRequest(transactionHash)) {
            assertTrue(it.result is KinTransactionApi.GetTransactionResponse.Result.TransientFailure)
            assertNull(it.transaction)
        }
    }

    @Test
    fun getTransaction_undefinedHttpError() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(480)
                .setBody(
                    ""
                )
        )

        val transactionHash = TransactionHash(
            Hex.decodeHex("91484c283c10bdc3fe45e3295e2df3993e03343d250d54b98039fb614139657b")
        )
        sut.getTransaction(KinTransactionApi.GetTransactionRequest(transactionHash)) {
            assertTrue(it.result is KinTransactionApi.GetTransactionResponse.Result.UndefinedError)
            assertNull(it.transaction)
        }
    }

    @Test
    fun getTransaction_timeout() {
        val transactionHash = TransactionHash(
            Hex.decodeHex("91484c283c10bdc3fe45e3295e2df3993e03343d250d54b98039fb614139657b")
        )
        sut.getTransaction(KinTransactionApi.GetTransactionRequest(transactionHash)) {
            assertTrue(it.result is KinTransactionApi.GetTransactionResponse.Result.TransientFailure)
            assertNull(it.transaction)
        }
    }

    @Test
    fun getTransaction_tooManyRequests() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(429)
                .setBody(
                    ""
                )
        )

        val transactionHash = TransactionHash(
            Hex.decodeHex("91484c283c10bdc3fe45e3295e2df3993e03343d250d54b98039fb614139657b")
        )
        sut.getTransaction(KinTransactionApi.GetTransactionRequest(transactionHash)) {
            assertTrue(it.result is KinTransactionApi.GetTransactionResponse.Result.TransientFailure)
            assertNull(it.transaction)
        }
    }

    @Test
    fun getTransaction_upgradeRequired() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(410)
                .setBody(
                    ""
                )
        )

        val transactionHash = TransactionHash(
            Hex.decodeHex("91484c283c10bdc3fe45e3295e2df3993e03343d250d54b98039fb614139657b")
        )
        sut.getTransaction(KinTransactionApi.GetTransactionRequest(transactionHash)) {
            assertTrue(it.result is KinTransactionApi.GetTransactionResponse.Result.UpgradeRequiredError)
            assertNull(it.transaction)
        }
    }

    @Test
    fun getTransaction_undefinedFailure() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    "invalid_body"
                )
        )

        val transactionHash = TransactionHash(
            Hex.decodeHex("91484c283c10bdc3fe45e3295e2df3993e03343d250d54b98039fb614139657b")
        )
        sut.getTransaction(KinTransactionApi.GetTransactionRequest(transactionHash)) {
            assertTrue(it.result is KinTransactionApi.GetTransactionResponse.Result.UndefinedError)
            assertNull(it.transaction)
        }
    }


    @Test
    fun getTransactionHistory_success() {

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    "{\n" +
                            "\"_links\": {\n" +
                            "\"self\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GBVSCW4K7ON7UE6BBLMZMXW5HKMS567DWBFEEMRHQ2B7L5BLUXPUIJMM/transactions?cursor=&limit=10&order=desc\"\n" +
                            "},\n" +
                            "\"next\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GBVSCW4K7ON7UE6BBLMZMXW5HKMS567DWBFEEMRHQ2B7L5BLUXPUIJMM/transactions?cursor=25616585112686592&limit=10&order=desc\"\n" +
                            "},\n" +
                            "\"prev\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GBVSCW4K7ON7UE6BBLMZMXW5HKMS567DWBFEEMRHQ2B7L5BLUXPUIJMM/transactions?cursor=25616675306999808&limit=10&order=asc\"\n" +
                            "}\n" +
                            "},\n" +
                            "\"_embedded\": {\n" +
                            "\"records\": [\n" +
                            "{\n" +
                            "\"_links\": {\n" +
                            "\"self\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions/97d50eb6ad59f971387b5efaefca004712ba49e2db420bc20650f4f445236dca\"\n" +
                            "},\n" +
                            "\"account\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GBVSCW4K7ON7UE6BBLMZMXW5HKMS567DWBFEEMRHQ2B7L5BLUXPUIJMM\"\n" +
                            "},\n" +
                            "\"ledger\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/ledgers/5964347\"\n" +
                            "},\n" +
                            "\"operations\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions/97d50eb6ad59f971387b5efaefca004712ba49e2db420bc20650f4f445236dca/operations{?cursor,limit,order}\",\n" +
                            "\"templated\": true\n" +
                            "},\n" +
                            "\"effects\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions/97d50eb6ad59f971387b5efaefca004712ba49e2db420bc20650f4f445236dca/effects{?cursor,limit,order}\",\n" +
                            "\"templated\": true\n" +
                            "},\n" +
                            "\"precedes\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions?order=asc&cursor=25616675306999808\"\n" +
                            "},\n" +
                            "\"succeeds\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions?order=desc&cursor=25616675306999808\"\n" +
                            "}\n" +
                            "},\n" +
                            "\"id\": \"97d50eb6ad59f971387b5efaefca004712ba49e2db420bc20650f4f445236dca\",\n" +
                            "\"paging_token\": \"25616675306999808\",\n" +
                            "\"hash\": \"97d50eb6ad59f971387b5efaefca004712ba49e2db420bc20650f4f445236dca\",\n" +
                            "\"ledger\": 5964347,\n" +
                            "\"created_at\": \"2020-04-15T23:56:25Z\",\n" +
                            "\"source_account\": \"GBVSCW4K7ON7UE6BBLMZMXW5HKMS567DWBFEEMRHQ2B7L5BLUXPUIJMM\",\n" +
                            "\"source_account_sequence\": \"25616585112682499\",\n" +
                            "\"fee_paid\": 100,\n" +
                            "\"operation_count\": 1,\n" +
                            "\"envelope_xdr\": \"AAAAAGshW4r7m/oTwQrZll7dOpku++OwSkIyJ4aD9fQrpd9EAAAAZABbAiYAAAADAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAAAAAAAAAZLVAAAAAAAAAAAErpd9EAAAAQA4bpNrxaFPsOau9PrSRiQXY72s3LVyCWq0JFhfltgGxuduq9Pqbh4T95uSnYH/3yyAX2JPdXJbabg8xj7VwmQk=\",\n" +
                            "\"result_xdr\": \"AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=\",\n" +
                            "\"result_meta_xdr\": \"AAAAAAAAAAEAAAAEAAAAAwBbAjsAAAAAAAAAAGshW4r7m/oTwQrZll7dOpku++OwSkIyJ4aD9fQrpd9EAAAAADtaspQAWwImAAAAAwAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAQBbAjsAAAAAAAAAAGshW4r7m/oTwQrZll7dOpku++OwSkIyJ4aD9fQrpd9EAAAAADr1/VQAWwImAAAAAwAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAwBbAjcAAAAAAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAADvFglQAWdv4AAAAAwAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAQBbAjsAAAAAAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAADwqN5QAWdv4AAAAAwAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAA\",\n" +
                            "\"fee_meta_xdr\": \"AAAAAgAAAAMAWwI3AAAAAAAAAABrIVuK+5v6E8EK2ZZe3TqZLvvjsEpCMieGg/X0K6XfRAAAAAA7WrL4AFsCJgAAAAIAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAEAWwI7AAAAAAAAAABrIVuK+5v6E8EK2ZZe3TqZLvvjsEpCMieGg/X0K6XfRAAAAAA7WrKUAFsCJgAAAAMAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAA==\",\n" +
                            "\"memo_type\": \"none\",\n" +
                            "\"signatures\": [\n" +
                            "\"Dhuk2vFoU+w5q70+tJGJBdjvazctXIJarQkWF+W2AbG526r0+puHhP3m5Kdgf/fLIBfYk91cltpuDzGPtXCZCQ==\"\n" +
                            "],\n" +
                            "\"valid_after\": \"1970-01-01T00:00:00Z\"\n" +
                            "},\n" +
                            "{\n" +
                            "\"_links\": {\n" +
                            "\"self\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions/3dd93cedf35ab55d81dde43ae27c5794711aeaa92bb3072a12658ea285d88fa0\"\n" +
                            "},\n" +
                            "\"account\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GBVSCW4K7ON7UE6BBLMZMXW5HKMS567DWBFEEMRHQ2B7L5BLUXPUIJMM\"\n" +
                            "},\n" +
                            "\"ledger\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/ledgers/5964343\"\n" +
                            "},\n" +
                            "\"operations\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions/3dd93cedf35ab55d81dde43ae27c5794711aeaa92bb3072a12658ea285d88fa0/operations{?cursor,limit,order}\",\n" +
                            "\"templated\": true\n" +
                            "},\n" +
                            "\"effects\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions/3dd93cedf35ab55d81dde43ae27c5794711aeaa92bb3072a12658ea285d88fa0/effects{?cursor,limit,order}\",\n" +
                            "\"templated\": true\n" +
                            "},\n" +
                            "\"precedes\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions?order=asc&cursor=25616658127130624\"\n" +
                            "},\n" +
                            "\"succeeds\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions?order=desc&cursor=25616658127130624\"\n" +
                            "}\n" +
                            "},\n" +
                            "\"id\": \"3dd93cedf35ab55d81dde43ae27c5794711aeaa92bb3072a12658ea285d88fa0\",\n" +
                            "\"paging_token\": \"25616658127130624\",\n" +
                            "\"hash\": \"3dd93cedf35ab55d81dde43ae27c5794711aeaa92bb3072a12658ea285d88fa0\",\n" +
                            "\"ledger\": 5964343,\n" +
                            "\"created_at\": \"2020-04-15T23:55:58Z\",\n" +
                            "\"source_account\": \"GBVSCW4K7ON7UE6BBLMZMXW5HKMS567DWBFEEMRHQ2B7L5BLUXPUIJMM\",\n" +
                            "\"source_account_sequence\": \"25616585112682498\",\n" +
                            "\"fee_paid\": 100,\n" +
                            "\"operation_count\": 1,\n" +
                            "\"envelope_xdr\": \"AAAAAGshW4r7m/oTwQrZll7dOpku++OwSkIyJ4aD9fQrpd9EAAAAZABbAiYAAAACAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAAAAAAAAALcbAAAAAAAAAAAErpd9EAAAAQMJPz9ZrCps174DAPTIDKVY8q46eX2wNlm13vJYZOrpIJOlApSy28oAq6gghPJV1+PJkakkkHfb6OOA8vknzwwk=\",\n" +
                            "\"result_xdr\": \"AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=\",\n" +
                            "\"result_meta_xdr\": \"AAAAAAAAAAEAAAAEAAAAAwBbAjcAAAAAAAAAAGshW4r7m/oTwQrZll7dOpku++OwSkIyJ4aD9fQrpd9EAAAAADuIebgAWwImAAAAAgAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAQBbAjcAAAAAAAAAAGshW4r7m/oTwQrZll7dOpku++OwSkIyJ4aD9fQrpd9EAAAAADtasvgAWwImAAAAAgAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAwBbAjMAAAAAAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAADuXu5QAWdv4AAAAAwAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAQBbAjcAAAAAAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAADvFglQAWdv4AAAAAwAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAA\",\n" +
                            "\"fee_meta_xdr\": \"AAAAAgAAAAMAWwIzAAAAAAAAAABrIVuK+5v6E8EK2ZZe3TqZLvvjsEpCMieGg/X0K6XfRAAAAAA7iHocAFsCJgAAAAEAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAEAWwI3AAAAAAAAAABrIVuK+5v6E8EK2ZZe3TqZLvvjsEpCMieGg/X0K6XfRAAAAAA7iHm4AFsCJgAAAAIAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAA==\",\n" +
                            "\"memo_type\": \"none\",\n" +
                            "\"signatures\": [\n" +
                            "\"wk/P1msKmzXvgMA9MgMpVjyrjp5fbA2WbXe8lhk6ukgk6UClLLbygCrqCCE8lXX48mRqSSQd9vo44Dy+SfPDCQ==\"\n" +
                            "],\n" +
                            "\"valid_after\": \"1970-01-01T00:00:00Z\"\n" +
                            "},\n" +
                            "{\n" +
                            "\"_links\": {\n" +
                            "\"self\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions/8e2a7d903b94c01d9fba355a1393164979e99ebdd0bbd5961dec343adb8b6ad9\"\n" +
                            "},\n" +
                            "\"account\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GBVSCW4K7ON7UE6BBLMZMXW5HKMS567DWBFEEMRHQ2B7L5BLUXPUIJMM\"\n" +
                            "},\n" +
                            "\"ledger\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/ledgers/5964339\"\n" +
                            "},\n" +
                            "\"operations\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions/8e2a7d903b94c01d9fba355a1393164979e99ebdd0bbd5961dec343adb8b6ad9/operations{?cursor,limit,order}\",\n" +
                            "\"templated\": true\n" +
                            "},\n" +
                            "\"effects\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions/8e2a7d903b94c01d9fba355a1393164979e99ebdd0bbd5961dec343adb8b6ad9/effects{?cursor,limit,order}\",\n" +
                            "\"templated\": true\n" +
                            "},\n" +
                            "\"precedes\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions?order=asc&cursor=25616640947261440\"\n" +
                            "},\n" +
                            "\"succeeds\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions?order=desc&cursor=25616640947261440\"\n" +
                            "}\n" +
                            "},\n" +
                            "\"id\": \"8e2a7d903b94c01d9fba355a1393164979e99ebdd0bbd5961dec343adb8b6ad9\",\n" +
                            "\"paging_token\": \"25616640947261440\",\n" +
                            "\"hash\": \"8e2a7d903b94c01d9fba355a1393164979e99ebdd0bbd5961dec343adb8b6ad9\",\n" +
                            "\"ledger\": 5964339,\n" +
                            "\"created_at\": \"2020-04-15T23:55:36Z\",\n" +
                            "\"source_account\": \"GBVSCW4K7ON7UE6BBLMZMXW5HKMS567DWBFEEMRHQ2B7L5BLUXPUIJMM\",\n" +
                            "\"source_account_sequence\": \"25616585112682497\",\n" +
                            "\"fee_paid\": 100,\n" +
                            "\"operation_count\": 1,\n" +
                            "\"envelope_xdr\": \"AAAAAGshW4r7m/oTwQrZll7dOpku++OwSkIyJ4aD9fQrpd9EAAAAZABbAiYAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAAAAAAAAAEk+AAAAAAAAAAAErpd9EAAAAQCgdLd60cd8y/Op1o5q1KWkojWCYaq7mvJxSSD+wxg3yvDcBGNwixrZaJwmhyMNnDvjwl1zu8Somi5iu616LFQ0=\",\n" +
                            "\"result_xdr\": \"AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=\",\n" +
                            "\"result_meta_xdr\": \"AAAAAAAAAAEAAAAEAAAAAwBbAjMAAAAAAAAAAGshW4r7m/oTwQrZll7dOpku++OwSkIyJ4aD9fQrpd9EAAAAADuayZwAWwImAAAAAQAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAQBbAjMAAAAAAAAAAGshW4r7m/oTwQrZll7dOpku++OwSkIyJ4aD9fQrpd9EAAAAADuIehwAWwImAAAAAQAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAwBa6+UAAAAAAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAADuFbBQAWdv4AAAAAwAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAQBbAjMAAAAAAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAADuXu5QAWdv4AAAAAwAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAA\",\n" +
                            "\"fee_meta_xdr\": \"AAAAAgAAAAMAWwImAAAAAAAAAABrIVuK+5v6E8EK2ZZe3TqZLvvjsEpCMieGg/X0K6XfRAAAAAA7msoAAFsCJgAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAEAWwIzAAAAAAAAAABrIVuK+5v6E8EK2ZZe3TqZLvvjsEpCMieGg/X0K6XfRAAAAAA7msmcAFsCJgAAAAEAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAA==\",\n" +
                            "\"memo_type\": \"none\",\n" +
                            "\"signatures\": [\n" +
                            "\"KB0t3rRx3zL86nWjmrUpaSiNYJhqrua8nFJIP7DGDfK8NwEY3CLGtlonCaHIw2cO+PCXXO7xKiaLmK7rXosVDQ==\"\n" +
                            "],\n" +
                            "\"valid_after\": \"1970-01-01T00:00:00Z\"\n" +
                            "},\n" +
                            "{\n" +
                            "\"_links\": {\n" +
                            "\"self\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions/b4fe656935ff44edf9048c3976d4963acceac77b56e6712628347d2d51ee5022\"\n" +
                            "},\n" +
                            "\"account\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GAVEEXYGVKCTG27SJCQPDKYLB53VIILQ5DSPFMXHAP2W6JDLJ4ANMFAH\"\n" +
                            "},\n" +
                            "\"ledger\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/ledgers/5964326\"\n" +
                            "},\n" +
                            "\"operations\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions/b4fe656935ff44edf9048c3976d4963acceac77b56e6712628347d2d51ee5022/operations{?cursor,limit,order}\",\n" +
                            "\"templated\": true\n" +
                            "},\n" +
                            "\"effects\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions/b4fe656935ff44edf9048c3976d4963acceac77b56e6712628347d2d51ee5022/effects{?cursor,limit,order}\",\n" +
                            "\"templated\": true\n" +
                            "},\n" +
                            "\"precedes\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions?order=asc&cursor=25616585112686592\"\n" +
                            "},\n" +
                            "\"succeeds\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/transactions?order=desc&cursor=25616585112686592\"\n" +
                            "}\n" +
                            "},\n" +
                            "\"id\": \"b4fe656935ff44edf9048c3976d4963acceac77b56e6712628347d2d51ee5022\",\n" +
                            "\"paging_token\": \"25616585112686592\",\n" +
                            "\"hash\": \"b4fe656935ff44edf9048c3976d4963acceac77b56e6712628347d2d51ee5022\",\n" +
                            "\"ledger\": 5964326,\n" +
                            "\"created_at\": \"2020-04-15T23:54:25Z\",\n" +
                            "\"source_account\": \"GAVEEXYGVKCTG27SJCQPDKYLB53VIILQ5DSPFMXHAP2W6JDLJ4ANMFAH\",\n" +
                            "\"source_account_sequence\": \"2014339699294\",\n" +
                            "\"fee_paid\": 0,\n" +
                            "\"operation_count\": 1,\n" +
                            "\"envelope_xdr\": \"AAAAACpCXwaqhTNr8kig8asLD3dUIXDo5PKy5wP1byRrTwDWAAAAZAAAAdUAAJJeAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAayFbivub+hPBCtmWXt06mS7747BKQjInhoP19Cul30QAAAAAO5rKAAAAAAAAAAABa08A1gAAAEBbsjZG07vxpQfT+T3wHY5p5aAQ7BMqNqIBbtp1Jz3a0CWRKyT91Y5amJCJaRtiDPTDabphlPwOlAesP37xCv8E\",\n" +
                            "\"result_xdr\": \"AAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAA=\",\n" +
                            "\"result_meta_xdr\": \"AAAAAAAAAAEAAAADAAAAAABbAiYAAAAAAAAAAGshW4r7m/oTwQrZll7dOpku++OwSkIyJ4aD9fQrpd9EAAAAADuaygAAWwImAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAwBbAiYAAAAAAAAAACpCXwaqhTNr8kig8asLD3dUIXDo5PKy5wP1byRrTwDWDdz99ZYh/eQAAAHVAACSXgAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAQBbAiYAAAAAAAAAACpCXwaqhTNr8kig8asLD3dUIXDo5PKy5wP1byRrTwDWDdz99VqHM+QAAAHVAACSXgAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAA\",\n" +
                            "\"fee_meta_xdr\": \"AAAAAgAAAAMAWv/KAAAAAAAAAAAqQl8GqoUza/JIoPGrCw93VCFw6OTysucD9W8ka08A1g3c/fWWIf3kAAAB1QAAkl0AAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAEAWwImAAAAAAAAAAAqQl8GqoUza/JIoPGrCw93VCFw6OTysucD9W8ka08A1g3c/fWWIf3kAAAB1QAAkl4AAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAA==\",\n" +
                            "\"memo_type\": \"none\",\n" +
                            "\"signatures\": [\n" +
                            "\"W7I2RtO78aUH0/k98B2OaeWgEOwTKjaiAW7adSc92tAlkSsk/dWOWpiQiWkbYgz0w2m6YZT8DpQHrD9+8Qr/BA==\"\n" +
                            "]\n" +
                            "}\n" +
                            "]\n" +
                            "}\n" +
                            "}"
                )
        )

        val account =
            TestUtils.fromAccountId("GBVSCW4K7ON7UE6BBLMZMXW5HKMS567DWBFEEMRHQ2B7L5BLUXPUIJMM")

        sut.getTransactionHistory(KinTransactionApi.GetTransactionHistoryRequest(account.id)) {
            assertEquals(KinTransactionApi.GetTransactionHistoryResponse.Result.Ok, it.result)
            assertEquals(4, it.transactions?.size)
            assertEquals(3, it.transactions?.asKinPayments()?.size)
        }
    }

    @Test
    fun getTransactionHistory_notFound() {

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody(
                    ""
                )
        )

        val account =
            TestUtils.fromAccountId("GBVSCW4K7ON7UE6BBLMZMXW5HKMS567DWBFEEMRHQ2B7L5BLUXPUIJMM")

        sut.getTransactionHistory(KinTransactionApi.GetTransactionHistoryRequest(account.id)) {
            assertEquals(KinTransactionApi.GetTransactionHistoryResponse.Result.NotFound, it.result)
            assertEquals(null, it.transactions)
        }
    }

    @Test
    fun getTransactionHistory_transientFailure() {

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody(
                    ""
                )
        )

        val account =
            TestUtils.fromAccountId("GBVSCW4K7ON7UE6BBLMZMXW5HKMS567DWBFEEMRHQ2B7L5BLUXPUIJMM")

        sut.getTransactionHistory(KinTransactionApi.GetTransactionHistoryRequest(account.id)) {
            assertTrue(it.result is KinTransactionApi.GetTransactionHistoryResponse.Result.TransientFailure)
            assertEquals(null, it.transactions)
        }
    }

    @Test
    fun getTransactionHistory_undefinedError() {

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(480)
                .setBody(
                    ""
                )
        )

        val account =
            TestUtils.fromAccountId("GBVSCW4K7ON7UE6BBLMZMXW5HKMS567DWBFEEMRHQ2B7L5BLUXPUIJMM")

        sut.getTransactionHistory(KinTransactionApi.GetTransactionHistoryRequest(account.id)) {
            assertTrue(it.result is KinTransactionApi.GetTransactionHistoryResponse.Result.UndefinedError)
            assertEquals(null, it.transactions)
        }
    }

    @Test
    fun getTransactionHistory_timeout() {
        val account =
            TestUtils.fromAccountId("GBVSCW4K7ON7UE6BBLMZMXW5HKMS567DWBFEEMRHQ2B7L5BLUXPUIJMM")

        sut.getTransactionHistory(KinTransactionApi.GetTransactionHistoryRequest(account.id)) {
            assertTrue(it.result is KinTransactionApi.GetTransactionHistoryResponse.Result.TransientFailure)
            assertEquals(null, it.transactions)
        }
    }

    @Test
    fun getTransactionHistory_tooManyRequests_transientFailure() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(429)
                .setBody("")
        )

        val account =
            TestUtils.fromAccountId("GBVSCW4K7ON7UE6BBLMZMXW5HKMS567DWBFEEMRHQ2B7L5BLUXPUIJMM")

        sut.getTransactionHistory(KinTransactionApi.GetTransactionHistoryRequest(account.id)) {
            assertTrue(it.result is KinTransactionApi.GetTransactionHistoryResponse.Result.TransientFailure)
            assertEquals(null, it.transactions)
        }
    }

    @Test
    fun getTransactionHistory_serverGone() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(410)
                .setBody("")
        )

        val account =
            TestUtils.fromAccountId("GBVSCW4K7ON7UE6BBLMZMXW5HKMS567DWBFEEMRHQ2B7L5BLUXPUIJMM")

        sut.getTransactionHistory(KinTransactionApi.GetTransactionHistoryRequest(account.id)) {
            assertTrue(it.result is KinTransactionApi.GetTransactionHistoryResponse.Result.UpgradeRequiredError)
            assertEquals(null, it.transactions)
        }
    }

    @Test
    fun getTransactionHistory_undefinedHttpError() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(480)
                .setBody("")
        )

        val account =
            TestUtils.fromAccountId("GBVSCW4K7ON7UE6BBLMZMXW5HKMS567DWBFEEMRHQ2B7L5BLUXPUIJMM")

        sut.getTransactionHistory(KinTransactionApi.GetTransactionHistoryRequest(account.id)) {
            assertTrue(it.result is KinTransactionApi.GetTransactionHistoryResponse.Result.UndefinedError)
            assertEquals(null, it.transactions)
        }
    }

    @Test
    fun getTransactionHistory_undefinedError2() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("invalid_body")
        )

        val account =
            TestUtils.fromAccountId("GBVSCW4K7ON7UE6BBLMZMXW5HKMS567DWBFEEMRHQ2B7L5BLUXPUIJMM")

        sut.getTransactionHistory(KinTransactionApi.GetTransactionHistoryRequest(account.id)) {
            assertTrue(it.result is KinTransactionApi.GetTransactionHistoryResponse.Result.UndefinedError)
            assertEquals(null, it.transactions)
        }
    }

    @Test
    fun getTransactionMinFee_success() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    "{\n" +
                            "\"_links\": {\n" +
                            "\"self\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/ledgers?cursor=&limit=1&order=desc\"\n" +
                            "},\n" +
                            "\"next\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/ledgers?cursor=25617096213790720&limit=1&order=desc\"\n" +
                            "},\n" +
                            "\"prev\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/ledgers?cursor=25617096213790720&limit=1&order=asc\"\n" +
                            "}\n" +
                            "},\n" +
                            "\"_embedded\": {\n" +
                            "\"records\": [\n" +
                            "{\n" +
                            "\"_links\": {\n" +
                            "\"self\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/ledgers/5964445\"\n" +
                            "},\n" +
                            "\"transactions\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/ledgers/5964445/transactions{?cursor,limit,order}\",\n" +
                            "\"templated\": true\n" +
                            "},\n" +
                            "\"operations\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/ledgers/5964445/operations{?cursor,limit,order}\",\n" +
                            "\"templated\": true\n" +
                            "},\n" +
                            "\"payments\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/ledgers/5964445/payments{?cursor,limit,order}\",\n" +
                            "\"templated\": true\n" +
                            "},\n" +
                            "\"effects\": {\n" +
                            "\"href\": \"https://horizon-testnet.kininfrastructure.com/ledgers/5964445/effects{?cursor,limit,order}\",\n" +
                            "\"templated\": true\n" +
                            "}\n" +
                            "},\n" +
                            "\"id\": \"96c0060f7646e19ae01d46056e00d774412040c659c7559da9a77a66807c7664\",\n" +
                            "\"paging_token\": \"25617096213790720\",\n" +
                            "\"hash\": \"96c0060f7646e19ae01d46056e00d774412040c659c7559da9a77a66807c7664\",\n" +
                            "\"prev_hash\": \"802304008224a6071ad0e3e50313407a5c0d41496070a9320bf86a82336381d4\",\n" +
                            "\"sequence\": 5964445,\n" +
                            "\"transaction_count\": 0,\n" +
                            "\"successful_transaction_count\": 0,\n" +
                            "\"failed_transaction_count\": 0,\n" +
                            "\"operation_count\": 0,\n" +
                            "\"closed_at\": \"2020-04-16T00:05:27Z\",\n" +
                            "\"total_coins\": \"10000000000000.00000\",\n" +
                            "\"fee_pool\": \"6159.64162\",\n" +
                            "\"base_fee_in_stroops\": 100,\n" +
                            "\"base_reserve_in_stroops\": 0,\n" +
                            "\"max_tx_set_size\": 500,\n" +
                            "\"protocol_version\": 9,\n" +
                            "\"header_xdr\": \"AAAACYAjBACCJKYHGtDj5QMTQHpcDUFJYHCpMgv4aoIzY4HUPmb32X7CJWFgZyQcj+dDyZu7JAjFIMR9TN/cgqCYLKAAAAAAXpehRwAAAAAAAAAA3z9hmASpL9tAVxktxD3XSOp3itxSvEmM6AUkwBS4ERm8PeZPVCjLq3wrE1C7+QkS9wnnjK4HYyBzIUJjL189AgBbAp0N4Lazp2QAAAAAAAAktt4CAAAAAAAAAAAAAAACAAAAZAAAAAAAAAH0971hei57V+oCQxFu65/6yujpkLWjTSgpCWtc8XYwCyvesMT/4GINy+w74C4dkYNFWkCNpLF7vLo6eaN/E+572o2f4jvmT1HOVCWDs9BQ8wUTcg7J+sJwIOv+5x9VNwpP+HxbH0lOvydqqeiI+6T0yQexDwlfm6HS+lkf2wFLJcoAAAAA\"\n" +
                            "}\n" +
                            "]\n" +
                            "}\n" +
                            "}"
                )
        )

        sut.getTransactionMinFee {
            assertEquals(KinTransactionApi.GetMinFeeForTransactionResponse.Result.Ok, it.result)
            assertEquals(QuarkAmount(100), it.minFee)
        }
    }

    @Test
    fun getTransactionMinFee_internalServerError() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody(
                    ""
                )
        )

        sut.getTransactionMinFee {
            assertTrue(it.result is KinTransactionApi.GetMinFeeForTransactionResponse.Result.TransientFailure)
            assertNull(it.minFee)
        }
    }

    @Test
    fun getTransactionMinFee_timeout() {
        sut.getTransactionMinFee {
            assertTrue(it.result is KinTransactionApi.GetMinFeeForTransactionResponse.Result.TransientFailure)
            assertNull(it.minFee)
        }
    }

    @Test
    fun getTransactionMinFee_tooManyRequests() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(429)
                .setBody(
                    ""
                )
        )

        sut.getTransactionMinFee {
            assertTrue(it.result is KinTransactionApi.GetMinFeeForTransactionResponse.Result.TransientFailure)
            assertNull(it.minFee)
        }
    }

    @Test
    fun getTransactionMinFee_serverGone() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(410)
                .setBody(
                    ""
                )
        )

        sut.getTransactionMinFee {
            assertTrue(it.result is KinTransactionApi.GetMinFeeForTransactionResponse.Result.UpgradeRequiredError)
            assertNull(it.minFee)
        }
    }

    @Test
    fun getTransactionMinFee_undefinedError() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(480)
                .setBody(
                    ""
                )
        )

        sut.getTransactionMinFee {
            assertTrue(it.result is KinTransactionApi.GetMinFeeForTransactionResponse.Result.UndefinedError)
            assertNull(it.minFee)
        }
    }

    @Test
    fun getTransactionMinFee_undefinedError2() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    "invalid_body"
                )
        )

        sut.getTransactionMinFee {
            assertTrue(it.result is KinTransactionApi.GetMinFeeForTransactionResponse.Result.UndefinedError)
            assertNull(it.minFee)
        }
    }

    @Test
    fun streamAccount() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    ("{" +
                            "  \"_links\": {" +
                            "    \"self\": {" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GA3U7NQPTL2UTUMTQLOAXQHD4YHIT5PQSZR7EISDQRHJHKYRZRGQYDPT\"" +
                            "    }," +
                            "    \"transactions\": {" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GA3U7NQPTL2UTUMTQLOAXQHD4YHIT5PQSZR7EISDQRHJHKYRZRGQYDPT/transactions{?cursor,limit,order}\"," +
                            "      \"templated\": true" +
                            "    }," +
                            "    \"operations\": {" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GA3U7NQPTL2UTUMTQLOAXQHD4YHIT5PQSZR7EISDQRHJHKYRZRGQYDPT/operations{?cursor,limit,order}\"," +
                            "      \"templated\": true" +
                            "    }," +
                            "    \"payments\": {" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GA3U7NQPTL2UTUMTQLOAXQHD4YHIT5PQSZR7EISDQRHJHKYRZRGQYDPT/payments{?cursor,limit,order}\"," +
                            "      \"templated\": true" +
                            "    }," +
                            "    \"effects\": {" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GA3U7NQPTL2UTUMTQLOAXQHD4YHIT5PQSZR7EISDQRHJHKYRZRGQYDPT/effects{?cursor,limit,order}\"," +
                            "      \"templated\": true" +
                            "    }," +
                            "    \"offers\": {" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GA3U7NQPTL2UTUMTQLOAXQHD4YHIT5PQSZR7EISDQRHJHKYRZRGQYDPT/offers{?cursor,limit,order}\"," +
                            "      \"templated\": true" +
                            "    }," +
                            "    \"trades\": {" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GA3U7NQPTL2UTUMTQLOAXQHD4YHIT5PQSZR7EISDQRHJHKYRZRGQYDPT/trades{?cursor,limit,order}\"," +
                            "      \"templated\": true" +
                            "    }," +
                            "    \"data\": {" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GA3U7NQPTL2UTUMTQLOAXQHD4YHIT5PQSZR7EISDQRHJHKYRZRGQYDPT/data/{key}\"," +
                            "      \"templated\": true" +
                            "    }" +
                            "  }," +
                            "  \"id\": \"GA3U7NQPTL2UTUMTQLOAXQHD4YHIT5PQSZR7EISDQRHJHKYRZRGQYDPT\"," +
                            "  \"paging_token\": \"\"," +
                            "  \"account_id\": \"GA3U7NQPTL2UTUMTQLOAXQHD4YHIT5PQSZR7EISDQRHJHKYRZRGQYDPT\"," +
                            "  \"sequence\": \"25150984887992321\"," +
                            "  \"subentry_count\": 0," +
                            "  \"last_modified_ledger\": 5855929," +
                            "  \"thresholds\": {" +
                            "    \"low_threshold\": 0," +
                            "    \"med_threshold\": 0," +
                            "    \"high_threshold\": 0" +
                            "  }," +
                            "  \"flags\": {" +
                            "    \"auth_required\": false," +
                            "    \"auth_revocable\": false," +
                            "    \"auth_immutable\": false" +
                            "  }," +
                            "  \"balances\": [" +
                            "    {" +
                            "      \"balance\": \"9999.99900\"," +
                            "      \"buying_liabilities\": \"0.00000\"," +
                            "      \"selling_liabilities\": \"0.00000\"," +
                            "      \"asset_type\": \"native\"" +
                            "    }" +
                            "  ]," +
                            "  \"signers\": [" +
                            "    {" +
                            "      \"public_key\": \"GA3U7NQPTL2UTUMTQLOAXQHD4YHIT5PQSZR7EISDQRHJHKYRZRGQYDPT\"," +
                            "      \"weight\": 1," +
                            "      \"key\": \"GA3U7NQPTL2UTUMTQLOAXQHD4YHIT5PQSZR7EISDQRHJHKYRZRGQYDPT\"," +
                            "      \"type\": \"ed25519_public_key\"" +
                            "    }" +
                            "  ]," +
                            "  \"data\": {}" +
                            "}"
                            ).chunkForStream() + "\n"
                )
        )

        val lifecycle = DisposeBag()

        sut.streamAccount(account.id).test {
            assertEquals(
                KinAccount(
                    TestUtils.fromAccountId("GA3U7NQPTL2UTUMTQLOAXQHD4YHIT5PQSZR7EISDQRHJHKYRZRGQYDPT").key,
                    balance = KinBalance(KinAmount(9999.99900)),
                    status = KinAccount.Status.Registered(25150984887992321)
                ),
                value
            )
        }.disposedBy(lifecycle)

        lifecycle.dispose()
    }

    @Test
    fun streamNewTransactions() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    ("{" +
                            "  \"memo\": \"testMemo\"," +
                            "  \"_links\": {" +
                            "    \"self\": {" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/transactions/a809e07ccd24cea8329adbd8b75139c8bd20bb1f72d5d0bcad6b4e4ac3dc73a5\"" +
                            "    }," +
                            "    \"account\": {" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/accounts/GA3U7NQPTL2UTUMTQLOAXQHD4YHIT5PQSZR7EISDQRHJHKYRZRGQYDPT\"" +
                            "    }," +
                            "    \"ledger\": {" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/ledgers/5855929\"" +
                            "    }," +
                            "    \"operations\": {" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/transactions/a809e07ccd24cea8329adbd8b75139c8bd20bb1f72d5d0bcad6b4e4ac3dc73a5/operations{?cursor,limit,order}\"," +
                            "      \"templated\": true" +
                            "    }," +
                            "    \"effects\": {" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/transactions/a809e07ccd24cea8329adbd8b75139c8bd20bb1f72d5d0bcad6b4e4ac3dc73a5/effects{?cursor,limit,order}\"," +
                            "      \"templated\": true" +
                            "    }," +
                            "    \"precedes\": {" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/transactions?order=asc&cursor=25151023542702080\"" +
                            "    }," +
                            "    \"succeeds\": {" +
                            "      \"href\": \"https://horizon-testnet.kininfrastructure.com/transactions?order=desc&cursor=25151023542702080\"" +
                            "    }" +
                            "  }," +
                            "  \"id\": \"a809e07ccd24cea8329adbd8b75139c8bd20bb1f72d5d0bcad6b4e4ac3dc73a5\"," +
                            "  \"paging_token\": \"25151023542702080\"," +
                            "  \"hash\": \"a809e07ccd24cea8329adbd8b75139c8bd20bb1f72d5d0bcad6b4e4ac3dc73a5\"," +
                            "  \"ledger\": 5855929," +
                            "  \"created_at\": \"2020-04-09T03:58:09Z\"," +
                            "  \"source_account\": \"GA3U7NQPTL2UTUMTQLOAXQHD4YHIT5PQSZR7EISDQRHJHKYRZRGQYDPT\"," +
                            "  \"source_account_sequence\": \"25150984887992321\"," +
                            "  \"fee_paid\": 100," +
                            "  \"operation_count\": 1," +
                            "  \"envelope_xdr\": \"AAAAADdPtg+a9UnRk4LcC8Dj5g6J9fCWY/IiQ4ROk6sRzE0MAAAAZABZWrAAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAABAAAACHRlc3RNZW1vAAAAAQAAAAAAAAABAAAAADdPtg+a9UnRk4LcC8Dj5g6J9fCWY/IiQ4ROk6sRzE0MAAAAAAAAAAAAu67gAAAAAAAAAAERzE0MAAAAQB98hgcZ1AWvtdXA9HhtXb/1+SccGFA0xgtN55IECL9rGvCf+s4PQnIOhGQ4yxC8KoQ2PtNJhd9Ipr/4g0f87w0=\"," +
                            "  \"result_xdr\": \"AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=\"," +
                            "  \"result_meta_xdr\": \"AAAAAAAAAAEAAAAA\"," +
                            "  \"fee_meta_xdr\": \"AAAAAgAAAAMAWVqwAAAAAAAAAAA3T7YPmvVJ0ZOC3AvA4+YOifXwlmPyIkOETpOrEcxNDAAAAAA7msoAAFlasAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAEAWVq5AAAAAAAAAAA3T7YPmvVJ0ZOC3AvA4+YOifXwlmPyIkOETpOrEcxNDAAAAAA7msmcAFlasAAAAAEAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAA==\"," +
                            "  \"memo_type\": \"text\"," +
                            "  \"signatures\": [" +
                            "    \"H3yGBxnUBa+11cD0eG1dv/X5JxwYUDTGC03nkgQIv2sa8J/6zg9Ccg6EZDjLELwqhDY+00mF30imv/iDR/zvDQ==\"" +
                            "  ]," +
                            "  \"valid_after\": \"1970-01-01T00:00:00Z\"" +
                            "}"
                            ).chunkForStream() + "\n"
                )
        )

        val lifecycle = DisposeBag()

        sut.streamNewTransactions(account.id).test {
            val expectedTxn = TestUtils.kinTransactionFromXdr(
                "AAAAADdPtg+a9UnRk4LcC8Dj5g6J9fCWY/IiQ4ROk6sRzE0MAAAAZABZWrAAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAABAAAACHRlc3RNZW1vAAAAAQAAAAAAAAABAAAAADdPtg+a9UnRk4LcC8Dj5g6J9fCWY/IiQ4ROk6sRzE0MAAAAAAAAAAAAu67gAAAAAAAAAAERzE0MAAAAQB98hgcZ1AWvtdXA9HhtXb/1+SccGFA0xgtN55IECL9rGvCf+s4PQnIOhGQ4yxC8KoQ2PtNJhd9Ipr/4g0f87w0=",
                KinTransaction.RecordType.InFlight(System.currentTimeMillis())
            )
            assertTrue { expectedTxn.bytesValue.contentEquals(value?.bytesValue!!) }
        }.disposedBy(lifecycle)

        lifecycle.dispose()
    }
}
