package org.kin.sdk.base

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.kin.sdk.base.models.KinBinaryMemo
import org.kin.sdk.base.models.AppIdx
import org.kin.sdk.base.models.Invoice
import org.kin.sdk.base.models.InvoiceList
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinDateFormat
import org.kin.sdk.base.models.KinMemo
import org.kin.sdk.base.models.KinPaymentItem
import org.kin.sdk.base.models.LineItem
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.asKinPayments
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.network.api.KinAccountApi
import org.kin.sdk.base.network.api.KinAccountApi.GetAccountRequest
import org.kin.sdk.base.network.api.KinAccountApi.GetAccountResponse
import org.kin.sdk.base.network.api.KinAccountCreationApi
import org.kin.sdk.base.network.api.KinAccountCreationApi.CreateAccountRequest
import org.kin.sdk.base.network.api.KinAccountCreationApi.CreateAccountResponse
import org.kin.sdk.base.network.api.KinStreamingApi
import org.kin.sdk.base.network.api.KinTransactionApi
import org.kin.sdk.base.network.api.KinTransactionApi.GetMinFeeForTransactionResponse
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionHistoryRequest
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionHistoryResponse
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionRequest
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionResponse
import org.kin.sdk.base.network.api.KinTransactionApi.SubmitTransactionResponse
import org.kin.sdk.base.network.api.KinTransactionWhitelistingApi
import org.kin.sdk.base.network.api.KinTransactionWhitelistingApi.WhitelistTransactionRequest
import org.kin.sdk.base.network.api.KinTransactionWhitelistingApi.WhitelistTransactionResponse
import org.kin.sdk.base.network.services.KinService
import org.kin.sdk.base.network.services.KinServiceImpl
import org.kin.sdk.base.stellar.models.ApiConfig
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.tools.KinLoggerFactoryImpl
import org.kin.sdk.base.tools.NetworkOperationsHandlerImpl
import org.kin.sdk.base.tools.Optional
import org.kin.sdk.base.tools.TestUtils
import org.kin.sdk.base.tools.ValueSubject
import org.kin.sdk.base.tools.test
import org.kin.sdk.base.tools.updateStatus
import org.kin.stellarfork.codec.Base64
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KinServiceImplTest {

    private companion object {
        val account = TestUtils.newSigningKinAccount()
        val registeredAccount = account.updateStatus(KinAccount.Status.Registered(1234))
        val createRequest = CreateAccountRequest(account.id)
        val getAccountRequest = GetAccountRequest(account.id)

        val pagingToken = KinTransaction.PagingToken("16576645322248192")
        val historicalKinTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=",
            KinTransaction.RecordType.Historical(
                KinDateFormat("2019-12-12T21:32:43Z").timestamp,
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                pagingToken
            )
        )
        val historicalKinTransaction2 = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7BBAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=",
            KinTransaction.RecordType.Historical(
                KinDateFormat("2019-12-12T21:32:43Z").timestamp,
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                pagingToken
            )
        )

        val getTransactionHistoryRequestLatestPage =
            GetTransactionHistoryRequest(account.id)
        val getTransactionHistoryRequestWithPagingToken =
            GetTransactionHistoryRequest(account.id, pagingToken)

        val getTransactionRequest =
            GetTransactionRequest(historicalKinTransaction.transactionHash)
    }

    private lateinit var sut: KinService

    private lateinit var mockAccountApi: KinAccountApi
    private lateinit var mockTransactionApi: KinTransactionApi
    private lateinit var mockStreamingApi: KinStreamingApi
    private lateinit var mockAccountCreationApi: KinAccountCreationApi
    private lateinit var mockTransactionWhitelistingApi: KinTransactionWhitelistingApi

    val logger = KinLoggerFactoryImpl(true)

    @Before
    fun setUp() {
        mockAccountApi = mock {}
        mockTransactionApi = mock {}
        mockStreamingApi = mock {}
        mockAccountCreationApi = mock {}
        mockTransactionWhitelistingApi = mock {
            on { isWhitelistingAvailable } doReturn false
        }
        sut = KinServiceImpl(
            ApiConfig.TestNetHorizon.networkEnv,
            NetworkOperationsHandlerImpl(logger = logger),
            mockAccountApi,
            mockTransactionApi,
            mockStreamingApi,
            mockAccountCreationApi,
            mockTransactionWhitelistingApi,
            logger
        )
    }

    @Test
    fun createAccount_success() {
        doAnswer {
            val respond =
                it.getArgument<(CreateAccountResponse) -> Unit>(1)
            respond(
                CreateAccountResponse(
                    CreateAccountResponse.Result.Ok,
                    registeredAccount
                )
            )
        }.whenever(mockAccountCreationApi).createAccount(eq(createRequest), any())

        sut.createAccount(account.id, account.key as Key.PrivateKey).test(100) {
            assertNull(error)
            assertEquals(registeredAccount, value)

            verify(mockAccountCreationApi).createAccount(eq(createRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verifyZeroInteractions(mockTransactionApi)
        }
    }


    @Test
    fun createAccount_already_exists() {
        doAnswer {
            val respond =
                it.getArgument<(CreateAccountResponse) -> Unit>(1)
            respond(
                CreateAccountResponse(
                    CreateAccountResponse.Result.Exists,
                    registeredAccount
                )
            )
        }.whenever(mockAccountCreationApi).createAccount(eq(createRequest), any())

        sut.createAccount(account.id, account.key as Key.PrivateKey).test {
            assertNull(error)
            assertEquals(registeredAccount, value)

            verify(mockAccountCreationApi).createAccount(eq(createRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun createAccount_already_exists_missing_account() {
        doAnswer {
            val respond =
                it.getArgument<(CreateAccountResponse) -> Unit>(1)
            respond(
                CreateAccountResponse(
                    CreateAccountResponse.Result.Exists
                )
            )
        }.whenever(mockAccountCreationApi).createAccount(eq(createRequest), any())

        doAnswer {
            it.getArgument<(GetAccountResponse) -> Unit>(1)
                .invoke(GetAccountResponse(GetAccountResponse.Result.Ok, registeredAccount))
        }.whenever(mockAccountApi).getAccount(eq(GetAccountRequest(registeredAccount.id)), any())

        sut.createAccount(account.id, account.key as Key.PrivateKey).test {
            assertNull(error)
            assertEquals(registeredAccount, value)

            verify(mockAccountCreationApi).createAccount(eq(createRequest), any())
            verify(mockAccountApi).getAccount(eq(GetAccountRequest(registeredAccount.id)), any())
            verifyNoMoreInteractions(mockAccountApi)
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun createAccount_malformed_response() {
        doAnswer {
            val respond =
                it.getArgument<(CreateAccountResponse) -> Unit>(1)
            respond(
                CreateAccountResponse(
                    CreateAccountResponse.Result.Ok
                )
            )
        }.whenever(mockAccountCreationApi).createAccount(eq(createRequest), any())

        sut.createAccount(account.id, account.key as Key.PrivateKey).test {
            assertEquals(KinService.FatalError.IllegalResponse, error)
            assertNull(value)

            verify(mockAccountCreationApi).createAccount(eq(createRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun createAccount_unavailable() {
        doAnswer {
            val respond =
                it.getArgument<(CreateAccountResponse) -> Unit>(1)
            respond(
                CreateAccountResponse(
                    CreateAccountResponse.Result.Unavailable
                )
            )
        }.whenever(mockAccountCreationApi).createAccount(eq(createRequest), any())

        sut.createAccount(account.id, account.key as Key.PrivateKey).test {
            assertEquals(KinService.FatalError.PermanentlyUnavailable, error)
            assertNull(value?.status)

            verify(mockAccountCreationApi).createAccount(eq(createRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun createAccount_undefined_failure() {
        doAnswer {
            val respond =
                it.getArgument<(CreateAccountResponse) -> Unit>(1)
            respond(
                CreateAccountResponse(
                    CreateAccountResponse.Result.UndefinedError(Exception())
                )
            )
        }.whenever(mockAccountCreationApi).createAccount(eq(createRequest), any())

        sut.createAccount(account.id, account.key as Key.PrivateKey).test {
            assertTrue(error is KinService.FatalError.UnexpectedServiceError)
            assertNull(value?.status)

            verify(mockAccountCreationApi).createAccount(eq(createRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun createAccount_transient_failure() {
        doAnswer {
            val respond =
                it.getArgument<(CreateAccountResponse) -> Unit>(1)
            respond(
                CreateAccountResponse(
                    CreateAccountResponse.Result.TransientFailure(Exception())
                )
            )
        }.whenever(mockAccountCreationApi).createAccount(eq(createRequest), any())

        sut.createAccount(account.id, account.key as Key.PrivateKey).test {
            assertTrue(error is KinService.FatalError.TransientFailure)
            assertNull(value?.status)

            verify(mockAccountCreationApi).createAccount(eq(createRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun createAccount_sdkUpgradeRequired() {
        doAnswer {
            val respond =
                it.getArgument<(CreateAccountResponse) -> Unit>(1)
            respond(
                CreateAccountResponse(
                    CreateAccountResponse.Result.UpgradeRequiredError
                )
            )
        }.whenever(mockAccountCreationApi).createAccount(eq(createRequest), any())

        sut.createAccount(account.id, account.key as Key.PrivateKey).test {
            assertTrue(error is KinService.FatalError.SDKUpgradeRequired)
            assertNull(value?.status)

            verify(mockAccountCreationApi).createAccount(eq(createRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun getAccount_success() {
        doAnswer {
            val respond =
                it.getArgument<(GetAccountResponse) -> Unit>(1)
            respond(
                GetAccountResponse(
                    GetAccountResponse.Result.Ok,
                    registeredAccount
                )
            )
        }.whenever(mockAccountApi).getAccount(eq(getAccountRequest), any())

        sut.getAccount(account.id).test {
            assertNull(error)
            assertEquals(registeredAccount, value)

            verify(mockAccountApi).getAccount(eq(getAccountRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun getAccount_not_found() {
        doAnswer {
            val respond =
                it.getArgument<(GetAccountResponse) -> Unit>(1)
            respond(
                GetAccountResponse(
                    GetAccountResponse.Result.NotFound
                )
            )
        }.whenever(mockAccountApi).getAccount(eq(getAccountRequest), any())

        sut.getAccount(account.id).test {
            assertNull(value)
            assertEquals(KinService.FatalError.ItemNotFound, error)

            verify(mockAccountApi).getAccount(eq(getAccountRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun getAccount_malformed_response() {
        doAnswer {
            val respond =
                it.getArgument<(GetAccountResponse) -> Unit>(1)
            respond(
                GetAccountResponse(
                    GetAccountResponse.Result.Ok
                )
            )
        }.whenever(mockAccountApi).getAccount(eq(getAccountRequest), any())

        sut.getAccount(account.id).test {
            assertNull(value)
            assertEquals(KinService.FatalError.IllegalResponse, error)

            verify(mockAccountApi).getAccount(eq(getAccountRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun getAccount_undefined_failure() {
        doAnswer {
            val respond =
                it.getArgument<(GetAccountResponse) -> Unit>(1)
            respond(
                GetAccountResponse(
                    GetAccountResponse.Result.UndefinedError(Exception())
                )
            )
        }.whenever(mockAccountApi).getAccount(eq(getAccountRequest), any())

        sut.getAccount(account.id).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.UnexpectedServiceError)

            verify(mockAccountApi).getAccount(eq(getAccountRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun getAccount_transient_failure() {
        doAnswer {
            val respond =
                it.getArgument<(GetAccountResponse) -> Unit>(1)
            respond(
                GetAccountResponse(
                    GetAccountResponse.Result.TransientFailure(Exception())
                )
            )
        }.whenever(mockAccountApi).getAccount(eq(getAccountRequest), any())

        sut.getAccount(account.id).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.TransientFailure)

            verify(mockAccountApi).getAccount(eq(getAccountRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun getAccount_SDKUpgradeRequired() {
        doAnswer {
            val respond =
                it.getArgument<(GetAccountResponse) -> Unit>(1)
            respond(
                GetAccountResponse(
                    GetAccountResponse.Result.UpgradeRequiredError
                )
            )
        }.whenever(mockAccountApi).getAccount(eq(getAccountRequest), any())

        sut.getAccount(account.id).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.SDKUpgradeRequired)

            verify(mockAccountApi).getAccount(eq(getAccountRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun getLatestTransactions_success() {
        doAnswer {
            val respond =
                it.getArgument<(GetTransactionHistoryResponse) -> Unit>(1)
            val transactions = listOf(historicalKinTransaction)
            respond(
                GetTransactionHistoryResponse(
                    GetTransactionHistoryResponse.Result.Ok,
                    transactions
                )
            )
        }.whenever(mockTransactionApi)
            .getTransactionHistory(eq(getTransactionHistoryRequestLatestPage), any())

        sut.getLatestTransactions(account.id).test {
            assertEquals(value, listOf(historicalKinTransaction))
            assertNull(error)

            verify(mockTransactionApi)
                .getTransactionHistory(eq(getTransactionHistoryRequestLatestPage), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun getLatestTransactions_illegalResponse() {
        doAnswer {
            val respond =
                it.getArgument<(GetTransactionHistoryResponse) -> Unit>(1)
            respond(
                GetTransactionHistoryResponse(
                    GetTransactionHistoryResponse.Result.Ok,
                    null
                )
            )
        }.whenever(mockTransactionApi)
            .getTransactionHistory(eq(getTransactionHistoryRequestLatestPage), any())

        sut.getLatestTransactions(account.id).test {
            assertTrue(error is KinService.FatalError.IllegalResponse)
            assertNull(value)

            verify(mockTransactionApi)
                .getTransactionHistory(eq(getTransactionHistoryRequestLatestPage), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun getLatestTransactions_not_found() {
        doAnswer {
            val respond =
                it.getArgument<(GetTransactionHistoryResponse) -> Unit>(1)
            respond(
                GetTransactionHistoryResponse(
                    GetTransactionHistoryResponse.Result.NotFound
                )
            )
        }.whenever(mockTransactionApi)
            .getTransactionHistory(eq(getTransactionHistoryRequestLatestPage), any())

        sut.getLatestTransactions(account.id).test {
            assertNull(value)
            assertEquals(KinService.FatalError.ItemNotFound, error)

            verify(mockTransactionApi)
                .getTransactionHistory(eq(getTransactionHistoryRequestLatestPage), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun getLatestTransactions_transient_failure() {
        doAnswer {
            val respond =
                it.getArgument<(GetTransactionHistoryResponse) -> Unit>(1)
            respond(
                GetTransactionHistoryResponse(
                    GetTransactionHistoryResponse.Result.TransientFailure(
                        Exception()
                    )
                )
            )
        }.whenever(mockTransactionApi)
            .getTransactionHistory(eq(getTransactionHistoryRequestLatestPage), any())

        sut.getLatestTransactions(account.id).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.TransientFailure)

            verify(mockTransactionApi)
                .getTransactionHistory(eq(getTransactionHistoryRequestLatestPage), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun getLatestTransactions_undefined_failure() {
        doAnswer {
            val respond =
                it.getArgument<(GetTransactionHistoryResponse) -> Unit>(1)
            respond(
                GetTransactionHistoryResponse(
                    GetTransactionHistoryResponse.Result.UndefinedError(Exception())
                )
            )
        }.whenever(mockTransactionApi)
            .getTransactionHistory(eq(getTransactionHistoryRequestLatestPage), any())

        sut.getLatestTransactions(account.id).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.UnexpectedServiceError)

            verify(mockTransactionApi)
                .getTransactionHistory(eq(getTransactionHistoryRequestLatestPage), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun getLatestTransactions_SDKUpgradeRequired() {
        doAnswer {
            val respond =
                it.getArgument<(GetTransactionHistoryResponse) -> Unit>(1)
            respond(
                GetTransactionHistoryResponse(
                    GetTransactionHistoryResponse.Result.UpgradeRequiredError
                )
            )
        }.whenever(mockTransactionApi)
            .getTransactionHistory(eq(getTransactionHistoryRequestLatestPage), any())

        sut.getLatestTransactions(account.id).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.SDKUpgradeRequired)

            verify(mockTransactionApi)
                .getTransactionHistory(eq(getTransactionHistoryRequestLatestPage), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun getTransactionPage_success() {
        doAnswer {
            val respond =
                it.getArgument<(GetTransactionHistoryResponse) -> Unit>(1)
            val transactions = listOf(historicalKinTransaction)
            respond(
                GetTransactionHistoryResponse(
                    GetTransactionHistoryResponse.Result.Ok,
                    transactions
                )
            )
        }.whenever(mockTransactionApi)
            .getTransactionHistory(eq(getTransactionHistoryRequestWithPagingToken), any())

        sut.getTransactionPage(account.id, pagingToken).test {
            assertEquals(listOf(historicalKinTransaction), value)
            assertNull(error)

            verify(mockTransactionApi)
                .getTransactionHistory(eq(getTransactionHistoryRequestWithPagingToken), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun getTransactionPage_illegalResponse() {
        doAnswer {
            val respond =
                it.getArgument<(GetTransactionHistoryResponse) -> Unit>(1)
            respond(
                GetTransactionHistoryResponse(
                    GetTransactionHistoryResponse.Result.Ok,
                    null
                )
            )
        }.whenever(mockTransactionApi)
            .getTransactionHistory(eq(getTransactionHistoryRequestWithPagingToken), any())

        sut.getTransactionPage(account.id, pagingToken).test {
            assertTrue(error is KinService.FatalError.IllegalResponse)
            assertNull(value)

            verify(mockTransactionApi)
                .getTransactionHistory(eq(getTransactionHistoryRequestWithPagingToken), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun getTransactionPage_not_found() {
        doAnswer {
            val respond =
                it.getArgument<(GetTransactionHistoryResponse) -> Unit>(1)
            respond(
                GetTransactionHistoryResponse(
                    GetTransactionHistoryResponse.Result.NotFound
                )
            )
        }.whenever(mockTransactionApi)
            .getTransactionHistory(eq(getTransactionHistoryRequestWithPagingToken), any())

        sut.getTransactionPage(account.id, pagingToken).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.ItemNotFound)

            verify(mockTransactionApi)
                .getTransactionHistory(eq(getTransactionHistoryRequestWithPagingToken), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun getTransactionPage_transient_failure() {
        doAnswer {
            val respond =
                it.getArgument<(GetTransactionHistoryResponse) -> Unit>(1)
            respond(
                GetTransactionHistoryResponse(
                    GetTransactionHistoryResponse.Result.TransientFailure(Exception())
                )
            )
        }.whenever(mockTransactionApi)
            .getTransactionHistory(eq(getTransactionHistoryRequestWithPagingToken), any())

        sut.getTransactionPage(account.id, pagingToken).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.TransientFailure)

            verify(mockTransactionApi)
                .getTransactionHistory(eq(getTransactionHistoryRequestWithPagingToken), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun getTransactionPage_unexpected_failure() {
        doAnswer {
            val respond =
                it.getArgument<(GetTransactionHistoryResponse) -> Unit>(1)
            respond(
                GetTransactionHistoryResponse(
                    GetTransactionHistoryResponse.Result.UndefinedError(Exception())
                )
            )
        }.whenever(mockTransactionApi)
            .getTransactionHistory(eq(getTransactionHistoryRequestWithPagingToken), any())

        sut.getTransactionPage(account.id, pagingToken).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.UnexpectedServiceError)

            verify(mockTransactionApi)
                .getTransactionHistory(eq(getTransactionHistoryRequestWithPagingToken), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun getTransactionPage_SDKUpgradeRequired() {
        doAnswer {
            val respond =
                it.getArgument<(GetTransactionHistoryResponse) -> Unit>(1)
            respond(
                GetTransactionHistoryResponse(
                    GetTransactionHistoryResponse.Result.UpgradeRequiredError
                )
            )
        }.whenever(mockTransactionApi)
            .getTransactionHistory(eq(getTransactionHistoryRequestWithPagingToken), any())

        sut.getTransactionPage(account.id, pagingToken).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.SDKUpgradeRequired)

            verify(mockTransactionApi)
                .getTransactionHistory(eq(getTransactionHistoryRequestWithPagingToken), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun getTransaction_success() {
        doAnswer {
            val respond =
                it.getArgument<(GetTransactionResponse) -> Unit>(1)
            respond(
                GetTransactionResponse(
                    GetTransactionResponse.Result.Ok,
                    historicalKinTransaction
                )
            )
        }.whenever(mockTransactionApi)
            .getTransaction(eq(getTransactionRequest), any())

        sut.getTransaction(historicalKinTransaction.transactionHash).test {
            assertEquals(historicalKinTransaction, value)
            assertNull(error)

            verify(mockTransactionApi)
                .getTransaction(eq(getTransactionRequest), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun getTransaction_response_malformed() {
        doAnswer {
            val respond =
                it.getArgument<(GetTransactionResponse) -> Unit>(1)
            respond(
                GetTransactionResponse(
                    GetTransactionResponse.Result.Ok
                )
            )
        }.whenever(mockTransactionApi)
            .getTransaction(eq(getTransactionRequest), any())

        sut.getTransaction(historicalKinTransaction.transactionHash).test {
            assertNull(value)
            assertEquals(KinService.FatalError.IllegalResponse, error)

            verify(mockTransactionApi)
                .getTransaction(eq(getTransactionRequest), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun getTransaction_ItemNotFound() {
        doAnswer {
            val respond =
                it.getArgument<(GetTransactionResponse) -> Unit>(1)
            respond(
                GetTransactionResponse(
                    GetTransactionResponse.Result.NotFound
                )
            )
        }.whenever(mockTransactionApi)
            .getTransaction(eq(getTransactionRequest), any())

        sut.getTransaction(historicalKinTransaction.transactionHash).test {
            assertNull(value)
            assertEquals(KinService.FatalError.ItemNotFound, error)

            verify(mockTransactionApi)
                .getTransaction(eq(getTransactionRequest), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun getTransaction_UnexpectedServiceError() {
        doAnswer {
            val respond =
                it.getArgument<(GetTransactionResponse) -> Unit>(1)
            respond(
                GetTransactionResponse(
                    GetTransactionResponse.Result.UndefinedError(Exception())
                )
            )
        }.whenever(mockTransactionApi)
            .getTransaction(eq(getTransactionRequest), any())

        sut.getTransaction(historicalKinTransaction.transactionHash).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.UnexpectedServiceError)

            verify(mockTransactionApi)
                .getTransaction(eq(getTransactionRequest), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun getTransaction_TransientFailure() {
        doAnswer {
            val respond =
                it.getArgument<(GetTransactionResponse) -> Unit>(1)
            respond(
                GetTransactionResponse(
                    GetTransactionResponse.Result.TransientFailure(Exception())
                )
            )
        }.whenever(mockTransactionApi)
            .getTransaction(eq(getTransactionRequest), any())

        sut.getTransaction(historicalKinTransaction.transactionHash).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.TransientFailure)

            verify(mockTransactionApi)
                .getTransaction(eq(getTransactionRequest), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun getTransaction_SDKUpgradeRequired() {
        doAnswer {
            val respond =
                it.getArgument<(GetTransactionResponse) -> Unit>(1)
            respond(
                GetTransactionResponse(
                    GetTransactionResponse.Result.UpgradeRequiredError
                )
            )
        }.whenever(mockTransactionApi)
            .getTransaction(eq(getTransactionRequest), any())

        sut.getTransaction(historicalKinTransaction.transactionHash).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.SDKUpgradeRequired)

            verify(mockTransactionApi)
                .getTransaction(eq(getTransactionRequest), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun buildAndSignTransaction_success_no_memo() {
        val expected = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAA" +
                    "AAAAAAAAAABAAAAAQAAAABdxfpblHH9TF1YUDVTOYZmBQBjvIdgy+cL+Kwhwhjv+wAAAAEAAAAAIQ" +
                    "cuqQ76FFMsBE6P9dhaJfbYuXBrsX8Q38U9xreKIC8AAAAAAAAAAAC7ruAAAAAAAAAAAcIY7/sAAAB" +
                    "A27VS3THSpBX3dcKDPOv0zr4sgdnpFYVOyMC51O8ck0JMARp34pMywwRNaU09+RS46hfyuVBQTblk" +
                    "VMBs4LxpBg==",
            KinTransaction.RecordType.InFlight(System.currentTimeMillis())
        )
        val sourceAccount =
            TestUtils.fromSecretSeed("SDFDPC5VK7FSFDH4Q3CQPQRA4OPFXYM6CFRXVQOA767OGXFYBEDEQGMF")
                .updateStatus(KinAccount.Status.Registered(16576250185252864))
        val destinationAccount =
            TestUtils.fromAccountId("GAQQOLVJB35BIUZMARHI75OYLIS7NWFZOBV3C7YQ37CT3RVXRIQC6CXN")

        sut.buildAndSignTransaction(
            sourceAccount.key as Key.PrivateKey,
            sourceAccount.key.asPublicKey(),
            (sourceAccount.status as KinAccount.Status.Registered).sequence,
            listOf(KinPaymentItem(KinAmount(123), destinationAccount.id)),
            KinMemo.NONE,
            QuarkAmount(100)
        ).test {
            assertNull(error)
            assertNotNull(value)
            println(Base64.encodeBase64String(value.bytesValue))
            assertTrue(expected.bytesValue.contentEquals(value.bytesValue))
            assertTrue(value.recordType is KinTransaction.RecordType.InFlight)

            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun buildAndSignTransaction_success_no_memo_kin2() {

        sut = KinServiceImpl(
            NetworkEnvironment.KinStellarTestNetKin2,
            NetworkOperationsHandlerImpl(logger = logger),
            mockAccountApi,
            mockTransactionApi,
            mockStreamingApi,
            mockAccountCreationApi,
            mockTransactionWhitelistingApi,
            logger
        )

        val sourceAccount =
            TestUtils.fromSecretSeed("SDFDPC5VK7FSFDH4Q3CQPQRA4OPFXYM6CFRXVQOA767OGXFYBEDEQGMF")
                .updateStatus(KinAccount.Status.Registered(16576250185252864))
        val destinationAccount =
            TestUtils.fromAccountId("GAQQOLVJB35BIUZMARHI75OYLIS7NWFZOBV3C7YQ37CT3RVXRIQC6CXN")

        sut.buildAndSignTransaction(
            sourceAccount.key as Key.PrivateKey,
            sourceAccount.key.asPublicKey(),
            (sourceAccount.status as KinAccount.Status.Registered).sequence,
            listOf(KinPaymentItem(KinAmount(123), destinationAccount.id)),
            KinMemo.NONE,
            QuarkAmount(100)
        ).test {
            assertNull(error)
            assertNotNull(value)
            assertEquals(QuarkAmount(100*100), value.fee)
            assertEquals(KinAmount(123*100), value.paymentOperations.map { it.amount }.reduce { acc, kinAmount -> acc + kinAmount })
            assertEquals(KinAmount(123), value.asKinPayments().map { it.amount }.reduce { acc, kinAmount -> acc + kinAmount })
            assertTrue(value.recordType is KinTransaction.RecordType.InFlight)

            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun buildAndSignTransaction_success_with_memo() {
        val expected = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAA" +
                    "AAAAEAAAADb2hpAAAAAAEAAAABAAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAA" +
                    "AQAAAAAhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogLwAAAAAAAAAAALuu4AAAAAAAAAABwh" +
                    "jv+wAAAEDpSBbgeceq6/vcEh3/blqn0qNYo4q8DLHes4DADOPzunvSjREWBeKJC9SdaKhtCnrcv3J0" +
                    "4V1MVmdN5iDQ5HcP",
            KinTransaction.RecordType.InFlight(System.currentTimeMillis())
        )
        val sourceAccount =
            TestUtils.fromSecretSeed("SDFDPC5VK7FSFDH4Q3CQPQRA4OPFXYM6CFRXVQOA767OGXFYBEDEQGMF")
                .updateStatus(KinAccount.Status.Registered(16576250185252864))
        val destinationAccount =
            TestUtils.fromAccountId("GAQQOLVJB35BIUZMARHI75OYLIS7NWFZOBV3C7YQ37CT3RVXRIQC6CXN")

        sut.buildAndSignTransaction(
            sourceAccount.key as Key.PrivateKey,
            sourceAccount.key.asPublicKey(),
            (sourceAccount.status as KinAccount.Status.Registered).sequence,
            listOf(KinPaymentItem(KinAmount(123), destinationAccount.id)),
            KinMemo("ohi"),
            QuarkAmount(100)
        ).test {
            assertNull(error)
            assertNotNull(value)
            println(Base64.encodeBase64String(value.bytesValue))
            assertTrue(expected.bytesValue.contentEquals(value.bytesValue))
            assertTrue(value.recordType is KinTransaction.RecordType.InFlight)

            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun buildAndSignTransaction_success_with_agoraMemo_andInvoices() {
        val sourceAccount =
            TestUtils.fromSecretSeed("SDFDPC5VK7FSFDH4Q3CQPQRA4OPFXYM6CFRXVQOA767OGXFYBEDEQGMF")
                .updateStatus(KinAccount.Status.Registered(16576250185252864))
        val destinationAccount =
            TestUtils.fromAccountId("GAQQOLVJB35BIUZMARHI75OYLIS7NWFZOBV3C7YQ37CT3RVXRIQC6CXN")

        val expectedMemo =
            KinMemo(Base64().decode("QQAAtPJbea1Ask4zS+JHdc2BVznFYve1NAZshGi1PwI=")!!)
        val destinationKinAppIdx = AppIdx.TEST_APP_IDX
        val invoice = Invoice.Builder()
            .addLineItem(
                LineItem.Builder("thing1", KinAmount(123))
                    .build()
            )
            .build()
        val invoiceList = InvoiceList.Builder().addInvoice(invoice).build()

        val expected = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAAAAAANBAAC08lt5rUCyTjNL4kd1zYFXOcVi97U0BmyEaLU/AgAAAAEAAAABAAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAAQAAAAAhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogLwAAAAAAAAAAALuu4AAAAAAAAAABwhjv+wAAAEDnaKu4YLfDtfy/By58PaZPQ9GcxPxKehKwdyvV7F7QdDOKEOApD0XNiXIZpaNCff1m+UTirpefcBF7ZUuIaVcP",
            KinTransaction.RecordType.InFlight(System.currentTimeMillis()),
            invoiceList = invoiceList
        )

        sut.buildAndSignTransaction(
            sourceAccount.key as Key.PrivateKey,
            sourceAccount.key.asPublicKey(),
            (sourceAccount.status as KinAccount.Status.Registered).sequence,
            listOf(KinPaymentItem(KinAmount(123), destinationAccount.id, Optional.of(invoice))),
            KinBinaryMemo.Builder(destinationKinAppIdx.value)
                .setForeignKey(invoiceList.id.invoiceHash.decode())
                .setTranferType(KinBinaryMemo.TransferType.Spend)
                .build()
                .toKinMemo(),
            QuarkAmount(100)
        ).test(timeout = 100) {
            assertNull(error)
            assertNotNull(value)
            println(Base64.encodeBase64String(value.bytesValue))
            assertTrue(expected.bytesValue.contentEquals(value.bytesValue))
            assertTrue(value.recordType is KinTransaction.RecordType.InFlight)
            assertEquals(expectedMemo, value.memo)
            assertEquals(invoice, value.asKinPayments().first().invoice)

            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

//    These tests may not be relevant anymore
//    @Test
//    fun buildAndSignTransaction_account_null() {
//        val sourceAccount =
//            TestUtils.fromAccountId("GAQQOLVJB35BIUZMARHI75OYLIS7NWFZOBV3C7YQ37CT3RVXRIQC6CXN")
//                .updateStatus(KinAccount.Status.Registered(16576250185252864))
//        val destinationAccount =
//            TestUtils.fromAccountId("GAQQOLVJB35BIUZMARHI75OYLIS7NWFZOBV3C7YQ37CT3RVXRIQC6CXN")
//
//        sut.buildAndSignTransaction(
//            sourceAccount.key as Key.PrivateKey,
//            sourceAccount.key.asPublicKey(),
//            (sourceAccount.status as KinAccount.Status.Registered).sequence,
//            listOf(KinPaymentItem(KinAmount(123), destinationAccount.id)),
//            KinMemo.NONE,
//            QuarkAmount(100)
//        ).test {
//            assertTrue(error is KinService.FatalError.IllegalRequest)
//            assertNull(value)
//
//            verifyNoMoreInteractions(mockTransactionApi)
//            verifyZeroInteractions(mockAccountApi)
//        }
//    }
//
//    @Test
//    fun buildAndSignTransaction_source_acount_not_eligible_for_signing() {
//        val sourceAccount = TestUtils.newKinAccount()
//            .updateStatus(KinAccount.Status.Registered(16576250185252864))
//        val destinationAccount =
//            TestUtils.fromAccountId("GAQQOLVJB35BIUZMARHI75OYLIS7NWFZOBV3C7YQ37CT3RVXRIQC6CXN")
//
//        sut.buildAndSignTransaction(
//            sourceAccount.key as Key.PrivateKey,
//            sourceAccount.key.asPublicKey(),
//            (sourceAccount.status as KinAccount.Status.Registered).sequence,
//            listOf(KinPaymentItem(KinAmount(123), destinationAccount.id)),
//            KinMemo.NONE,
//            QuarkAmount(100)
//        ).test {
//            assertTrue { error is KinService.FatalError.IllegalRequest }
//            assertNull(value)
//
//            verifyNoMoreInteractions(mockTransactionApi)
//            verifyZeroInteractions(mockAccountApi)
//        }
//    }

    @Test
    fun submitTransaction_success() {
        val signedTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAAAAAAAA\n" +
                    "AAABAAAAAAAAAAEAAAAAIQcuqQ76FFMsBE6P9dhaJfbYuXBrsX8Q38U9xreKIC8AAAAAAAAAAAC7\n" +
                    "ruAAAAAAAAAAAcIY7/sAAABA6Qs1HI1B40fJNBc0RR0R7WfLDqKgniTGcT7yWa5ogAlEHwIuX54f\n" +
                    "HPv+sqKmCXa9JRadOmnPxi0/24UGFuUrDw==",
            KinTransaction.RecordType.InFlight(System.currentTimeMillis())
        )
        val signedTransactionBase64String =
            Base64.encodeBase64String(signedTransaction.bytesValue)!!
        val submitTransactionRequest =
            KinTransactionApi.SubmitTransactionRequest(signedTransaction.bytesValue)
        val expected = TestUtils.kinTransactionFromXdr(
            signedTransactionBase64String,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
            )
        )

        doAnswer {
            val respond =
                it.getArgument<(SubmitTransactionResponse) -> Unit>(1)
            respond(
                SubmitTransactionResponse(
                    SubmitTransactionResponse.Result.Ok,
                    expected
                )
            )
        }.whenever(mockTransactionApi)
            .submitTransaction(eq(submitTransactionRequest), any())

        sut.submitTransaction(signedTransaction).test {
            assertNull(error)
            assertNotNull(value)
            assertTrue { expected.bytesValue.contentEquals(value.bytesValue) }

            verify(mockTransactionApi)
                .submitTransaction(eq(submitTransactionRequest), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun submitTransaction_withInvoice_success() {
        val invoiceList = InvoiceList.Builder()
            .addInvoice(
                Invoice.Builder()
                    .addLineItem(
                        LineItem.Builder("title", KinAmount(123))
                            .build()
                    )
                    .build()
            ).build()
        val signedTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAAAAAAAA\n" +
                    "AAABAAAAAAAAAAEAAAAAIQcuqQ76FFMsBE6P9dhaJfbYuXBrsX8Q38U9xreKIC8AAAAAAAAAAAC7\n" +
                    "ruAAAAAAAAAAAcIY7/sAAABA6Qs1HI1B40fJNBc0RR0R7WfLDqKgniTGcT7yWa5ogAlEHwIuX54f\n" +
                    "HPv+sqKmCXa9JRadOmnPxi0/24UGFuUrDw==",
            KinTransaction.RecordType.InFlight(System.currentTimeMillis()),
            invoiceList = invoiceList
        )
        val signedTransactionBase64String =
            Base64.encodeBase64String(signedTransaction.bytesValue)!!
        val submitTransactionRequest =
            KinTransactionApi.SubmitTransactionRequest(signedTransaction.bytesValue, invoiceList)
        val expected = TestUtils.kinTransactionFromXdr(
            signedTransactionBase64String,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
            ),
            invoiceList = invoiceList
        )

        doAnswer {
            val respond =
                it.getArgument<(SubmitTransactionResponse) -> Unit>(1)
            respond(
                SubmitTransactionResponse(
                    SubmitTransactionResponse.Result.Ok,
                    expected
                )
            )
        }.whenever(mockTransactionApi)
            .submitTransaction(eq(submitTransactionRequest), any())

        sut.submitTransaction(signedTransaction).test(timeout = 1000) {
            assertNull(error)
            assertNotNull(value)
            assertTrue { expected.bytesValue.contentEquals(value.bytesValue) }

            assertEquals(invoiceList.invoices.first(), value.asKinPayments().first().invoice)

            verify(mockTransactionApi)
                .submitTransaction(eq(submitTransactionRequest), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun submitTransaction_whitelisted_success() {
        val signedTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAAAAAAAA\n" +
                    "AAABAAAAAAAAAAEAAAAAIQcuqQ76FFMsBE6P9dhaJfbYuXBrsX8Q38U9xreKIC8AAAAAAAAAAAC7\n" +
                    "ruAAAAAAAAAAAcIY7/sAAABA6Qs1HI1B40fJNBc0RR0R7WfLDqKgniTGcT7yWa5ogAlEHwIuX54f\n" +
                    "HPv+sqKmCXa9JRadOmnPxi0/24UGFuUrDw==",
            KinTransaction.RecordType.InFlight(System.currentTimeMillis())
        )
        val signedTransactionBase64String =
            Base64.encodeBase64String(signedTransaction.bytesValue)!!
        val submitTransactionRequest =
            KinTransactionApi.SubmitTransactionRequest(signedTransaction.bytesValue)
        val expected = TestUtils.kinTransactionFromXdr(
            signedTransactionBase64String,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
            )
        )

        doAnswer {
            true
        }.whenever(mockTransactionWhitelistingApi).isWhitelistingAvailable

        doAnswer {
            val callback: (WhitelistTransactionResponse) -> Unit = it.getArgument(1)
            callback(
                WhitelistTransactionResponse(
                    WhitelistTransactionResponse.Result.Ok,
                    signedTransactionBase64String
                )
            )
        }.whenever(mockTransactionWhitelistingApi)
            .whitelistTransaction(
                eq(WhitelistTransactionRequest(signedTransactionBase64String)),
                any()
            )

        doAnswer {
            val respond =
                it.getArgument<(SubmitTransactionResponse) -> Unit>(1)
            respond(
                SubmitTransactionResponse(
                    SubmitTransactionResponse.Result.Ok,
                    expected
                )
            )
        }.whenever(mockTransactionApi)
            .submitTransaction(eq(submitTransactionRequest), any())

        sut.submitTransaction(signedTransaction).test {
            assertNull(error)
            assertNotNull(value)
            assertTrue { expected.bytesValue.contentEquals(value.bytesValue) }

            verify(mockTransactionApi)
                .submitTransaction(eq(submitTransactionRequest), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun submitTransaction_whitelistingFailed_soPassThrough() {
        val signedTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAAAAAAAA\n" +
                    "AAABAAAAAAAAAAEAAAAAIQcuqQ76FFMsBE6P9dhaJfbYuXBrsX8Q38U9xreKIC8AAAAAAAAAAAC7\n" +
                    "ruAAAAAAAAAAAcIY7/sAAABA6Qs1HI1B40fJNBc0RR0R7WfLDqKgniTGcT7yWa5ogAlEHwIuX54f\n" +
                    "HPv+sqKmCXa9JRadOmnPxi0/24UGFuUrDw==",
            KinTransaction.RecordType.InFlight(System.currentTimeMillis())
        )
        val signedTransactionBase64String =
            Base64.encodeBase64String(signedTransaction.bytesValue)!!
        val submitTransactionRequest =
            KinTransactionApi.SubmitTransactionRequest(signedTransaction.bytesValue)
        val expected = TestUtils.kinTransactionFromXdr(
            signedTransactionBase64String,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
            )
        )

        doAnswer {
            true
        }.whenever(mockTransactionWhitelistingApi).isWhitelistingAvailable

        doAnswer {
            val callback: (WhitelistTransactionResponse) -> Unit = it.getArgument(1)
            callback(
                WhitelistTransactionResponse(
                    WhitelistTransactionResponse.Result.WhitelistingDisabled,
                    signedTransactionBase64String
                )
            )
        }.whenever(mockTransactionWhitelistingApi)
            .whitelistTransaction(
                eq(WhitelistTransactionRequest(signedTransactionBase64String)),
                any()
            )

        doAnswer {
            val respond =
                it.getArgument<(SubmitTransactionResponse) -> Unit>(1)
            respond(
                SubmitTransactionResponse(
                    SubmitTransactionResponse.Result.Ok,
                    expected
                )
            )
        }.whenever(mockTransactionApi)
            .submitTransaction(eq(submitTransactionRequest), any())

        sut.submitTransaction(signedTransaction).test {
            assertNull(error)
            assertNotNull(value)
            assertTrue { expected.bytesValue.contentEquals(value.bytesValue) }

            verify(mockTransactionApi)
                .submitTransaction(eq(submitTransactionRequest), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun submitTransaction_bad_sequence_number() {
        val signedTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAAAAAAAA\n" +
                    "AAABAAAAAAAAAAEAAAAAIQcuqQ76FFMsBE6P9dhaJfbYuXBrsX8Q38U9xreKIC8AAAAAAAAAAAC7\n" +
                    "ruAAAAAAAAAAAcIY7/sAAABA6Qs1HI1B40fJNBc0RR0R7WfLDqKgniTGcT7yWa5ogAlEHwIuX54f\n" +
                    "HPv+sqKmCXa9JRadOmnPxi0/24UGFuUrDw==",
            KinTransaction.RecordType.InFlight(System.currentTimeMillis())
        )
        val submitTransactionRequest =
            KinTransactionApi.SubmitTransactionRequest(signedTransaction.bytesValue)

        doAnswer {
            val respond =
                it.getArgument<(SubmitTransactionResponse) -> Unit>(1)
            respond(
                SubmitTransactionResponse(
                    SubmitTransactionResponse.Result.BadSequenceNumber
                )
            )
        }.whenever(mockTransactionApi)
            .submitTransaction(eq(submitTransactionRequest), any())

        sut.submitTransaction(signedTransaction).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.BadSequenceNumberInRequest)

            verify(mockTransactionApi)
                .submitTransaction(eq(submitTransactionRequest), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun submitTransaction_insufficient_fee() {
        val signedTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAAAAAAAA\n" +
                    "AAABAAAAAAAAAAEAAAAAIQcuqQ76FFMsBE6P9dhaJfbYuXBrsX8Q38U9xreKIC8AAAAAAAAAAAC7\n" +
                    "ruAAAAAAAAAAAcIY7/sAAABA6Qs1HI1B40fJNBc0RR0R7WfLDqKgniTGcT7yWa5ogAlEHwIuX54f\n" +
                    "HPv+sqKmCXa9JRadOmnPxi0/24UGFuUrDw==",
            KinTransaction.RecordType.InFlight(System.currentTimeMillis())
        )
        val submitTransactionRequest =
            KinTransactionApi.SubmitTransactionRequest(signedTransaction.bytesValue)

        doAnswer {
            val respond =
                it.getArgument<(SubmitTransactionResponse) -> Unit>(1)
            respond(
                SubmitTransactionResponse(
                    SubmitTransactionResponse.Result.InsufficientFee
                )
            )
        }.whenever(mockTransactionApi)
            .submitTransaction(eq(submitTransactionRequest), any())

        sut.submitTransaction(signedTransaction).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.InsufficientFeeInRequest)

            verify(mockTransactionApi)
                .submitTransaction(eq(submitTransactionRequest), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun submitTransaction_insufficient_balance() {
        val signedTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAAAAAAAA\n" +
                    "AAABAAAAAAAAAAEAAAAAIQcuqQ76FFMsBE6P9dhaJfbYuXBrsX8Q38U9xreKIC8AAAAAAAAAAAC7\n" +
                    "ruAAAAAAAAAAAcIY7/sAAABA6Qs1HI1B40fJNBc0RR0R7WfLDqKgniTGcT7yWa5ogAlEHwIuX54f\n" +
                    "HPv+sqKmCXa9JRadOmnPxi0/24UGFuUrDw==",
            KinTransaction.RecordType.InFlight(System.currentTimeMillis())
        )
        val submitTransactionRequest =
            KinTransactionApi.SubmitTransactionRequest(signedTransaction.bytesValue)

        doAnswer {
            val respond =
                it.getArgument<(SubmitTransactionResponse) -> Unit>(1)
            respond(
                SubmitTransactionResponse(
                    SubmitTransactionResponse.Result.InsufficientBalance
                )
            )
        }.whenever(mockTransactionApi)
            .submitTransaction(eq(submitTransactionRequest), any())

        sut.submitTransaction(signedTransaction).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.InsufficientBalanceForSourceAccountInRequest)

            verify(mockTransactionApi)
                .submitTransaction(eq(submitTransactionRequest), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun submitTransaction_no_account() {
        val signedTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAAAAAAAA\n" +
                    "AAABAAAAAAAAAAEAAAAAIQcuqQ76FFMsBE6P9dhaJfbYuXBrsX8Q38U9xreKIC8AAAAAAAAAAAC7\n" +
                    "ruAAAAAAAAAAAcIY7/sAAABA6Qs1HI1B40fJNBc0RR0R7WfLDqKgniTGcT7yWa5ogAlEHwIuX54f\n" +
                    "HPv+sqKmCXa9JRadOmnPxi0/24UGFuUrDw==",
            KinTransaction.RecordType.InFlight(System.currentTimeMillis())
        )
        val submitTransactionRequest =
            KinTransactionApi.SubmitTransactionRequest(signedTransaction.bytesValue)

        doAnswer {
            val respond =
                it.getArgument<(SubmitTransactionResponse) -> Unit>(1)
            respond(
                SubmitTransactionResponse(
                    SubmitTransactionResponse.Result.NoAccount
                )
            )
        }.whenever(mockTransactionApi)
            .submitTransaction(eq(submitTransactionRequest), any())

        sut.submitTransaction(signedTransaction).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.UnknownAccountInRequest)

            verify(mockTransactionApi)
                .submitTransaction(eq(submitTransactionRequest), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun submitTransaction_transient_failure() {
        val signedTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAAAAAAAA\n" +
                    "AAABAAAAAAAAAAEAAAAAIQcuqQ76FFMsBE6P9dhaJfbYuXBrsX8Q38U9xreKIC8AAAAAAAAAAAC7\n" +
                    "ruAAAAAAAAAAAcIY7/sAAABA6Qs1HI1B40fJNBc0RR0R7WfLDqKgniTGcT7yWa5ogAlEHwIuX54f\n" +
                    "HPv+sqKmCXa9JRadOmnPxi0/24UGFuUrDw==",
            KinTransaction.RecordType.InFlight(System.currentTimeMillis())
        )
        val submitTransactionRequest =
            KinTransactionApi.SubmitTransactionRequest(signedTransaction.bytesValue)

        doAnswer {
            val respond =
                it.getArgument<(SubmitTransactionResponse) -> Unit>(1)
            respond(
                SubmitTransactionResponse(
                    SubmitTransactionResponse.Result.TransientFailure(Exception())
                )
            )
        }.whenever(mockTransactionApi)
            .submitTransaction(eq(submitTransactionRequest), any())

        sut.submitTransaction(signedTransaction).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.TransientFailure)

            verify(mockTransactionApi)
                .submitTransaction(eq(submitTransactionRequest), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun submitTransaction_undefinied_failure() {
        val signedTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAAAAAAAA\n" +
                    "AAABAAAAAAAAAAEAAAAAIQcuqQ76FFMsBE6P9dhaJfbYuXBrsX8Q38U9xreKIC8AAAAAAAAAAAC7\n" +
                    "ruAAAAAAAAAAAcIY7/sAAABA6Qs1HI1B40fJNBc0RR0R7WfLDqKgniTGcT7yWa5ogAlEHwIuX54f\n" +
                    "HPv+sqKmCXa9JRadOmnPxi0/24UGFuUrDw==",
            KinTransaction.RecordType.InFlight(System.currentTimeMillis())
        )
        val submitTransactionRequest =
            KinTransactionApi.SubmitTransactionRequest(signedTransaction.bytesValue)

        doAnswer {
            val respond =
                it.getArgument<(SubmitTransactionResponse) -> Unit>(1)
            respond(
                SubmitTransactionResponse(
                    SubmitTransactionResponse.Result.UndefinedError(Exception())
                )
            )
        }.whenever(mockTransactionApi)
            .submitTransaction(eq(submitTransactionRequest), any())

        sut.submitTransaction(signedTransaction).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.UnexpectedServiceError)

            verify(mockTransactionApi)
                .submitTransaction(eq(submitTransactionRequest), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun submitTransaction_SDKUpgradeRequired() {
        val signedTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAAAAAAAA\n" +
                    "AAABAAAAAAAAAAEAAAAAIQcuqQ76FFMsBE6P9dhaJfbYuXBrsX8Q38U9xreKIC8AAAAAAAAAAAC7\n" +
                    "ruAAAAAAAAAAAcIY7/sAAABA6Qs1HI1B40fJNBc0RR0R7WfLDqKgniTGcT7yWa5ogAlEHwIuX54f\n" +
                    "HPv+sqKmCXa9JRadOmnPxi0/24UGFuUrDw==",
            KinTransaction.RecordType.InFlight(System.currentTimeMillis())
        )
        val submitTransactionRequest =
            KinTransactionApi.SubmitTransactionRequest(signedTransaction.bytesValue)

        doAnswer {
            val respond =
                it.getArgument<(SubmitTransactionResponse) -> Unit>(1)
            respond(
                SubmitTransactionResponse(
                    SubmitTransactionResponse.Result.UpgradeRequiredError
                )
            )
        }.whenever(mockTransactionApi)
            .submitTransaction(eq(submitTransactionRequest), any())

        sut.submitTransaction(signedTransaction).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.SDKUpgradeRequired)

            verify(mockTransactionApi)
                .submitTransaction(eq(submitTransactionRequest), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun submitTransaction_equality() {
        val request1 =
            KinTransactionApi.SubmitTransactionRequest(
                byteArrayOf(1, 2, 3),
                InvoiceList.Builder().addInvoice(
                    Invoice.Builder()
                        .addLineItem(
                            LineItem.Builder("aTitle", KinAmount(123)).build()
                        ).build()
                ).build()
            )

        val request2 =
            KinTransactionApi.SubmitTransactionRequest(
                byteArrayOf(1, 2, 3),
                InvoiceList.Builder().addInvoice(
                    Invoice.Builder()
                        .addLineItem(
                            LineItem.Builder("aTitle", KinAmount(123)).build()
                        ).build()
                ).build()
            )
        val request3 =
            KinTransactionApi.SubmitTransactionRequest(
                byteArrayOf(4, 5, 6),
                InvoiceList.Builder().addInvoice(
                    Invoice.Builder()
                        .addLineItem(
                            LineItem.Builder("aTitle2", KinAmount(456)).build()
                        ).build()
                ).build()
            )

        assertEquals(request1, request2)
        assertEquals(request1.hashCode(), request2.hashCode())
        assertNotEquals(request1, request3)
        assertNotEquals(request1.hashCode(), request3.hashCode())
    }

    @Test
    fun getMinFee_success() {

        doAnswer {
            it.getArgument<(GetMinFeeForTransactionResponse) -> Unit>(0)(
                GetMinFeeForTransactionResponse(
                    GetMinFeeForTransactionResponse.Result.Ok,
                    QuarkAmount(0)
                )
            )
        }.whenever(mockTransactionApi).getTransactionMinFee(any())

        sut.getMinFee().test {
            assertEquals(value, QuarkAmount(0))
        }
    }

    @Test
    fun getMinFee_illegalResponse() {

        doAnswer {
            it.getArgument<(GetMinFeeForTransactionResponse) -> Unit>(0)(
                GetMinFeeForTransactionResponse(
                    GetMinFeeForTransactionResponse.Result.Ok,
                    null
                )
            )
        }.whenever(mockTransactionApi).getTransactionMinFee(any())

        sut.getMinFee().test {
            assertEquals(value, null)
            assertNotNull(error)
            assertTrue { error is KinService.FatalError.IllegalResponse }
        }
    }

    @Test
    fun getMinFee_transient_failure() {

        doAnswer {
            it.getArgument<(GetMinFeeForTransactionResponse) -> Unit>(0)(
                GetMinFeeForTransactionResponse(
                    GetMinFeeForTransactionResponse.Result.TransientFailure(Exception()),
                    null
                )
            )
        }.whenever(mockTransactionApi).getTransactionMinFee(any())

        sut.getMinFee().test {
            assertEquals(value, null)
            assertNotNull(error)
            assertTrue { error is KinService.FatalError.IllegalRequest }
        }
    }

    @Test
    fun getMinFee_undefined_failure() {

        doAnswer {
            it.getArgument<(GetMinFeeForTransactionResponse) -> Unit>(0)(
                GetMinFeeForTransactionResponse(
                    GetMinFeeForTransactionResponse.Result.UndefinedError(Exception()),
                    null
                )
            )
        }.whenever(mockTransactionApi).getTransactionMinFee(any())

        sut.getMinFee().test {
            assertEquals(value, null)
            assertNotNull(error)
            assertTrue { error is KinService.FatalError.IllegalRequest }
        }
    }

    @Test
    fun getMinFee_undefined_SDKUpgradeRequired() {

        doAnswer {
            it.getArgument<(GetMinFeeForTransactionResponse) -> Unit>(0)(
                GetMinFeeForTransactionResponse(
                    GetMinFeeForTransactionResponse.Result.UpgradeRequiredError
                )
            )
        }.whenever(mockTransactionApi).getTransactionMinFee(any())

        sut.getMinFee().test {
            assertEquals(value, null)
            assertNotNull(error)
            assertTrue { error is KinService.FatalError.SDKUpgradeRequired }
        }
    }

    @Test
    fun testStreamAccount() {
        val account = TestUtils.newKinAccount()
        val observer = ValueSubject<KinAccount>()

        doAnswer {
            observer
        }.whenever(mockStreamingApi).streamAccount(eq(account.id))

        observer.onNext(account)

        sut.streamAccount(account.id).test {
            assertEquals(account, value)
        }
    }

    @Test
    fun testStreamTransactions() {
        val account = TestUtils.newKinAccount()
        val observer = ValueSubject<KinTransaction>()

        doAnswer {
            observer
        }.whenever(mockStreamingApi).streamNewTransactions(eq(account.id))

        val values = mutableListOf<KinTransaction>()
        val latch = CountDownLatch(2)
        sut.streamNewTransactions(account.id).add {
            values.add(it)
            latch.countDown()
        }

        observer.onNext(historicalKinTransaction)
        observer.onNext(historicalKinTransaction2)
        latch.await(5, TimeUnit.SECONDS)
        assertEquals(listOf(historicalKinTransaction, historicalKinTransaction2), values)
    }

    @Test
    fun getTestService() {
        assertNotNull(sut.testService)
    }
}
