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
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinDateFormat
import org.kin.sdk.base.models.KinMemo
import org.kin.sdk.base.models.KinPaymentItem
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.network.api.KinAccountApi
import org.kin.sdk.base.network.api.KinAccountApi.GetAccountRequest
import org.kin.sdk.base.network.api.KinAccountApi.GetAccountResponse
import org.kin.sdk.base.network.api.KinAccountCreationApi
import org.kin.sdk.base.network.api.KinAccountCreationApi.CreateAccountRequest
import org.kin.sdk.base.network.api.KinAccountCreationApi.CreateAccountResponse
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
import org.kin.sdk.base.tools.ExecutorServices
import org.kin.sdk.base.tools.NetworkOperationsHandlerImpl
import org.kin.sdk.base.tools.TestUtils
import org.kin.sdk.base.tools.ValueSubject
import org.kin.sdk.base.tools.test
import org.kin.sdk.base.tools.updateStatus
import org.kin.stellarfork.codec.Base64
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KinServiceImplTest {

    private companion object {
        val account = TestUtils.newKinAccount()
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
    private lateinit var mockAccountCreationApi: KinAccountCreationApi
    private lateinit var mockTransactionWhitelistingApi: KinTransactionWhitelistingApi

    @Before
    fun setUp() {
        mockAccountApi = mock {}
        mockTransactionApi = mock {}
        mockAccountCreationApi = mock {}
        mockTransactionWhitelistingApi = mock {
            on { isWhitelistingAvailable } doReturn false
        }

        sut = KinServiceImpl(
            ApiConfig.TestNetHorizon.networkEnv,
            NetworkOperationsHandlerImpl(),
            mockAccountApi,
            mockTransactionApi,
            mockAccountCreationApi,
            mockTransactionWhitelistingApi
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

        sut.createAccount(account.id).test(100) {
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

        sut.createAccount(account.id).test {
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
            it.getArgument<(GetAccountResponse) -> Unit>(1).invoke(GetAccountResponse(GetAccountResponse.Result.Ok, registeredAccount))
        }.whenever(mockAccountApi).getAccount(eq(GetAccountRequest(registeredAccount.id)), any())

        sut.createAccount(account.id).test {
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

        sut.createAccount(account.id).test {
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

        sut.createAccount(account.id).test {
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

        sut.createAccount(account.id).test {
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

        sut.createAccount(account.id).test {
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

        sut.createAccount(account.id).test {
            assertTrue(error is KinService.SDKUpgradeRequired)
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
            assertTrue(error is KinService.SDKUpgradeRequired)

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
            val transactions = listOf(historicalKinTransaction)
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
            assertTrue(error is KinService.SDKUpgradeRequired)

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
            val transactions = listOf(historicalKinTransaction)
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
            assertTrue(error is KinService.SDKUpgradeRequired)

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
            assertTrue(error is KinService.SDKUpgradeRequired)

            verify(mockTransactionApi)
                .getTransaction(eq(getTransactionRequest), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun buildAndSignTransaction_success_no_memo() {
        val expected = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAAAAAAAA\n" +
                    "AAABAAAAAAAAAAEAAAAAIQcuqQ76FFMsBE6P9dhaJfbYuXBrsX8Q38U9xreKIC8AAAAAAAAAAAC7\n" +
                    "ruAAAAAAAAAAAcIY7/sAAABA6Qs1HI1B40fJNBc0RR0R7WfLDqKgniTGcT7yWa5ogAlEHwIuX54f\n" +
                    "HPv+sqKmCXa9JRadOmnPxi0/24UGFuUrDw==",
            KinTransaction.RecordType.InFlight(System.currentTimeMillis())
        )
        val sourceAccount =
            TestUtils.fromSecretSeed("SDFDPC5VK7FSFDH4Q3CQPQRA4OPFXYM6CFRXVQOA767OGXFYBEDEQGMF")
                .updateStatus(KinAccount.Status.Registered(16576250185252864))
        val destinationAccount =
            TestUtils.fromAccountId("GAQQOLVJB35BIUZMARHI75OYLIS7NWFZOBV3C7YQ37CT3RVXRIQC6CXN")

        sut.buildAndSignTransaction(
            sourceAccount,
            listOf(KinPaymentItem(KinAmount(123), destinationAccount.id)),
            KinMemo.NONE,
            QuarkAmount(100)
        ).test {
            assertNull(error)
            assertNotNull(value)
            println(Base64.encodeBase64String(value.envelopeXdrBytes))
            assertTrue(expected.envelopeXdrBytes.contentEquals(value.envelopeXdrBytes))
            assertTrue(value.recordType is KinTransaction.RecordType.InFlight)

            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun buildAndSignTransaction_success_with_memo() {
        val expected = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAA" +
                    "AAAAAEAAAADb2hpAAAAAAEAAAAAAAAAAQAAAAAhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4" +
                    "ogLwAAAAAAAAAAALuu4AAAAAAAAAABwhjv+wAAAEA48MSchM8GZ5sRgBWKKw1SgCmTKwSpsiMuen2" +
                    "zkc2mmSnm1t8uAzvQIFpWm/YUXI+1HaH9VKdR7tBz6LXgHAgN",
            KinTransaction.RecordType.InFlight(System.currentTimeMillis())
        )
        val sourceAccount =
            TestUtils.fromSecretSeed("SDFDPC5VK7FSFDH4Q3CQPQRA4OPFXYM6CFRXVQOA767OGXFYBEDEQGMF")
                .updateStatus(KinAccount.Status.Registered(16576250185252864))
        val destinationAccount =
            TestUtils.fromAccountId("GAQQOLVJB35BIUZMARHI75OYLIS7NWFZOBV3C7YQ37CT3RVXRIQC6CXN")

        sut.buildAndSignTransaction(
            sourceAccount,
            listOf(KinPaymentItem(KinAmount(123), destinationAccount.id)),
            KinMemo("ohi"),
            QuarkAmount(100)
        ).test {
            assertNull(error)
            assertNotNull(value)
            println(Base64.encodeBase64String(value.envelopeXdrBytes))
            assertTrue(expected.envelopeXdrBytes.contentEquals(value.envelopeXdrBytes))
            assertTrue(value.recordType is KinTransaction.RecordType.InFlight)

            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun buildAndSignTransaction_account_null() {
        val sourceAccount =
            TestUtils.fromAccountId("GAQQOLVJB35BIUZMARHI75OYLIS7NWFZOBV3C7YQ37CT3RVXRIQC6CXN")
                .updateStatus(KinAccount.Status.Registered(16576250185252864))
        val destinationAccount =
            TestUtils.fromAccountId("GAQQOLVJB35BIUZMARHI75OYLIS7NWFZOBV3C7YQ37CT3RVXRIQC6CXN")

        sut.buildAndSignTransaction(
            sourceAccount,
            listOf(KinPaymentItem(KinAmount(123), destinationAccount.id)),
            KinMemo.NONE,
            QuarkAmount(100)
        ).test {
            assertTrue(error is KinService.FatalError.IllegalRequest)
            assertNull(value)

            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun buildAndSignTransaction_source_acount_not_eligible_for_signing() {
        val sourceAccount = TestUtils.newKinAccount()
            .updateStatus(KinAccount.Status.Registered(16576250185252864))
        val destinationAccount =
            TestUtils.fromAccountId("GAQQOLVJB35BIUZMARHI75OYLIS7NWFZOBV3C7YQ37CT3RVXRIQC6CXN")

        sut.buildAndSignTransaction(
            sourceAccount,
            listOf(KinPaymentItem(KinAmount(123), destinationAccount.id)),
            KinMemo.NONE,
            QuarkAmount(100)
        ).test {
            assertTrue { error is KinService.FatalError.IllegalRequest }
            assertNull(value)

            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

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
            Base64.encodeBase64String(signedTransaction.envelopeXdrBytes)!!
        val submitTransactionRequest =
            KinTransactionApi.SubmitTransactionRequest(signedTransaction.envelopeXdrBytes)
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
            assertTrue { expected.envelopeXdrBytes.contentEquals(value.envelopeXdrBytes) }

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
            Base64.encodeBase64String(signedTransaction.envelopeXdrBytes)!!
        val submitTransactionRequest =
            KinTransactionApi.SubmitTransactionRequest(signedTransaction.envelopeXdrBytes)
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
            assertTrue { expected.envelopeXdrBytes.contentEquals(value.envelopeXdrBytes) }

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
            Base64.encodeBase64String(signedTransaction.envelopeXdrBytes)!!
        val submitTransactionRequest =
            KinTransactionApi.SubmitTransactionRequest(signedTransaction.envelopeXdrBytes)
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
            assertTrue { expected.envelopeXdrBytes.contentEquals(value.envelopeXdrBytes) }

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
            KinTransactionApi.SubmitTransactionRequest(signedTransaction.envelopeXdrBytes)

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
            assertTrue(error is KinService.FatalError.IllegalRequest)

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
            KinTransactionApi.SubmitTransactionRequest(signedTransaction.envelopeXdrBytes)

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
            assertTrue(error is KinService.FatalError.IllegalRequest)

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
            KinTransactionApi.SubmitTransactionRequest(signedTransaction.envelopeXdrBytes)

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
            assertTrue(error is KinService.FatalError.Denied)

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
            KinTransactionApi.SubmitTransactionRequest(signedTransaction.envelopeXdrBytes)

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
            assertTrue(error is KinService.FatalError.Denied)

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
            KinTransactionApi.SubmitTransactionRequest(signedTransaction.envelopeXdrBytes)

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
            KinTransactionApi.SubmitTransactionRequest(signedTransaction.envelopeXdrBytes)

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
            KinTransactionApi.SubmitTransactionRequest(signedTransaction.envelopeXdrBytes)

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
            assertTrue(error is KinService.SDKUpgradeRequired)

            verify(mockTransactionApi)
                .submitTransaction(eq(submitTransactionRequest), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
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
            assertTrue { error is KinService.SDKUpgradeRequired}
        }
    }

    @Test
    fun testStreamAccount() {
        val account = TestUtils.newKinAccount()
        val observer = ValueSubject<KinAccount>()

        doAnswer {
            observer
        }.whenever(mockAccountApi).streamAccount(eq(account.id))

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
        }.whenever(mockTransactionApi).streamNewTransactions(eq(account.id))

        ExecutorServices().sequentialScheduled.schedule({
            observer.onNext(historicalKinTransaction)
            observer.onNext(historicalKinTransaction)
        }, 100, TimeUnit.MILLISECONDS)

        sut.streamNewTransactions(account.id).test(2) {
            assertEquals(listOf(historicalKinTransaction, historicalKinTransaction), values)
        }
    }

    @Test
    fun getTestService() {
        assertNotNull(sut.testService)
    }
}
