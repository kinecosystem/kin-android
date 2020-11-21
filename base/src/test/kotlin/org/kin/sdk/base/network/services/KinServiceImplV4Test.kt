package org.kin.sdk.base.network.services

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.kin.sdk.base.models.AppIdx
import org.kin.sdk.base.models.Invoice
import org.kin.sdk.base.models.InvoiceList
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBinaryMemo
import org.kin.sdk.base.models.KinDateFormat
import org.kin.sdk.base.models.KinMemo
import org.kin.sdk.base.models.KinPaymentItem
import org.kin.sdk.base.models.LineItem
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.asKinAccountId
import org.kin.sdk.base.models.asKinPayments
import org.kin.sdk.base.models.asPrivateKey
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.models.solana.FixedByteArray32
import org.kin.sdk.base.models.solana.Hash
import org.kin.sdk.base.models.solana.SystemProgram
import org.kin.sdk.base.models.solana.TokenProgram
import org.kin.sdk.base.models.solana.Transaction
import org.kin.sdk.base.models.solana.unmarshal
import org.kin.sdk.base.models.toKeyPair
import org.kin.sdk.base.models.toSigningKeyPair
import org.kin.sdk.base.network.api.KinAccountApiV4
import org.kin.sdk.base.network.api.KinAccountCreationApiV4
import org.kin.sdk.base.network.api.KinStreamingApiV4
import org.kin.sdk.base.network.api.KinTransactionApiV4
import org.kin.sdk.base.stellar.models.ApiConfig
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.tools.KinLoggerFactoryImpl
import org.kin.sdk.base.tools.NetworkOperationsHandlerImpl
import org.kin.sdk.base.tools.Optional
import org.kin.sdk.base.tools.TestUtils
import org.kin.sdk.base.tools.ValueSubject
import org.kin.sdk.base.tools.sha256
import org.kin.sdk.base.tools.test
import org.kin.sdk.base.tools.updateStatus
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.codec.Base64
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KinServiceImplV4Test {
    private companion object {
        val account = TestUtils.newSigningKinAccount()

        val subsidizerId =
            TestUtils.fromAccountId("GDXSRIW5MB2FZ3XM6D6RKVVVO65JLQETBRUA6XQ4EYYHKRPHIFNWCVNC").id
        val minRentExemptionInLamports = 555L // would actually be size dependant in real life
        val tokenKey = TokenProgram.PROGRAM_KEY
        val mintKey = Key.PublicKey("GBJEHPZ5WWQ7QZZKARPYSZCKZ3ZOJQWYRGABSN6LAUNQRN7KBOQL5EXS")
        val recentBlockHash = Hash(FixedByteArray32())

        val registeredAccount = account.updateStatus(KinAccount.Status.Registered(1234))
        val createRequest = run {
            val subsidizer: Key.PublicKey = subsidizerId.toKeyPair().asPublicKey()

            val signer = account.toSigningKeyPair().asPrivateKey()
            val tokenAccountSeed = signer.toSigningKeyPair().rawSecretSeed!!.sha256()
            val tokenAccount = KeyPair.fromSecretSeed(tokenAccountSeed).asPrivateKey()
            val accountPub: Key.PublicKey = tokenAccount.asPublicKey()
            val owner = account.key.asPublicKey()

            val transaction = Transaction.newTransaction(
                subsidizer,
                SystemProgram.CreateAccount(
                    subsidizer = subsidizer,
                    address = accountPub,
                    owner = TokenProgram.PROGRAM_KEY,
                    lamports = minRentExemptionInLamports,
                    size = TokenProgram.accountSize
                ).instruction,
                TokenProgram.InitializeAccount(
                    account = accountPub,
                    mint = mintKey,
                    owner = owner,
                    programKey = tokenKey
                ).instruction,
                TokenProgram.SetAuthority(
                    account = accountPub,
                    currentAuthority = owner,
                    newAuthority = subsidizer,
                    authorityType = TokenProgram.AuthorityType.AuthorityCloseAccount,
                    programKey = tokenKey
                ).instruction
            ).copyAndSetRecentBlockhash(recentBlockHash)
                .copyAndSign(signer, tokenAccount)

            KinAccountCreationApiV4.CreateAccountRequest(transaction)
        }
        val getAccountRequest = KinAccountApiV4.GetAccountRequest(account.id)
        val resolveAccountRequest = KinAccountApiV4.ResolveTokenAccountsRequest(account.id)

        val pagingToken = KinTransaction.PagingToken("16576645322248192")
        val historicalKinTransaction =
            TestUtils.kinTransactionFromSolanaTransaction(
                "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
                KinTransaction.RecordType.Historical(
                    KinDateFormat("2019-12-12T21:32:43Z").timestamp,
                    Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                    pagingToken
                )
            )

        val historicalKinTransaction2 =
            TestUtils.kinTransactionFromSolanaTransaction(
                "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjTKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
                KinTransaction.RecordType.Historical(
                    KinDateFormat("2019-12-12T21:32:43Z").timestamp,
                    Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                    pagingToken
                )
            )

        val getTransactionHistoryRequestLatestPage =
            KinTransactionApiV4.GetTransactionHistoryRequest(account.id)
        val getTransactionHistoryRequestWithPagingToken =
            KinTransactionApiV4.GetTransactionHistoryRequest(account.id, pagingToken)

        val getTransactionRequest =
            KinTransactionApiV4.GetTransactionRequest(historicalKinTransaction.transactionHash)
    }

    private lateinit var sut: KinService

    private lateinit var mockAccountApi: KinAccountApiV4
    private lateinit var mockTransactionApi: KinTransactionApiV4
    private lateinit var mockStreamingApi: KinStreamingApiV4
    private lateinit var mockAccountCreationApi: KinAccountCreationApiV4

    @Before
    fun setUp() {
        mockAccountApi = mock {}
        mockTransactionApi = mock {
            on { getServiceConfig(any(), any()) } doAnswer {
                val respond =
                    it.getArgument<(KinTransactionApiV4.GetServiceConfigResponse) -> Unit>(1)
                respond(
                    KinTransactionApiV4.GetServiceConfigResponse(
                        KinTransactionApiV4.GetServiceConfigResponse.Result.Ok,
                        subsidizerId,
                        tokenKey.asKinAccountId(),
                        mintKey.asKinAccountId()
                    )
                )
            }
            on { getRecentBlockHash(any(), any()) } doAnswer {
                val respond =
                    it.getArgument<(KinTransactionApiV4.GetRecentBlockHashResponse) -> Unit>(1)
                respond(
                    KinTransactionApiV4.GetRecentBlockHashResponse(
                        KinTransactionApiV4.GetRecentBlockHashResponse.Result.Ok,
                        recentBlockHash
                    )
                )
            }
            on { getMinimumBalanceForRentExemption(any(), any()) } doAnswer {
                val respond =
                    it.getArgument<(KinTransactionApiV4.GetMinimumBalanceForRentExemptionResponse) -> Unit>(
                        1
                    )
                respond(
                    KinTransactionApiV4.GetMinimumBalanceForRentExemptionResponse(
                        KinTransactionApiV4.GetMinimumBalanceForRentExemptionResponse.Result.Ok,
                        minRentExemptionInLamports
                    )
                )
            }
        }

        doAnswer {
            val respond =
                it.getArgument<(KinTransactionApiV4.GetTransactionHistoryResponse) -> Unit>(1)
            val transactions = listOf(historicalKinTransaction)
            respond(
                KinTransactionApiV4.GetTransactionHistoryResponse(
                    KinTransactionApiV4.GetTransactionHistoryResponse.Result.Ok,
                    transactions
                )
            )
        }.whenever(mockTransactionApi)
            .getTransactionHistory(eq(getTransactionHistoryRequestLatestPage), any())

        mockStreamingApi = mock {}
        mockAccountCreationApi = mock {}

        val logger = KinLoggerFactoryImpl(true)

        sut = KinServiceImplV4(
            ApiConfig.TestNetHorizon.networkEnv,
            NetworkOperationsHandlerImpl(logger = logger),
            mockAccountApi,
            mockTransactionApi,
            mockStreamingApi,
            mockAccountCreationApi,
            logger,
        )
    }

    @Test
    fun createAccount_success() {
        doAnswer {
            val respond =
                it.getArgument<(KinAccountCreationApiV4.CreateAccountResponse) -> Unit>(1)
            respond(
                KinAccountCreationApiV4.CreateAccountResponse(
                    KinAccountCreationApiV4.CreateAccountResponse.Result.Ok,
                    registeredAccount
                )
            )
        }.whenever(mockAccountCreationApi).createAccount(eq(createRequest), any())

        sut.createAccount(account.id, account.key as Key.PrivateKey).test(100) {
            assertNull(error)
            assertEquals(registeredAccount, value)

            verify(mockAccountCreationApi).createAccount(eq(createRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verify(mockTransactionApi).getMinimumBalanceForRentExemption(any(),any())
            verify(mockTransactionApi).getServiceConfig(any(),any())
            verify(mockTransactionApi).getRecentBlockHash(any(),any())
            verifyZeroInteractions(mockTransactionApi)
        }
    }


    @Test
    fun createAccount_already_exists() {
        doAnswer {
            val respond =
                it.getArgument<(KinAccountCreationApiV4.CreateAccountResponse) -> Unit>(1)
            respond(
                KinAccountCreationApiV4.CreateAccountResponse(
                    KinAccountCreationApiV4.CreateAccountResponse.Result.Exists,
                    registeredAccount
                )
            )
        }.whenever(mockAccountCreationApi).createAccount(eq(createRequest), any())

        sut.createAccount(account.id, account.key as Key.PrivateKey).test {
            assertNull(error)
            assertEquals(registeredAccount, value)

            verify(mockAccountCreationApi).createAccount(eq(createRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verify(mockTransactionApi).getMinimumBalanceForRentExemption(any(),any())
            verify(mockTransactionApi).getServiceConfig(any(),any())
            verify(mockTransactionApi).getRecentBlockHash(any(),any())
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun createAccount_already_exists_missing_account() {
        doAnswer {
            val respond =
                it.getArgument<(KinAccountCreationApiV4.CreateAccountResponse) -> Unit>(1)
            respond(
                KinAccountCreationApiV4.CreateAccountResponse(
                    KinAccountCreationApiV4.CreateAccountResponse.Result.Exists
                )
            )
        }.whenever(mockAccountCreationApi).createAccount(eq(createRequest), any())

        doAnswer {
            it.getArgument<(KinAccountApiV4.GetAccountResponse) -> Unit>(1)
                .invoke(
                    KinAccountApiV4.GetAccountResponse(
                        KinAccountApiV4.GetAccountResponse.Result.Ok,
                        registeredAccount
                    )
                )
        }.whenever(mockAccountApi)
            .getAccount(eq(KinAccountApiV4.GetAccountRequest(registeredAccount.id)), any())

        sut.createAccount(account.id, account.key as Key.PrivateKey).test {
            assertNull(error)
            assertEquals(registeredAccount, value)

            verify(mockAccountCreationApi).createAccount(eq(createRequest), any())
            verify(mockAccountApi).getAccount(
                eq(KinAccountApiV4.GetAccountRequest(registeredAccount.id)),
                any()
            )
            verifyNoMoreInteractions(mockAccountApi)
            verify(mockTransactionApi).getMinimumBalanceForRentExemption(any(),any())
            verify(mockTransactionApi).getServiceConfig(any(),any())
            verify(mockTransactionApi).getRecentBlockHash(any(),any())
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun createAccount_malformed_response() {
        doAnswer {
            val respond =
                it.getArgument<(KinAccountCreationApiV4.CreateAccountResponse) -> Unit>(1)
            respond(
                KinAccountCreationApiV4.CreateAccountResponse(
                    KinAccountCreationApiV4.CreateAccountResponse.Result.Ok
                )
            )
        }.whenever(mockAccountCreationApi).createAccount(eq(createRequest), any())

        sut.createAccount(account.id, account.key as Key.PrivateKey).test {
            assertEquals(KinService.FatalError.IllegalResponse, error)
            assertNull(value)

            verify(mockAccountCreationApi).createAccount(eq(createRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verify(mockTransactionApi).getMinimumBalanceForRentExemption(any(),any())
            verify(mockTransactionApi).getServiceConfig(any(),any())
            verify(mockTransactionApi).getRecentBlockHash(any(),any())
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun createAccount_undefined_failure() {
        doAnswer {
            val respond =
                it.getArgument<(KinAccountCreationApiV4.CreateAccountResponse) -> Unit>(1)
            respond(
                KinAccountCreationApiV4.CreateAccountResponse(
                    KinAccountCreationApiV4.CreateAccountResponse.Result.UndefinedError(Exception())
                )
            )
        }.whenever(mockAccountCreationApi).createAccount(eq(createRequest), any())

        sut.createAccount(account.id, account.key as Key.PrivateKey).test {
            assertTrue(error is KinService.FatalError.UnexpectedServiceError)
            assertNull(value?.status)

            verify(mockAccountCreationApi).createAccount(eq(createRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verify(mockTransactionApi).getMinimumBalanceForRentExemption(any(),any())
            verify(mockTransactionApi).getServiceConfig(any(),any())
            verify(mockTransactionApi).getRecentBlockHash(any(),any())
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun createAccount_transient_failure() {
        doAnswer {
            val respond =
                it.getArgument<(KinAccountCreationApiV4.CreateAccountResponse) -> Unit>(1)
            respond(
                KinAccountCreationApiV4.CreateAccountResponse(
                    KinAccountCreationApiV4.CreateAccountResponse.Result.TransientFailure(Exception())
                )
            )
        }.whenever(mockAccountCreationApi).createAccount(eq(createRequest), any())

        sut.createAccount(account.id, account.key as Key.PrivateKey).test {
            assertTrue(error is KinService.FatalError.TransientFailure)
            assertNull(value?.status)

            verify(mockAccountCreationApi).createAccount(eq(createRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verify(mockTransactionApi).getMinimumBalanceForRentExemption(any(),any())
            verify(mockTransactionApi).getServiceConfig(any(),any())
            verify(mockTransactionApi).getRecentBlockHash(any(),any())
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun createAccount_sdkUpgradeRequired() {
        doAnswer {
            val respond =
                it.getArgument<(KinAccountCreationApiV4.CreateAccountResponse) -> Unit>(1)
            respond(
                KinAccountCreationApiV4.CreateAccountResponse(
                    KinAccountCreationApiV4.CreateAccountResponse.Result.UpgradeRequiredError
                )
            )
        }.whenever(mockAccountCreationApi).createAccount(eq(createRequest), any())

        sut.createAccount(account.id, account.key as Key.PrivateKey).test {
            assertTrue(error is KinService.FatalError.SDKUpgradeRequired)
            assertNull(value?.status)

            verify(mockAccountCreationApi).createAccount(eq(createRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verify(mockTransactionApi).getMinimumBalanceForRentExemption(any(),any())
            verify(mockTransactionApi).getServiceConfig(any(),any())
            verify(mockTransactionApi).getRecentBlockHash(any(),any())
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun resolveAccount_success() {
        val tokenAccounts = listOf(TestUtils.newPublicKey())
        doAnswer {
            val respond =
                it.getArgument<(KinAccountApiV4.ResolveTokenAccountsResponse) -> Unit>(1)
            respond(
                KinAccountApiV4.ResolveTokenAccountsResponse(
                    KinAccountApiV4.ResolveTokenAccountsResponse.Result.Ok,
                    tokenAccounts
                )
            )
        }.whenever(mockAccountApi).resolveTokenAcounts(eq(resolveAccountRequest), any())

        sut.resolveTokenAccounts(account.id).test {
            assertNull(error)
            assertEquals(tokenAccounts, value)

            verify(mockAccountApi).resolveTokenAcounts(eq(resolveAccountRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun resolveAccount_transient_failure() {
        val tokenAccounts = listOf(TestUtils.newPublicKey())
        doAnswer {
            val respond =
                it.getArgument<(KinAccountApiV4.ResolveTokenAccountsResponse) -> Unit>(1)
            respond(
                KinAccountApiV4.ResolveTokenAccountsResponse(
                    KinAccountApiV4.ResolveTokenAccountsResponse.Result.TransientFailure(Exception()),
                    emptyList()
                )
            )
        }.whenever(mockAccountApi).resolveTokenAcounts(eq(resolveAccountRequest), any())

        sut.resolveTokenAccounts(account.id).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.TransientFailure)

            verify(mockAccountApi).resolveTokenAcounts(eq(resolveAccountRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun resolveAccount_unknown_failure() {
        val tokenAccounts = listOf(TestUtils.newPublicKey())
        doAnswer {
            val respond =
                it.getArgument<(KinAccountApiV4.ResolveTokenAccountsResponse) -> Unit>(1)
            respond(
                KinAccountApiV4.ResolveTokenAccountsResponse(
                    KinAccountApiV4.ResolveTokenAccountsResponse.Result.UndefinedError(Exception()),
                    emptyList()
                )
            )
        }.whenever(mockAccountApi).resolveTokenAcounts(eq(resolveAccountRequest), any())

        sut.resolveTokenAccounts(account.id).test {
            assertNull(value)
            assertTrue(error is KinService.FatalError.UnexpectedServiceError)

            verify(mockAccountApi).resolveTokenAcounts(eq(resolveAccountRequest), any())
            verifyNoMoreInteractions(mockAccountApi)
            verifyZeroInteractions(mockTransactionApi)
        }
    }

    @Test
    fun getAccount_success() {
        doAnswer {
            val respond =
                it.getArgument<(KinAccountApiV4.GetAccountResponse) -> Unit>(1)
            respond(
                KinAccountApiV4.GetAccountResponse(
                    KinAccountApiV4.GetAccountResponse.Result.Ok,
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
                it.getArgument<(KinAccountApiV4.GetAccountResponse) -> Unit>(1)
            respond(
                KinAccountApiV4.GetAccountResponse(
                    KinAccountApiV4.GetAccountResponse.Result.NotFound
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
                it.getArgument<(KinAccountApiV4.GetAccountResponse) -> Unit>(1)
            respond(
                KinAccountApiV4.GetAccountResponse(
                    KinAccountApiV4.GetAccountResponse.Result.Ok
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
                it.getArgument<(KinAccountApiV4.GetAccountResponse) -> Unit>(1)
            respond(
                KinAccountApiV4.GetAccountResponse(
                    KinAccountApiV4.GetAccountResponse.Result.UndefinedError(Exception())
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
                it.getArgument<(KinAccountApiV4.GetAccountResponse) -> Unit>(1)
            respond(
                KinAccountApiV4.GetAccountResponse(
                    KinAccountApiV4.GetAccountResponse.Result.TransientFailure(Exception())
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
                it.getArgument<(KinAccountApiV4.GetAccountResponse) -> Unit>(1)
            respond(
                KinAccountApiV4.GetAccountResponse(
                    KinAccountApiV4.GetAccountResponse.Result.UpgradeRequiredError
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
                it.getArgument<(KinTransactionApiV4.GetTransactionHistoryResponse) -> Unit>(1)
            val transactions = listOf(historicalKinTransaction)
            respond(
                KinTransactionApiV4.GetTransactionHistoryResponse(
                    KinTransactionApiV4.GetTransactionHistoryResponse.Result.Ok,
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
                it.getArgument<(KinTransactionApiV4.GetTransactionHistoryResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.GetTransactionHistoryResponse(
                    KinTransactionApiV4.GetTransactionHistoryResponse.Result.Ok,
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
                it.getArgument<(KinTransactionApiV4.GetTransactionHistoryResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.GetTransactionHistoryResponse(
                    KinTransactionApiV4.GetTransactionHistoryResponse.Result.NotFound
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
                it.getArgument<(KinTransactionApiV4.GetTransactionHistoryResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.GetTransactionHistoryResponse(
                    KinTransactionApiV4.GetTransactionHistoryResponse.Result.TransientFailure(
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
                it.getArgument<(KinTransactionApiV4.GetTransactionHistoryResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.GetTransactionHistoryResponse(
                    KinTransactionApiV4.GetTransactionHistoryResponse.Result.UndefinedError(
                        Exception()
                    )
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
                it.getArgument<(KinTransactionApiV4.GetTransactionHistoryResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.GetTransactionHistoryResponse(
                    KinTransactionApiV4.GetTransactionHistoryResponse.Result.UpgradeRequiredError
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
                it.getArgument<(KinTransactionApiV4.GetTransactionHistoryResponse) -> Unit>(1)
            val transactions = listOf(historicalKinTransaction)
            respond(
                KinTransactionApiV4.GetTransactionHistoryResponse(
                    KinTransactionApiV4.GetTransactionHistoryResponse.Result.Ok,
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
                it.getArgument<(KinTransactionApiV4.GetTransactionHistoryResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.GetTransactionHistoryResponse(
                    KinTransactionApiV4.GetTransactionHistoryResponse.Result.Ok,
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
                it.getArgument<(KinTransactionApiV4.GetTransactionHistoryResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.GetTransactionHistoryResponse(
                    KinTransactionApiV4.GetTransactionHistoryResponse.Result.NotFound
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
                it.getArgument<(KinTransactionApiV4.GetTransactionHistoryResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.GetTransactionHistoryResponse(
                    KinTransactionApiV4.GetTransactionHistoryResponse.Result.TransientFailure(
                        Exception()
                    )
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
                it.getArgument<(KinTransactionApiV4.GetTransactionHistoryResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.GetTransactionHistoryResponse(
                    KinTransactionApiV4.GetTransactionHistoryResponse.Result.UndefinedError(
                        Exception()
                    )
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
                it.getArgument<(KinTransactionApiV4.GetTransactionHistoryResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.GetTransactionHistoryResponse(
                    KinTransactionApiV4.GetTransactionHistoryResponse.Result.UpgradeRequiredError
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
                it.getArgument<(KinTransactionApiV4.GetTransactionResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.GetTransactionResponse(
                    KinTransactionApiV4.GetTransactionResponse.Result.Ok,
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
                it.getArgument<(KinTransactionApiV4.GetTransactionResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.GetTransactionResponse(
                    KinTransactionApiV4.GetTransactionResponse.Result.Ok
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
                it.getArgument<(KinTransactionApiV4.GetTransactionResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.GetTransactionResponse(
                    KinTransactionApiV4.GetTransactionResponse.Result.NotFound
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
                it.getArgument<(KinTransactionApiV4.GetTransactionResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.GetTransactionResponse(
                    KinTransactionApiV4.GetTransactionResponse.Result.UndefinedError(Exception())
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
                it.getArgument<(KinTransactionApiV4.GetTransactionResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.GetTransactionResponse(
                    KinTransactionApiV4.GetTransactionResponse.Result.TransientFailure(Exception())
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
                it.getArgument<(KinTransactionApiV4.GetTransactionResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.GetTransactionResponse(
                    KinTransactionApiV4.GetTransactionResponse.Result.UpgradeRequiredError
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
        val expected = TestUtils.kinTransactionFromSolanaTransaction(
                "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD7SgRBfjyh1qkQCsj51yD3yqMXKYPPuJZWjR7omIiSyf+USAES8pO1TjzPsNSyt83QMx/cNQ32X2aakjiymAINAgABBO8oot1gdFzu7PD9FVa1d7qVwJMMaA9eHCYwdUXnQVthXcX6W5Rx/UxdWFA1UzmGZgUAY7yHYMvnC/isIcIY7/shBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogLwbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCpAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAwMBAgEJA+CuuwAAAAAA",
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

            verify(mockTransactionApi).getRecentBlockHash(any(), any())
            verify(mockTransactionApi).getServiceConfig(any(), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun buildAndSignTransaction_success_with_memo() {
        val expected = TestUtils.kinTransactionFromSolanaTransaction(
                "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAcFjOr5Gik6yixN3qtagNgmumIJRhfuxHN6YE/oAPqfOeIFKHlAhWfx9cm1FdVtWDVan2cYpy7fsc8SW1D16UMAgACBe8oot1gdFzu7PD9FVa1d7qVwJMMaA9eHCYwdUXnQVthXcX6W5Rx/UxdWFA1UzmGZgUAY7yHYMvnC/isIcIY7/shBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogLwbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCpBUpTUPhdyILWFKVWcniKKW3fHqur0KYGeIhJMvTu9qAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIEACxRUVFBQkFnTUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBPQMDAQIBCQPgrrsAAAAAAA==",
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
            KinBinaryMemo.Builder(1)
                .setForeignKey(byteArrayOf(1,2,3))
                .setTranferType(KinBinaryMemo.TransferType.Spend)
                .build()
                .toKinMemo(),
            QuarkAmount(100)
        ).test {
            assertNull(error)
            assertNotNull(value)
            println(Base64.encodeBase64String(value.bytesValue))
            assertTrue(expected.bytesValue.contentEquals(value.bytesValue))
            assertTrue(value.recordType is KinTransaction.RecordType.InFlight)

            verify(mockTransactionApi).getRecentBlockHash(any(), any())
            verify(mockTransactionApi).getServiceConfig(any(), any())
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

        val expected = TestUtils.kinTransactionFromSolanaTransaction(
            "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADBhKbplKYVFe1zp0Qbm0sgmfDJ/4PaKI6sdhW5K2hYa3yTxBa4fJz/KclOzYQnutToS8NCcgtE1Zm43VjEEo8LAgACBe8oot1gdFzu7PD9FVa1d7qVwJMMaA9eHCYwdUXnQVthXcX6W5Rx/UxdWFA1UzmGZgUAY7yHYMvnC/isIcIY7/shBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogLwbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCpBUpTUPhdyILWFKVWcniKKW3fHqur0KYGeIhJMvTu9qAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIEACxRUUFBdFBKYmVhMUFzazR6UytKSGRjMkJWem5GWXZlMU5BWnNoR2kxUHdJPQMDAQIBCQPgrrsAAAAAAA==",
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

            verify(mockTransactionApi).getRecentBlockHash(any(), any())
            verify(mockTransactionApi).getServiceConfig(any(), any())
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
//        ).test(timeout = 1000) {
//            assertTrue(error is KinService.FatalError.IllegalRequest)
//            assertNull(value)
//
//            verify(mockTransactionApi).getRecentBlockHash(any(), any())
//            verify(mockTransactionApi).getServiceConfig(any(), any())
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
//            verify(mockTransactionApi).getRecentBlockHash(any(), any())
//            verify(mockTransactionApi).getServiceConfig(any(), any())
//            verifyNoMoreInteractions(mockTransactionApi)
//            verifyZeroInteractions(mockAccountApi)
//        }
//    }

    @Test
    fun submitTransaction_success() {
        val signedTransaction = TestUtils.kinTransactionFromSolanaTransaction(
                "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
                KinTransaction.RecordType.InFlight(System.currentTimeMillis())
            )
        val signedTransactionBase64String =
            Base64.encodeBase64String(signedTransaction.bytesValue)!!
        val submitTransactionRequest =
            KinTransactionApiV4.SubmitTransactionRequest(Transaction.unmarshal(signedTransaction.bytesValue))
        val expected = TestUtils.kinTransactionFromXdr(
            signedTransactionBase64String,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
            )
        )

        doAnswer {
            val respond =
                it.getArgument<(KinTransactionApiV4.SubmitTransactionResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.SubmitTransactionResponse(
                    KinTransactionApiV4.SubmitTransactionResponse.Result.Ok,
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
        val signedTransaction = TestUtils.kinTransactionFromSolanaTransaction(
                "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
                KinTransaction.RecordType.InFlight(System.currentTimeMillis()),
                invoiceList = invoiceList
            )
        val signedTransactionBase64String =
            Base64.encodeBase64String(signedTransaction.bytesValue)!!
        val submitTransactionRequest =
            KinTransactionApiV4.SubmitTransactionRequest(
                Transaction.unmarshal(signedTransaction.bytesValue),
                invoiceList
            )
        val expected = TestUtils.kinTransactionFromSolanaTransaction(
            signedTransactionBase64String,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
            ),
            invoiceList = invoiceList
        )

        doAnswer {
            val respond =
                it.getArgument<(KinTransactionApiV4.SubmitTransactionResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.SubmitTransactionResponse(
                    KinTransactionApiV4.SubmitTransactionResponse.Result.Ok,
                    expected
                )
            )
        }.whenever(mockTransactionApi)
            .submitTransaction(eq(submitTransactionRequest), any())

        sut.submitTransaction(signedTransaction).test(timeout = 1000) {
            assertNull(error)
            assertNotNull(value)
            assertTrue { expected.bytesValue.contentEquals(value.bytesValue) }

            assertEquals(
                invoiceList.invoices.first(),
                value.asKinPayments().first().invoice
            )

            verify(mockTransactionApi)
                .submitTransaction(eq(submitTransactionRequest), any())
            verifyNoMoreInteractions(mockTransactionApi)
            verifyZeroInteractions(mockAccountApi)
        }
    }

    @Test
    fun submitTransaction_whitelisted_success() {
        val signedTransaction = TestUtils.kinTransactionFromSolanaTransaction(
                "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
                KinTransaction.RecordType.InFlight(System.currentTimeMillis())
            )
        val signedTransactionBase64String =
            Base64.encodeBase64String(signedTransaction.bytesValue)!!
        val submitTransactionRequest =
            KinTransactionApiV4.SubmitTransactionRequest(Transaction.unmarshal(signedTransaction.bytesValue))
        val expected = TestUtils.kinTransactionFromXdr(
            signedTransactionBase64String,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
            )
        )

//        doAnswer {
//            true
//        }.whenever(mockTransactionWhitelistingApi).isWhitelistingAvailable
//
//        doAnswer {
//            val callback: (KinTransactionWhitelistingApiV4.WhitelistTransactionResponse) -> Unit = it.getArgument(1)
//            callback(
//                KinTransactionWhitelistingApiV4.WhitelistTransactionResponse(
//                    KinTransactionWhitelistingApiV4.WhitelistTransactionResponse.Result.Ok,
//                    signedTransactionBase64String
//                )
//            )
//        }.whenever(mockTransactionWhitelistingApi)
//            .whitelistTransaction(
//                eq(KinTransactionWhitelistingApiV4.WhitelistTransactionRequest(signedTransactionBase64String)),
//                any()
//            )

        doAnswer {
            val respond =
                it.getArgument<(KinTransactionApiV4.SubmitTransactionResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.SubmitTransactionResponse(
                    KinTransactionApiV4.SubmitTransactionResponse.Result.Ok,
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
        val signedTransaction = TestUtils.kinTransactionFromSolanaTransaction(
                "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
                KinTransaction.RecordType.InFlight(System.currentTimeMillis())
            )
        val signedTransactionBase64String =
            Base64.encodeBase64String(signedTransaction.bytesValue)!!
        val submitTransactionRequest =
            KinTransactionApiV4.SubmitTransactionRequest(Transaction.unmarshal(signedTransaction.bytesValue))
        val expected = TestUtils.kinTransactionFromXdr(
            signedTransactionBase64String,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
            )
        )

        doAnswer {
            val respond =
                it.getArgument<(KinTransactionApiV4.SubmitTransactionResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.SubmitTransactionResponse(
                    KinTransactionApiV4.SubmitTransactionResponse.Result.Ok,
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
        val signedTransaction = TestUtils.kinTransactionFromSolanaTransaction(
                "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
                KinTransaction.RecordType.InFlight(System.currentTimeMillis())
            )
        val submitTransactionRequest =
            KinTransactionApiV4.SubmitTransactionRequest(Transaction.unmarshal(signedTransaction.bytesValue))

        doAnswer {
            val respond =
                it.getArgument<(KinTransactionApiV4.SubmitTransactionResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.SubmitTransactionResponse(
                    KinTransactionApiV4.SubmitTransactionResponse.Result.BadSequenceNumber
                )
            )
        }.whenever(mockTransactionApi)
            .submitTransaction(eq(submitTransactionRequest), any())

        sut.submitTransaction(signedTransaction).test() {
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
        val signedTransaction = TestUtils.kinTransactionFromSolanaTransaction(
                "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
                KinTransaction.RecordType.InFlight(System.currentTimeMillis())
            )
        val submitTransactionRequest =
            KinTransactionApiV4.SubmitTransactionRequest(Transaction.unmarshal(signedTransaction.bytesValue))

        doAnswer {
            val respond =
                it.getArgument<(KinTransactionApiV4.SubmitTransactionResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.SubmitTransactionResponse(
                    KinTransactionApiV4.SubmitTransactionResponse.Result.InsufficientFee
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
        val signedTransaction = TestUtils.kinTransactionFromSolanaTransaction(
                "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
                KinTransaction.RecordType.InFlight(System.currentTimeMillis())
            )
        val submitTransactionRequest =
            KinTransactionApiV4.SubmitTransactionRequest(Transaction.unmarshal(signedTransaction.bytesValue))

        doAnswer {
            val respond =
                it.getArgument<(KinTransactionApiV4.SubmitTransactionResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.SubmitTransactionResponse(
                    KinTransactionApiV4.SubmitTransactionResponse.Result.InsufficientBalance
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
        val signedTransaction = TestUtils.kinTransactionFromSolanaTransaction(
                "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
                KinTransaction.RecordType.InFlight(System.currentTimeMillis())
            )
        val submitTransactionRequest =
            KinTransactionApiV4.SubmitTransactionRequest(Transaction.unmarshal(signedTransaction.bytesValue))

        doAnswer {
            val respond =
                it.getArgument<(KinTransactionApiV4.SubmitTransactionResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.SubmitTransactionResponse(
                    KinTransactionApiV4.SubmitTransactionResponse.Result.NoAccount
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
        val signedTransaction = TestUtils.kinTransactionFromSolanaTransaction(
                "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
                KinTransaction.RecordType.InFlight(System.currentTimeMillis())
            )
        val submitTransactionRequest =
            KinTransactionApiV4.SubmitTransactionRequest(Transaction.unmarshal(signedTransaction.bytesValue))

        doAnswer {
            val respond =
                it.getArgument<(KinTransactionApiV4.SubmitTransactionResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.SubmitTransactionResponse(
                    KinTransactionApiV4.SubmitTransactionResponse.Result.TransientFailure(Exception())
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
        val signedTransaction = TestUtils.kinTransactionFromSolanaTransaction(
                "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
                KinTransaction.RecordType.InFlight(System.currentTimeMillis())
            )
        val submitTransactionRequest =
            KinTransactionApiV4.SubmitTransactionRequest(Transaction.unmarshal(signedTransaction.bytesValue))

        doAnswer {
            val respond =
                it.getArgument<(KinTransactionApiV4.SubmitTransactionResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.SubmitTransactionResponse(
                    KinTransactionApiV4.SubmitTransactionResponse.Result.UndefinedError(Exception())
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
        val signedTransaction = TestUtils.kinTransactionFromSolanaTransaction(
                "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
                KinTransaction.RecordType.InFlight(System.currentTimeMillis())
            )
        val submitTransactionRequest =
            KinTransactionApiV4.SubmitTransactionRequest(Transaction.unmarshal(signedTransaction.bytesValue))

        doAnswer {
            val respond =
                it.getArgument<(KinTransactionApiV4.SubmitTransactionResponse) -> Unit>(1)
            respond(
                KinTransactionApiV4.SubmitTransactionResponse(
                    KinTransactionApiV4.SubmitTransactionResponse.Result.UpgradeRequiredError
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
            KinTransactionApiV4.SubmitTransactionRequest(
                Transaction.unmarshal(historicalKinTransaction.bytesValue),
                InvoiceList.Builder().addInvoice(
                    Invoice.Builder()
                        .addLineItem(
                            LineItem.Builder("aTitle", KinAmount(123)).build()
                        ).build()
                ).build()
            )

        val request2 =
            KinTransactionApiV4.SubmitTransactionRequest(
                Transaction.unmarshal(historicalKinTransaction.bytesValue),
                InvoiceList.Builder().addInvoice(
                    Invoice.Builder()
                        .addLineItem(
                            LineItem.Builder("aTitle", KinAmount(123)).build()
                        ).build()
                ).build()
            )
        val request3 =
            KinTransactionApiV4.SubmitTransactionRequest(
                Transaction.unmarshal(historicalKinTransaction.bytesValue),
                InvoiceList.Builder().addInvoice(
                    Invoice.Builder()
                        .addLineItem(
                            LineItem.Builder("aTitle2", KinAmount(456)).build()
                        ).build()
                ).build()
            )

        assertEquals(request1, request2)
        assertEquals(request1.hashCode(), request2.hashCode())
        kotlin.test.assertNotEquals(request1, request3)
        kotlin.test.assertNotEquals(request1.hashCode(), request3.hashCode())
    }

    @Test
    fun getMinFee_success() {

        sut.getMinFee().test {
            assertEquals(value, QuarkAmount(0))
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
        assertEquals(
            listOf(historicalKinTransaction, historicalKinTransaction2),
            values
        )
    }
}
