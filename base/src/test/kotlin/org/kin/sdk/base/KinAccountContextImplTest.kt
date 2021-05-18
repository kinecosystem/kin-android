
package org.kin.sdk.base

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.kin.sdk.base.models.AppIdx
import org.kin.sdk.base.models.AppInfo
import org.kin.sdk.base.models.AppUserCreds
import org.kin.sdk.base.models.Invoice
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.KinMemo
import org.kin.sdk.base.models.KinPayment
import org.kin.sdk.base.models.KinPaymentItem
import org.kin.sdk.base.models.KinTokenAccountInfo
import org.kin.sdk.base.models.LineItem
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.TransactionHash
import org.kin.sdk.base.models.asKinAccountId
import org.kin.sdk.base.models.asKinPayments
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.models.merge
import org.kin.sdk.base.models.toKeyPair
import org.kin.sdk.base.models.toKin
import org.kin.sdk.base.network.services.AppInfoProvider
import org.kin.sdk.base.network.services.KinService
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.KinTransactions
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.stellar.models.SolanaKinTransaction
import org.kin.sdk.base.storage.Storage
import org.kin.sdk.base.tools.DisposeBag
import org.kin.sdk.base.tools.ExecutorServices
import org.kin.sdk.base.tools.Optional
import org.kin.sdk.base.tools.Promise
import org.kin.sdk.base.tools.TestUtils
import org.kin.sdk.base.tools.ValueListener
import org.kin.sdk.base.tools.ValueSubject
import org.kin.sdk.base.tools.latchOperationValueCapture
import org.kin.sdk.base.tools.test
import org.kin.sdk.base.tools.updateStatus
import org.kin.stellarfork.codec.Base64
import java.lang.IllegalArgumentException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KinAccountContextImplTest {

    companion object {
        val privateKey = TestUtils.newPrivateKey()
        val registeredAccount = KinAccount(privateKey)
            .updateStatus(KinAccount.Status.Registered(1234)).copy(
                balance = KinBalance(KinAmount(1000))
            )
        val registeredAccount2 = TestUtils.newSigningKinAccount()
            .updateStatus(KinAccount.Status.Registered(1234))
        val unregisteredAccount = TestUtils.newSigningKinAccount()

        val fee = QuarkAmount(100)

        val pagingToken = KinTransaction.PagingToken("16576645322248192")
        val networkEnvironment = NetworkEnvironment.TestNet
        val historicalKinTransaction =  SolanaKinTransaction(
            Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABhp+xHw13QTBg0/2iNL+W0wZ/W9fAF76qqZH8Al+djtT4xw1fIufa+fmDQ5EFlwziYixu4Xt3tqgIwcJm41DgCAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW94cG3LT0a+kX6wqa2O0qYbqC6rMebhyR0LBQgHRjoebjh8kk+ML92HthfRee5O1nEhhSh3G/n3/omSR1jJoT8qQbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCp/emg+dGNKfC5/F0EbUpvD4rCYPM6q/9+KMsPHsXv4esBAwMBAgEJA+CuuwAAAAAA")!!,
            networkEnvironment = networkEnvironment
        )
    }

    private lateinit var sut: KinAccountContext
    private lateinit var sut2: KinAccountContext

    lateinit var excecutors: ExecutorServices
    lateinit var mockService: KinService
    lateinit var mockStorage: Storage

    lateinit var mockService2: KinService
    lateinit var mockStorage2: Storage

    @Before
    fun setUp() {
        excecutors = ExecutorServices()
        mockService = mock {

        }
        mockStorage = mock {
            on { getMinApiVersion() } doReturn Promise.of(Optional.empty())
//            on { getStoredAccount(eq(registeredAccount.id))} doReturn Promise.of(Optional.of(registeredAccount))
        }

        mockService2 = mock {

        }
        mockStorage2 = mock {
            on { getMinApiVersion() } doReturn Promise.of(Optional.empty())
//            on { getStoredAccount(eq(registeredAccount2.id))} doReturn Promise.of(Optional.of(registeredAccount2))
        }

        sut = KinAccountContext.Builder(
            KinEnvironment.Agora.Builder(NetworkEnvironment.TestNet)
                .setAppInfoProvider(object : AppInfoProvider {
                    override val appInfo: AppInfo by lazy {
                        AppInfo(
                            AppIdx(0),
                            TestUtils.newKinAccount().id,
                            "TestApp",
                            0
                        )
                    }

                    override fun getPassthroughAppUserCredentials(): AppUserCreds {
                        return AppUserCreds("abcd", "1212")
                    }
                })
                .setKinService(mockService)
                .setExecutorServices(excecutors)
                .doNotAutoMergeTokenAccounts()
                .setStorage(mockStorage)
        ).useExistingAccount(registeredAccount.id).build()

        sut2 = KinAccountContext.Builder(
            KinEnvironment.Agora.Builder(NetworkEnvironment.TestNet)
                .setAppInfoProvider(object : AppInfoProvider {
                    override val appInfo: AppInfo by lazy {
                        AppInfo(
                            AppIdx(0),
                            TestUtils.newKinAccount().id,
                            "TestApp",
                            0
                        )
                    }

                    override fun getPassthroughAppUserCredentials(): AppUserCreds {
                        return AppUserCreds("abcd", "1212")
                    }
                })
                .setKinService(mockService2)
                .setExecutorServices(excecutors)
                .doNotAutoMergeTokenAccounts()
                .setStorage(mockStorage2)
        ).useExistingAccount(registeredAccount2.id).build()

        doAnswer {
            val accountId: KinAccount.Id = it.arguments.first() as KinAccount.Id
            Promise.of(listOf(KinTokenAccountInfo(accountId.toKeyPair().asPublicKey(), KinAmount.ONE, null)))
        }.whenever(mockService).resolveTokenAccounts(any())

        verify(mockStorage).getAllAccountIds()
        verify(mockStorage2).getAllAccountIds()
    }

    @Test
    fun testNewAccountBuilder() {
        val context = KinAccountContext.Builder(
            KinEnvironment.Agora.Builder(NetworkEnvironment.TestNet)
                .setAppInfoProvider(KinEnvironmentTest.DummyAppInfoProvider())
                .setKinService(mockService)
                .setExecutorServices(excecutors)
                .doNotAutoMergeTokenAccounts()
                .setStorage(mockStorage)
        ).createNewAccount().build()

        assertNotNull(context.accountId)
    }

    @Test
    fun getSigningAccount_success() {
        doAnswer {
            Promise.of(Optional.of(registeredAccount))
        }.whenever(mockStorage).getStoredAccount(eq(registeredAccount.id))

        sut.getAccount().test {
            assertNull(error)
            assertNotNull(value)
            assertEquals(registeredAccount, value)

            verify(mockStorage).getStoredAccount(eq(registeredAccount.id))
            verifyZeroInteractions(mockService)
            verifyNoMoreInteractions(mockStorage)
        }
    }

    @Test
    fun getSigningAccount_upgradeRequired() {
        doAnswer {
            Promise.of(Optional.of(unregisteredAccount))
        }.whenever(mockStorage).getStoredAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.error<KinAccount>(KinService.FatalError.SDKUpgradeRequired)
        }.whenever(mockService).createAccount(eq(registeredAccount.id),
            eq(privateKey),
            eq(AppIdx.TEST_APP_IDX))

        doAnswer {
            Promise.error<KinAccount>(KinService.FatalError.SDKUpgradeRequired)
        }.whenever(mockService).getAccount(eq(registeredAccount.id))

        sut.getAccount().test {
            assertNotNull(error)
            assertNull(value)

            verify(mockStorage).getStoredAccount(eq(registeredAccount.id))
            verify(mockService).createAccount(any(), any(), eq(AppIdx.TEST_APP_IDX))
            verifyZeroInteractions(mockService)
            verifyNoMoreInteractions(mockStorage)
        }
    }

    @Test
    fun getSigningAccount_missing_in_storage() {
        doAnswer {
            Promise.of(Optional.empty<KinAccount>())
        }.whenever(mockStorage).getStoredAccount(eq(registeredAccount.id))

        sut.getAccount().test {
            assertNull(value)
            assertNotNull(error)
            assertTrue(error is IllegalStateException)

            verify(mockStorage).getStoredAccount(eq(registeredAccount.id))
            verifyZeroInteractions(mockService)
            verifyNoMoreInteractions(mockStorage)
        }
    }

    @Test
    fun getSigningAccount_force_fetch_success() {
        doAnswer {
            Promise.of(Optional.of(registeredAccount))
        }.whenever(mockStorage).getStoredAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(registeredAccount)
        }.whenever(mockService).getAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(registeredAccount)
        }.whenever(mockStorage).updateAccountInStorage(eq(registeredAccount))

        sut.getAccount(true).test {
            assertNull(error)
            assertNotNull(value)
            assertEquals(registeredAccount, value)

            verify(mockStorage).getStoredAccount(eq(registeredAccount.id))
            verify(mockService).getAccount(eq(registeredAccount.id))
            verify(mockStorage).updateAccountInStorage(eq(registeredAccount))
            verifyZeroInteractions(mockService)
            verifyNoMoreInteractions(mockStorage)
        }
    }

    @Test
    fun getSigningAccount_force_fetch_not_found_requires_resolve() {
        doAnswer {
            Promise.of(Optional.of(registeredAccount))
        }.whenever(mockStorage).getStoredAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.error<KinAccount>(KinService.FatalError.ItemNotFound)
        }.whenever(mockService).getAccount(eq(registeredAccount.id))

        val newKey = TestUtils.newPublicKey()
        doAnswer {
            Promise.of(listOf(KinTokenAccountInfo(newKey, KinAmount.ONE, null)))
        }.whenever(mockService).resolveTokenAccounts(eq(registeredAccount.id))

        doAnswer {
            Promise.of(KinAccount(newKey).copy(status = KinAccount.Status.Registered(0),balance = KinBalance(KinAmount(300))))
        }.whenever(mockService).getAccount(eq(newKey.asKinAccountId()))

        doAnswer {
            Promise.of(registeredAccount.copy(status = KinAccount.Status.Registered(0), tokenAccounts = listOf(newKey), balance = KinBalance(KinAmount(300))))
        }.whenever(mockStorage).updateAccountInStorage(any())

        sut.getAccount(true).test {
            assertNull(error)
            assertNotNull(value)
            assertEquals(registeredAccount.copy(status = KinAccount.Status.Registered(0), tokenAccounts = listOf(newKey), balance = KinBalance(KinAmount(300))), value)

            verify(mockStorage).getStoredAccount(eq(registeredAccount.id))
            verify(mockService).getAccount(eq(registeredAccount.id))
            verify(mockStorage).updateAccountInStorage(any())
            verify(mockService).getAccount(eq(newKey.asKinAccountId()))
            verify(mockService).resolveTokenAccounts(eq(registeredAccount.id))
            verifyZeroInteractions(mockService)
            verifyNoMoreInteractions(mockStorage)
        }
    }

    @Test
    fun getSigningAccount_needs_create_success() {
        val accountId = registeredAccount.id
        val unregisteredAccount = registeredAccount.updateStatus(KinAccount.Status.Unregistered)
        val registeredAccountNoPrivKey =
            KinAccount(registeredAccount.key.asPublicKey(), status = registeredAccount.status, balance = KinBalance(
                KinAmount(1000)
            ))
        doAnswer {
            Promise.of(registeredAccountNoPrivKey)
        }.whenever(mockService).createAccount(eq(accountId),
            eq(privateKey),
            eq(AppIdx.TEST_APP_IDX))

        doAnswer {
            Promise.of(Optional.of(unregisteredAccount))
        }.whenever(mockStorage).getStoredAccount(eq(accountId))

        doAnswer {
            true
        }.whenever(mockStorage).updateAccount(eq(registeredAccount))

        sut.getAccount().test {
            assertNull(error)
            assertNotNull(value)
            assertEquals(registeredAccount, value)

            verify(mockService).createAccount(eq(accountId),
                eq(privateKey),
                eq(AppIdx.TEST_APP_IDX))
            verify(mockStorage).getStoredAccount(eq(accountId))
            verify(mockStorage).updateAccount(eq(registeredAccount))
            verifyNoMoreInteractions(mockService)
            verifyNoMoreInteractions(mockStorage)
        }
    }

    @Test
    fun getSigningAccount_created_but_wrong_status_from_import_success() {
        val accountId = registeredAccount.id
        val unregisteredAccount = registeredAccount.updateStatus(KinAccount.Status.Unregistered)
        val registeredAccountNoPrivKey =
            KinAccount(registeredAccount.key.asPublicKey(), status = registeredAccount.status, balance = KinBalance(
                KinAmount(1000)
            ))
        doAnswer {
            Promise.error<KinAccount>(KinService.FatalError.PermanentlyUnavailable)
        }.whenever(mockService).createAccount(eq(accountId),
            eq(privateKey),
            eq(AppIdx.TEST_APP_IDX)
        )

        doAnswer {
            Promise.of(registeredAccountNoPrivKey)
        }.whenever(mockService).getAccount(eq(accountId))

        doAnswer {
            Promise.of(registeredAccount)
        }.whenever(mockStorage).updateAccountInStorage(eq(registeredAccountNoPrivKey))

        doAnswer {
            Promise.of(Optional.of(unregisteredAccount))
        }.whenever(mockStorage).getStoredAccount(eq(accountId))

        doAnswer {
            true
        }.whenever(mockStorage).updateAccount(eq(registeredAccount))

        sut.getAccount().test {
            assertNull(error)
            assertNotNull(value)
            assertEquals(registeredAccount, value)

            verify(mockService).createAccount(eq(accountId),
                eq(privateKey),
               eq(AppIdx.TEST_APP_IDX))
            verify(mockService).getAccount(eq(accountId))
            verify(mockStorage).getStoredAccount(eq(accountId))
            verify(mockStorage).updateAccountInStorage(eq(registeredAccountNoPrivKey))
            verifyNoMoreInteractions(mockService)
            verifyNoMoreInteractions(mockStorage)
        }
    }

    @Test
    fun watchBalance_local_andService_success() {

        val accountFromService = KinAccount(
            registeredAccount.key,
            balance = KinBalance(KinAmount(123)),
            status = registeredAccount.status
        )
        doAnswer {
            Promise.of(Optional.of(registeredAccount))
        }.whenever(mockStorage).getStoredAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(accountFromService)
        }.whenever(mockService).getAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(accountFromService)
        }.whenever(mockStorage).updateAccountInStorage(eq(accountFromService))

        latchOperationValueCapture<KinBalance>(2, 10) { capture ->
            sut.observeBalance(ObservationMode.Passive, object : ValueListener<KinBalance> {
                override fun onNext(value: KinBalance) {
                    capture(value)
                }

                override fun onError(error: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }.apply {
            assertNull(error)
            assertEquals(listOf(KinBalance(KinAmount(1000)), KinBalance(KinAmount(123))), values)
            verify(mockStorage, times(2)).getStoredAccount(eq(registeredAccount.id))
            verify(mockService).getAccount(eq(registeredAccount.id))
            verify(mockStorage,times(2)).updateAccountInStorage(eq(accountFromService))
        }
    }

    @Test
    fun watchBalance_local_andService_success2() {

        val accountFromService = KinAccount(
            registeredAccount.key,
            balance = KinBalance(KinAmount(123)),
            status = registeredAccount.status
        )
        doAnswer {
            Promise.of(Optional.of(registeredAccount))
        }.whenever(mockStorage).getStoredAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(accountFromService)
        }.whenever(mockService).getAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(accountFromService)
        }.whenever(mockStorage).updateAccountInStorage(eq(accountFromService))

        latchOperationValueCapture<KinBalance>(2, 10) { capture ->
            sut.observeBalance().add(capture)
        }.apply {
            assertNull(error)
            assertEquals(listOf(KinBalance(KinAmount(1000)), KinBalance(KinAmount(123))), values)
            verify(mockStorage, times(2)).getStoredAccount(eq(registeredAccount.id))
            verify(mockService).getAccount(eq(registeredAccount.id))
            verify(mockStorage, times(2)).updateAccountInStorage(eq(accountFromService))
        }
    }

    @Test
    fun watchBalance_local_andService_success3() {

        val accountFromService = KinAccount(
            registeredAccount.key,
            balance = KinBalance(KinAmount(123)),
            status = registeredAccount.status
        )
        doAnswer {
            Promise.of(Optional.of(registeredAccount))
        }.whenever(mockStorage).getStoredAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(accountFromService)
        }.whenever(mockService).getAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(accountFromService)
        }.whenever(mockStorage).updateAccountInStorage(eq(accountFromService))

        latchOperationValueCapture<KinBalance>(2, 10) { capture ->
            sut.observeBalance(balanceListener = object : ValueListener<KinBalance> {
                override fun onNext(value: KinBalance) {
                    capture(value)
                }

                override fun onError(error: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }.apply {
            assertNull(error)
            assertEquals(listOf(KinBalance(KinAmount(1000)), KinBalance(KinAmount(123))), values)
            verify(mockStorage, times(2)).getStoredAccount(eq(registeredAccount.id))
            verify(mockService).getAccount(eq(registeredAccount.id))
            verify(mockStorage, times(2)).updateAccountInStorage(eq(accountFromService))
        }
    }

    @Test
    fun watchPayments_noneInStorage_fetchFromService_success() {

        val latestKinTransactions = listOf(historicalKinTransaction)
        val expectedPayments = latestKinTransactions.asKinPayments()

        doAnswer {
            Promise.of<List<KinTransaction>>(latestKinTransactions)
        }.whenever(mockService).getLatestTransactions(eq(registeredAccount.id))

        doAnswer {
            Promise.of<List<KinTransaction>>(latestKinTransactions)
        }.whenever(mockStorage)
            .upsertNewTransactionsInStorage(eq(registeredAccount.id), eq(latestKinTransactions))

        doAnswer {
            Promise.of(null)
        }.whenever(mockStorage).getStoredTransactions(eq(registeredAccount.id))

        latchOperationValueCapture<List<KinPayment>>(2, timeoutSeconds = 10) { capture ->
            sut.observePayments()
                .add(capture)
        }.apply {
            assertEquals(expectedPayments, value)

            verify(mockService).getLatestTransactions(eq(registeredAccount.id))
            verify(mockStorage).upsertNewTransactionsInStorage(
                eq(registeredAccount.id),
                eq(latestKinTransactions)
            )
            verify(mockStorage, times(2)).getStoredTransactions(eq(registeredAccount.id))
        }
    }

    @Test
    fun watchPayments_noneInStorage_fetchFromService_success2() {

        val latestKinTransactions: List<KinTransaction> = listOf(historicalKinTransaction)
        val expectedPayments = latestKinTransactions.asKinPayments()

        doAnswer {
            Promise.of(latestKinTransactions)
        }.whenever(mockService).getLatestTransactions(eq(registeredAccount.id))

        doAnswer {
            Promise.of(latestKinTransactions)
        }.whenever(mockStorage)
            .upsertNewTransactionsInStorage(eq(registeredAccount.id), eq(latestKinTransactions))

        doAnswer {
            Promise.of(null)
        }.whenever(mockStorage).getStoredTransactions(eq(registeredAccount.id))

        latchOperationValueCapture<List<KinPayment>>(2, timeoutSeconds = 10) { capture ->
            sut.observePayments(paymentsListener = object : ValueListener<List<KinPayment>> {
                override fun onNext(value: List<KinPayment>) {
                    capture(value)
                }

                override fun onError(error: Throwable) {
                    TODO("Not yet implemented")
                }
            })

        }.apply {
            assertEquals(expectedPayments, value)

            verify(mockService).getLatestTransactions(eq(registeredAccount.id))
            verify(mockStorage).upsertNewTransactionsInStorage(
                eq(registeredAccount.id),
                eq(latestKinTransactions)
            )
            verify(mockStorage, times(2)).getStoredTransactions(eq(registeredAccount.id))
        }
    }

    @Test
    fun sendKinPayment_success() {
        val destination = TestUtils.newKinAccount()
        val transactionToBeSent = 
            SolanaKinTransaction(
                Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABhp+xHw13QTBg0/2iNL+W0wZ/W9fAF76qqZH8Al+djtT4xw1fIufa+fmDQ5EFlwziYixu4Xt3tqgIwcJm41DgCAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW94cG3LT0a+kX6wqa2O0qYbqC6rMebhyR0LBQgHRjoebjh8kk+ML92HthfRee5O1nEhhSh3G/n3/omSR1jJoT8qQbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCp/emg+dGNKfC5/F0EbUpvD4rCYPM6q/9+KMsPHsXv4esBAwMBAgEJA+CuuwAAAAAA")!!,
                networkEnvironment = networkEnvironment
            )           
        val responseTransaction = SolanaKinTransaction(
            transactionToBeSent.bytesValue,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                KinTransaction.ResultCode.Success, 
            ),
            networkEnvironment
        )

        doAnswer {
            Promise.of(Optional.of(registeredAccount))
        }.whenever(mockStorage).getStoredAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(transactionToBeSent)
        }.whenever(mockService).buildAndSignTransaction(
            eq(registeredAccount.key as Key.PrivateKey),
            eq(registeredAccount.key.asPublicKey()),
            eq(listOf(KinPaymentItem(KinAmount(123), destination.id))),
            eq(KinMemo.NONE),
            any(),
            any()
        )

        doAnswer {
            val buildAndSign = it.getArgument(0) as (() -> Promise<KinTransaction>)
            buildAndSign()
            Promise.of(responseTransaction)
        }.whenever(mockService).buildSignAndSubmitTransaction(any())

        doAnswer {
            Promise.of(listOf(responseTransaction))
        }.whenever(mockStorage).insertNewTransactionInStorage(
            eq(registeredAccount.id),
            eq(responseTransaction)
        )

        doAnswer {
            true
        }.whenever(mockStorage).updateAccount(eq(registeredAccount))

        val amountSpent = transactionToBeSent.paymentOperations.first().amount
        val updatedAccountWithNewBalance = registeredAccount.merge(
            KinAccount(
                registeredAccount.key,
                balance = KinBalance( amountSpent)
            )
        )

        doAnswer {
            Promise.of(Optional.of(updatedAccountWithNewBalance))
        }.whenever(mockStorage).updateAccountBalance(
            eq(registeredAccount.id),
            eq(KinBalance(registeredAccount.balance.amount - amountSpent - responseTransaction.fee.toKin()))
        )

        doAnswer {
            Promise.of(KinTransactions(listOf(responseTransaction), null, null))
        }.whenever(mockStorage).getStoredTransactions(eq(registeredAccount.id))

        sut.sendKinPayment(KinAmount(123), destination.id).test(1000) {
            assertNull(error)
            assertEquals(responseTransaction.asKinPayments().first(), value)

            verify(mockStorage, times(2)).getStoredAccount(eq(registeredAccount.id))
            verify(mockService).buildAndSignTransaction(
                eq(registeredAccount.key as Key.PrivateKey),
                eq(registeredAccount.key.asPublicKey()),
                eq(listOf(KinPaymentItem(KinAmount(123), destination.id))),
                eq(KinMemo.NONE),
                any(),
                any()
            )
            val expectedTransactionToSubmit = {Promise.of(transactionToBeSent as KinTransaction)}
            verify(mockService).buildSignAndSubmitTransaction(any())
            verify(mockStorage).advanceSequence(eq(registeredAccount.id))
            verify(mockStorage).insertNewTransactionInStorage(
                eq(registeredAccount.id),
                eq(responseTransaction)
            )
            verify(mockStorage).updateAccountBalance(
                eq(registeredAccount.id),
                eq(KinBalance(registeredAccount.balance.amount - amountSpent - responseTransaction.fee.toKin()))
            )
            verify(mockStorage).getStoredTransactions(eq(registeredAccount.id))
            verifyNoMoreInteractions(mockService)
            verifyNoMoreInteractions(mockStorage)
        }
    }

    @Test
    fun payInvoice_success() {
        val destination = TestUtils.newKinAccount()
        val transactionToBeSent =
            SolanaKinTransaction(
                Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABhp+xHw13QTBg0/2iNL+W0wZ/W9fAF76qqZH8Al+djtT4xw1fIufa+fmDQ5EFlwziYixu4Xt3tqgIwcJm41DgCAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW94cG3LT0a+kX6wqa2O0qYbqC6rMebhyR0LBQgHRjoebjh8kk+ML92HthfRee5O1nEhhSh3G/n3/omSR1jJoT8qQbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCp/emg+dGNKfC5/F0EbUpvD4rCYPM6q/9+KMsPHsXv4esBAwMBAgEJA+CuuwAAAAAA")!!,
                networkEnvironment = networkEnvironment
            )
        val responseTransaction = SolanaKinTransaction(
            transactionToBeSent.bytesValue,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                KinTransaction.ResultCode.Success
            ),
            networkEnvironment
        )

        doAnswer {
            Promise.of(Optional.of(registeredAccount))
        }.whenever(mockStorage).getStoredAccount(eq(registeredAccount.id))

        val expectedMemo =
            KinMemo(Base64().decode("QQAAtPJbea1Ask4zS+JHdc2BVznFYve1NAZshGi1PwI=")!!)
        val destinationKinAppIdx = AppIdx.TEST_APP_IDX
        val invoice = Invoice.Builder()
            .addLineItem(
                LineItem.Builder("thing1", KinAmount(123))
                    .build()
            )
            .build()

        doAnswer {
            Promise.of(transactionToBeSent)
        }.whenever(mockService).buildAndSignTransaction(
            eq(registeredAccount.key as Key.PrivateKey),
            eq(registeredAccount.key.asPublicKey()),
            eq(listOf(KinPaymentItem(KinAmount(123), destination.id, Optional.of(invoice)))),
            eq(expectedMemo),
            any(),
            any()
        )

        doAnswer {
            Promise.of(responseTransaction)
        }.whenever(mockService).buildSignAndSubmitTransaction(any())

        doAnswer {
            Promise.of(responseTransaction)
        }.whenever(mockService).submitTransaction(eq(transactionToBeSent))

        doAnswer {
            Promise.of(listOf(responseTransaction))
        }.whenever(mockStorage).insertNewTransactionInStorage(
            eq(registeredAccount.id),
            eq(responseTransaction)
        )

        doAnswer {
            true
        }.whenever(mockStorage).updateAccount(eq(registeredAccount))

        val amountSpent = transactionToBeSent.paymentOperations.first().amount
        val updatedAccountWithNewBalance = registeredAccount.merge(
            KinAccount(
                registeredAccount.key,
                balance = KinBalance(amountSpent)
            )
        )

        doAnswer {
            Promise.of(Optional.of(updatedAccountWithNewBalance))
        }.whenever(mockStorage).updateAccountBalance(
            eq(registeredAccount.id),
            eq(KinBalance(registeredAccount.balance.amount - amountSpent - responseTransaction.fee.toKin()))
        )

        doAnswer {
            Promise.of(KinTransactions(listOf(responseTransaction), null, null))
        }.whenever(mockStorage).getStoredTransactions(eq(registeredAccount.id))

        sut.payInvoice(invoice, destination.id, destinationKinAppIdx).test(10) {
            assertNull(error)
            assertEquals(responseTransaction.asKinPayments().first(), value)

            verify(mockStorage, times(2)).getStoredAccount(eq(registeredAccount.id))
            verify(mockService).buildAndSignTransaction(
                eq(registeredAccount.key as Key.PrivateKey),
                eq(registeredAccount.key.asPublicKey()),
                eq(listOf(KinPaymentItem(KinAmount(123), destination.id, Optional.of(invoice)))),
                eq(expectedMemo),
                any(),
                any()
            )
            verify(mockService).buildSignAndSubmitTransaction(any())
            verify(mockStorage).advanceSequence(eq(registeredAccount.id))
            verify(mockStorage).insertNewTransactionInStorage(
                eq(registeredAccount.id),
                eq(responseTransaction)
            )
            verify(mockStorage).updateAccountBalance(
                eq(registeredAccount.id),
                eq(KinBalance(registeredAccount.balance.amount - amountSpent - responseTransaction.fee.toKin()))
            )
            verify(mockStorage).getStoredTransactions(eq(registeredAccount.id))
            verifyNoMoreInteractions(mockService)
            verifyNoMoreInteractions(mockStorage)
        }
    }

    @Test
    fun sendMultipleKinPayments_success() {
        val destination = TestUtils.newKinAccount()
        val transactionToBeSent =
            SolanaKinTransaction(
                Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABhp+xHw13QTBg0/2iNL+W0wZ/W9fAF76qqZH8Al+djtT4xw1fIufa+fmDQ5EFlwziYixu4Xt3tqgIwcJm41DgCAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW94cG3LT0a+kX6wqa2O0qYbqC6rMebhyR0LBQgHRjoebjh8kk+ML92HthfRee5O1nEhhSh3G/n3/omSR1jJoT8qQbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCp/emg+dGNKfC5/F0EbUpvD4rCYPM6q/9+KMsPHsXv4esBAwMBAgEJA+CuuwAAAAAA")!!,
                networkEnvironment = networkEnvironment
            )
        val responseTransaction = SolanaKinTransaction(
            transactionToBeSent.bytesValue,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                KinTransaction.ResultCode.Success
            ),
            networkEnvironment
        )

        doAnswer {
            Promise.of(Optional.of(registeredAccount))
        }.whenever(mockStorage).getStoredAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(transactionToBeSent)
        }.whenever(mockService).buildAndSignTransaction(
            eq(registeredAccount.key as Key.PrivateKey),
            eq(registeredAccount.key.asPublicKey()),
            eq(listOf(KinPaymentItem(KinAmount(123), destination.id))),
            eq(KinMemo.NONE),
            any(),
            any()
        )

        doAnswer {
            Promise.of(responseTransaction)
        }.whenever(mockService).buildSignAndSubmitTransaction(any())

        doAnswer {
            Promise.of(registeredAccount)
        }.whenever(mockStorage).updateAccountInStorage(eq(registeredAccount))

        doAnswer {
            Promise.of(listOf(responseTransaction))
        }.whenever(mockStorage).insertNewTransactionInStorage(
            eq(registeredAccount.id),
            eq(responseTransaction)
        )

        val amountSpent = KinAmount(123)
        val updatedAccountWithNewBalance = registeredAccount.merge(
            KinAccount(
                registeredAccount.key,
                balance = KinBalance(amountSpent)
            )
        )

        doAnswer {
            Promise.of(Optional.of(updatedAccountWithNewBalance))
        }.whenever(mockStorage).updateAccountBalance(
            eq(registeredAccount.id),
            eq(KinBalance(registeredAccount.balance.amount - amountSpent - responseTransaction.fee.toKin()))
        )

        doAnswer {
            Promise.of(
                KinTransactions(
                    listOf(
                        responseTransaction,
                        responseTransaction,
                        responseTransaction
                    ), null, null
                )
            )
        }.whenever(mockStorage).getStoredTransactions(eq(registeredAccount.id))

        sut.sendKinPayment(KinAmount(123), destination.id).test(10) {
            assertNull(error)
            assertEquals(responseTransaction.asKinPayments().first(), value)
        }

        sut.sendKinPayment(KinAmount(123), destination.id).test(10) {
            assertNull(error)
            assertEquals(responseTransaction.asKinPayments().first(), value)
        }

        sut.sendKinPayment(KinAmount(123), destination.id).test(10) {
            assertNull(error)
            assertEquals(responseTransaction.asKinPayments().first(), value)
        }

        verify(mockStorage, times(6)).getStoredAccount(eq(registeredAccount.id))
        verify(mockService, times(3)).buildAndSignTransaction(
            eq(registeredAccount.key as Key.PrivateKey),
            eq(registeredAccount.key.asPublicKey()),
            eq(listOf(KinPaymentItem(KinAmount(123), destination.id))),
            eq(KinMemo.NONE),
            any(),
            any()
        )
        verify(mockService, times(3)).buildSignAndSubmitTransaction(any())
        verify(mockStorage, times(3)).advanceSequence(eq(registeredAccount.id))
        verify(mockStorage, times(3)).insertNewTransactionInStorage(
            eq(registeredAccount.id),
            eq(responseTransaction)
        )
        verify(mockStorage, times(3)).updateAccountBalance(
            eq(registeredAccount.id),
            eq(KinBalance(registeredAccount.balance.amount -amountSpent - responseTransaction.fee.toKin()))
        )
        verify(mockStorage, times(3)).getStoredTransactions(eq(registeredAccount.id))
        verifyNoMoreInteractions(mockService)
        verifyNoMoreInteractions(mockStorage)
    }

    @Test
    fun sendThreeKinPayments_with_bad_sequence_number_for_first_two() {
        val destination = TestUtils.newKinAccount()
        val transactionToBeSent =
            SolanaKinTransaction(
                Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABhp+xHw13QTBg0/2iNL+W0wZ/W9fAF76qqZH8Al+djtT4xw1fIufa+fmDQ5EFlwziYixu4Xt3tqgIwcJm41DgCAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW94cG3LT0a+kX6wqa2O0qYbqC6rMebhyR0LBQgHRjoebjh8kk+ML92HthfRee5O1nEhhSh3G/n3/omSR1jJoT8qQbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCp/emg+dGNKfC5/F0EbUpvD4rCYPM6q/9+KMsPHsXv4esBAwMBAgEJA+CuuwAAAAAA")!!,
                networkEnvironment = networkEnvironment
            )
        val responseTransaction = SolanaKinTransaction(
            transactionToBeSent.bytesValue,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                KinTransaction.ResultCode.Success
            ),
            networkEnvironment
        )

        doAnswer {
            Promise.of(Optional.of(registeredAccount))
        }.whenever(mockStorage).getStoredAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(registeredAccount)
        }.whenever(mockService).getAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(transactionToBeSent)
        }.whenever(mockService).buildAndSignTransaction(
            eq(registeredAccount.key as Key.PrivateKey),
            eq(registeredAccount.key.asPublicKey()),
            eq(listOf(KinPaymentItem(KinAmount(123), destination.id))),
            eq(KinMemo.NONE),
            any(),
            any()
        )

        doAnswer {
            Promise.of(registeredAccount)
        }.whenever(mockStorage).updateAccountInStorage(eq(registeredAccount))

        doAnswer {
            Promise.of(registeredAccount.copy(tokenAccounts = listOf(registeredAccount.id.toKeyPair().asPublicKey())))
        }.whenever(mockStorage).updateAccountInStorage(eq(registeredAccount.copy(tokenAccounts = listOf(registeredAccount.id.toKeyPair().asPublicKey()))))

        doAnswer {
            Promise.of(listOf(responseTransaction))
        }.whenever(mockStorage).insertNewTransactionInStorage(
            eq(registeredAccount.id),
            eq(responseTransaction)
        )

        var i = -1

        doAnswer {
            if (++i == 0 || i == 2) {
                Promise.error(KinService.FatalError.BadBlockhashInRequest)
            } else {
                Promise.of(responseTransaction)
            }
        }.whenever(mockService).buildSignAndSubmitTransaction(any())

        val amountSpent = KinAmount(123)
        val updatedAccountWithNewBalance = registeredAccount.merge(
            KinAccount(
                registeredAccount.key,
                balance = KinBalance(amountSpent)
            )
        )

        doAnswer {
            Promise.of(Optional.of(updatedAccountWithNewBalance))
        }.whenever(mockStorage).updateAccountBalance(
            eq(registeredAccount.id),
            eq(KinBalance(registeredAccount.balance.amount - amountSpent - responseTransaction.fee.toKin()))
        )

        doAnswer {
            Promise.of(
                KinTransactions(
                    listOf(
                        responseTransaction,
                        responseTransaction,
                        responseTransaction
                    ), null, null
                )
            )
        }.whenever(mockStorage).getStoredTransactions(eq(registeredAccount.id))

        sut.sendKinPayment(KinAmount(123), destination.id).test(10) {
            assertNull(error)
            assertEquals(responseTransaction.asKinPayments().first(), value)
        }

        sut.sendKinPayment(KinAmount(123), destination.id).test(10) {
            assertNull(error)
            assertEquals(responseTransaction.asKinPayments().first(), value)
        }

        sut.sendKinPayment(KinAmount(123), destination.id).test(10) {
            assertNull(error)
            assertEquals(responseTransaction.asKinPayments().first(), value)
        }

        verify(mockStorage, times(10)).getStoredAccount(eq(registeredAccount.id))
        verify(mockService, times(2)).invalidateBlockhashCache()
        verify(mockService, times(5)).buildAndSignTransaction(
            eq(registeredAccount.key as Key.PrivateKey),
            eq(registeredAccount.key.asPublicKey()),
            eq(listOf(KinPaymentItem(KinAmount(123), destination.id))),
            eq(KinMemo.NONE),
            any(),
            any()
        )
        verify(
            mockService,
            times(5)
        ).buildSignAndSubmitTransaction(any()) // 5 due to 2 retries
        verify(mockStorage, times(3))
            .advanceSequence(eq(registeredAccount.id))
        verify(mockStorage, times(2)).updateAccountInStorage(eq(registeredAccount.copy(tokenAccounts = listOf(registeredAccount.id.toKeyPair().asPublicKey()))))
        verify(mockStorage, times(3)).insertNewTransactionInStorage(
            eq(registeredAccount.id),
            eq(responseTransaction)
        )
        verify(mockStorage, times(3)).updateAccountBalance(
            eq(registeredAccount.id),
            eq(KinBalance(registeredAccount.balance.amount - amountSpent - responseTransaction.fee.toKin()))
        )
        verify(mockStorage, times(3)).getStoredTransactions(eq(registeredAccount.id))
        verify(mockService, times(4)).resolveTokenAccounts(any())
        verify(mockStorage, times(2)).updateAccountInStorage(eq(registeredAccount.copy(tokenAccounts = listOf(
            registeredAccount.key.asPublicKey()))))
        verifyNoMoreInteractions(mockService)
        verifyNoMoreInteractions(mockStorage)
    }

    @Test
    fun sendKinPayment_with_bad_sequence_number_max_tries() {
        val destination = TestUtils.newKinAccount()
        val transactionToBeSent =
            SolanaKinTransaction(
                Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABhp+xHw13QTBg0/2iNL+W0wZ/W9fAF76qqZH8Al+djtT4xw1fIufa+fmDQ5EFlwziYixu4Xt3tqgIwcJm41DgCAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW94cG3LT0a+kX6wqa2O0qYbqC6rMebhyR0LBQgHRjoebjh8kk+ML92HthfRee5O1nEhhSh3G/n3/omSR1jJoT8qQbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCp/emg+dGNKfC5/F0EbUpvD4rCYPM6q/9+KMsPHsXv4esBAwMBAgEJA+CuuwAAAAAA")!!,
                networkEnvironment = networkEnvironment
            )
        val responseTransaction = SolanaKinTransaction(
            transactionToBeSent.bytesValue,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                KinTransaction.ResultCode.Success
            ),
            networkEnvironment
        )

        doAnswer {
            Promise.of(Optional.of(registeredAccount))
        }.whenever(mockStorage).getStoredAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(registeredAccount)
        }.whenever(mockService).getAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(transactionToBeSent)
        }.whenever(mockService).buildAndSignTransaction(
            eq(registeredAccount.key as Key.PrivateKey),
            eq(registeredAccount.key.asPublicKey()),
            eq(listOf(KinPaymentItem(KinAmount(123), destination.id))),
            eq(KinMemo.NONE),
            any(),
            any()
        )

        doAnswer {
            Promise.of(registeredAccount)
        }.whenever(mockStorage).updateAccountInStorage(eq(registeredAccount))

        doAnswer {
            Promise.of(registeredAccount.copy(tokenAccounts = listOf(registeredAccount.id.toKeyPair().asPublicKey())))
        }.whenever(mockStorage).updateAccountInStorage(eq(registeredAccount.copy(tokenAccounts = listOf(registeredAccount.id.toKeyPair().asPublicKey()))))

        doAnswer {
            Promise.of(listOf(responseTransaction))
        }.whenever(mockStorage).insertNewTransactionInStorage(
            eq(registeredAccount.id),
            eq(responseTransaction)
        )

        var i = -1

        doAnswer {
            Promise.error<KinTransaction>(KinService.FatalError.BadBlockhashInRequest)
        }.whenever(mockService).buildSignAndSubmitTransaction(any())

        val amountSpent = KinAmount(123)
        val updatedAccountWithNewBalance = registeredAccount.merge(
            KinAccount(
                registeredAccount.key,
                balance = KinBalance(amountSpent)
            )
        )

        doAnswer {
            Promise.of(Optional.of(updatedAccountWithNewBalance))
        }.whenever(mockStorage).updateAccountBalance(
            eq(registeredAccount.id),
            eq(KinBalance(registeredAccount.balance.amount - amountSpent - responseTransaction.fee.toKin()))
        )

        doAnswer {
            Promise.of(
                KinTransactions(
                    listOf(
                        responseTransaction,
                        responseTransaction,
                        responseTransaction
                    ), null, null
                )
            )
        }.whenever(mockStorage).getStoredTransactions(eq(registeredAccount.id))

        sut.sendKinPayment(KinAmount(123), destination.id).test(1000) {
            assertEquals(KinService.FatalError.BadBlockhashInRequest, error)
        }

        verify(mockStorage, times(12)).getStoredAccount(eq(registeredAccount.id))
        verify(mockService, times(6)).invalidateBlockhashCache()
        verify(mockService, times(6)).buildAndSignTransaction(
            eq(registeredAccount.key as Key.PrivateKey),
            eq(registeredAccount.key.asPublicKey()),
            eq(listOf(KinPaymentItem(KinAmount(123), destination.id))),
            eq(KinMemo.NONE),
            any(),
            any()
        )
        verify(
            mockService,
            times(6)
        ).buildSignAndSubmitTransaction(any()) // 6 due to 6 retries
        verify(mockStorage, times(0))
            .advanceSequence(eq(registeredAccount.id))
        verify(mockStorage, times(5)).updateAccountInStorage(eq(registeredAccount.copy(tokenAccounts = listOf(registeredAccount.id.toKeyPair().asPublicKey()))))
        verify(mockStorage, times(0)).insertNewTransactionInStorage(
            eq(registeredAccount.id),
            eq(responseTransaction)
        )
        verify(mockStorage, times(0)).updateAccountBalance(
            eq(registeredAccount.id),
            eq(KinBalance(registeredAccount.balance.amount - amountSpent - responseTransaction.fee.toKin()))
        )
        verify(mockStorage, times(0)).getStoredTransactions(eq(registeredAccount.id))
        verify(mockService, times(10)).resolveTokenAccounts(any())
        verify(mockStorage, times(5)).updateAccountInStorage(eq(registeredAccount.copy(tokenAccounts = listOf(
            registeredAccount.key.asPublicKey()))))
        verifyNoMoreInteractions(mockService)
        verifyNoMoreInteractions(mockStorage)
    }

    @Test
    fun sendKinPayment_with_unknown_error() {
        val destination = TestUtils.newKinAccount()
        val transactionToBeSent =
            SolanaKinTransaction(
                Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABhp+xHw13QTBg0/2iNL+W0wZ/W9fAF76qqZH8Al+djtT4xw1fIufa+fmDQ5EFlwziYixu4Xt3tqgIwcJm41DgCAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW94cG3LT0a+kX6wqa2O0qYbqC6rMebhyR0LBQgHRjoebjh8kk+ML92HthfRee5O1nEhhSh3G/n3/omSR1jJoT8qQbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCp/emg+dGNKfC5/F0EbUpvD4rCYPM6q/9+KMsPHsXv4esBAwMBAgEJA+CuuwAAAAAA")!!,
                networkEnvironment = networkEnvironment
            )
        val responseTransaction = SolanaKinTransaction(
            transactionToBeSent.bytesValue,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                KinTransaction.ResultCode.Success
            ),
            networkEnvironment
        )

        doAnswer {
            Promise.of(Optional.of(registeredAccount))
        }.whenever(mockStorage).getStoredAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(registeredAccount)
        }.whenever(mockService).getAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(transactionToBeSent)
        }.whenever(mockService).buildAndSignTransaction(
            eq(registeredAccount.key as Key.PrivateKey),
            eq(registeredAccount.key.asPublicKey()),
            eq(listOf(KinPaymentItem(KinAmount(123), destination.id))),
            eq(KinMemo.NONE),
            any(),
            any()
        )

        doAnswer {
            Promise.of(registeredAccount)
        }.whenever(mockStorage).updateAccountInStorage(eq(registeredAccount))

        doAnswer {
            Promise.of(listOf(responseTransaction))
        }.whenever(mockStorage).insertNewTransactionInStorage(
            eq(registeredAccount.id),
            eq(responseTransaction)
        )

        var i = -1
        doAnswer {
            Promise.error<KinTransaction>(IllegalArgumentException("explosion!"))
        }.whenever(mockService).buildSignAndSubmitTransaction(any())


        val amountSpent = KinAmount(123)
        val updatedAccountWithNewBalance = registeredAccount.merge(
            KinAccount(
                registeredAccount.key,
                balance = KinBalance(amountSpent)
            )
        )

        doAnswer {
            Promise.of(Optional.of(updatedAccountWithNewBalance))
        }.whenever(mockStorage).updateAccountBalance(
            eq(registeredAccount.id),
            eq(KinBalance(registeredAccount.balance.amount - amountSpent - responseTransaction.fee.toKin()))
        )

        doAnswer {
            Promise.of(
                KinTransactions(
                    listOf(
                        responseTransaction,
                        responseTransaction,
                        responseTransaction
                    ), null, null
                )
            )
        }.whenever(mockStorage).getStoredTransactions(eq(registeredAccount.id))

        sut.sendKinPayment(KinAmount(123), destination.id).test(10) {
            assertTrue { error is IllegalArgumentException }
        }

        verify(mockStorage, times(2)).getStoredAccount(eq(registeredAccount.id))
        verify(mockService, times(0)).getAccount(eq(registeredAccount.id))
        verify(mockService, times(1)).buildAndSignTransaction(
            eq(registeredAccount.key as Key.PrivateKey),
            eq(registeredAccount.key.asPublicKey()),
            eq(listOf(KinPaymentItem(KinAmount(123), destination.id))),
            eq(KinMemo.NONE),
            any(),
            any()
        )
        verify(
            mockService,
            times(1)
        ).buildSignAndSubmitTransaction(any()) // 2 due to 2 retries
        verify(mockStorage, times(0))
            .advanceSequence(eq(registeredAccount.id))
        verify(mockStorage, times(0)).updateAccountInStorage(eq(registeredAccount))
        verify(mockStorage, times(0)).insertNewTransactionInStorage(
            eq(registeredAccount.id),
            eq(responseTransaction)
        )
        verify(mockStorage, times(0)).updateAccountBalance(
            eq(registeredAccount.id),
            eq(KinBalance(registeredAccount.balance.amount - amountSpent - responseTransaction.fee.toKin()))
        )
        verify(mockStorage, times(0)).getStoredTransactions(eq(registeredAccount.id))
        verifyNoMoreInteractions(mockService)
        verifyNoMoreInteractions(mockStorage)
    }

    @Test
    fun sendMultipleKinPayments_receiveMultiplePayments_requestNextPage_success() {
        val destination = TestUtils.newKinAccount()
        val transactionToBeSent =
            SolanaKinTransaction(
                Base64.decodeBase64("\"AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkPkcGwNdqhA/6R+ZuA0wcDK9Dlf/2uoJKlIUD58nNl7Ki6cZSUxX6NyQp7Ieh7maV7R5hVgzKxqgVTGOQ9Y8FAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW9XfCd1GR52OfreEYpI0+El+wlY/bIiBBO48k81/+47PBXOdIQ9PhPKO/g0xviztNdJriWX40Gi+LNZoEgxD3s0gbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCpNJwYNsvTYodJyw+tKWxTwIzLleQf2SwAj02FZb4ES9EBAwMBAgEJA6CGAQAAAAAA")!!,
                networkEnvironment = networkEnvironment
            )
        val responseTransaction = SolanaKinTransaction(
            transactionToBeSent.bytesValue,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                KinTransaction.ResultCode.Success
            ),
            networkEnvironment
        )
        val historicalTransaction1 = SolanaKinTransaction(
            Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkPkcGwNdqhA/6R+ZuA0wcDK9Dlf/2uoJKlIUD58nNl7Ki6cZSUxX6NyQp7Ieh7maV7R5hVgzKxqgVTGOQ9Y8FAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW9XfCd1GR52OfreEYpI0+El+wlY/bIiBBO48k81/+47PBXOdIQ9PhPKO/g0xviztNdJriWX40Gi+LNZoEgxD3s0gbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCpNJwYNsvTYodJyw+tKWxTwIzLleQf2SwAj02FZb4ES9EBAwMBAgEJA6CGAQAAAAAA")!!,
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                KinTransaction.ResultCode.Success,
                KinTransaction.PagingToken("1231412342453245")
            ),
            networkEnvironment
        )
        val historicalTransaction2 = SolanaKinTransaction(
            Base64.decodeBase64(" AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABJ/7uglDgA+Guf9/TlKIydsbNVKow3IMxNGJkXFEgYQjRBeWrUFxzbcTZ/Qm9Byd0g5Bg3L5llEB4ddDP9CgMAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW9XfCd1GR52OfreEYpI0+El+wlY/bIiBBO48k81/+47PBXOdIQ9PhPKO/g0xviztNdJriWX40Gi+LNZoEgxD3s0gbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCpNJwYNsvTYodJyw+tKWxTwIzLleQf2SwAj02FZb4ES9EBAwMBAgEJA0ANAwAAAAAA")!!,
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                KinTransaction.ResultCode.Success,
                KinTransaction.PagingToken("1231412342453245")
            ),
            networkEnvironment
        )
        val historicalTransaction3 = SolanaKinTransaction(
            Base64.decodeBase64(" AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADOYWAeT5yvPjE7XndVVObPE8sZTA2pE3uzDGMzLqATXzxy/BFV/5NMwTSuanQ3obq2mIV3q0U1xI5CV7n2Ab4DAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW9XfCd1GR52OfreEYpI0+El+wlY/bIiBBO48k81/+47PBXOdIQ9PhPKO/g0xviztNdJriWX40Gi+LNZoEgxD3s0gbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCpNJwYNsvTYodJyw+tKWxTwIzLleQf2SwAj02FZb4ES9EBAwMBAgEJA+CTBAAAAAAA")!!,
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                KinTransaction.ResultCode.Success,
                KinTransaction.PagingToken("1231412342453245")
            ),
            networkEnvironment
        )
        val historicalTransaction4 = SolanaKinTransaction(
            Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACnqf21k4NHre9V/YFu8ezrzoiPq+l4JpJDxDpl3Xj1dVFwpf7+MGRZoixbwFM7DleQccAWJ8Nj3ruL3rhuLdUPAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW9XfCd1GR52OfreEYpI0+El+wlY/bIiBBO48k81/+47PBXOdIQ9PhPKO/g0xviztNdJriWX40Gi+LNZoEgxD3s0gbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCpNJwYNsvTYodJyw+tKWxTwIzLleQf2SwAj02FZb4ES9EBAwMBAgEJA4AaBgAAAAAA")!!,
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                KinTransaction.ResultCode.Success,
                KinTransaction.PagingToken("1231412342453245")
            ),
            networkEnvironment
        )
        val historicalTransaction5 = SolanaKinTransaction(
            Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUW7tRJEwBNyDeZHPK6nz6EgkpP0hSnWNk4DwCBCLWlrgmjIern9Afwk4AYIZ3qe5do6a6PdjVZTxZVkqRT84IAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW9XfCd1GR52OfreEYpI0+El+wlY/bIiBBO48k81/+47PBXOdIQ9PhPKO/g0xviztNdJriWX40Gi+LNZoEgxD3s0gbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCpNJwYNsvTYodJyw+tKWxTwIzLleQf2SwAj02FZb4ES9EBAwMBAgEJAyChBwAAAAAA")!!,
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                KinTransaction.ResultCode.Success,
                KinTransaction.PagingToken("1231412342453245")
            ),
            networkEnvironment
        )

        val historicalTransactionList = listOf(
            historicalTransaction5,
            historicalTransaction4,
            historicalTransaction3,
            historicalTransaction2,
            historicalTransaction1
        )

        doAnswer {
            Promise.of(Optional.of(registeredAccount))
        }.whenever(mockStorage).getStoredAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(transactionToBeSent)
        }.whenever(mockService).buildAndSignTransaction(
            eq(registeredAccount.key as Key.PrivateKey),
            eq(registeredAccount.key.asPublicKey()),
            eq(listOf(KinPaymentItem(KinAmount(1), destination.id))),
            eq(KinMemo.NONE),
            any(),
            any()
        )

        doAnswer {
            Promise.of(transactionToBeSent)
        }.whenever(mockService).buildAndSignTransaction(
            eq(registeredAccount.key as Key.PrivateKey),
            eq(registeredAccount.key.asPublicKey()),
            eq(listOf(KinPaymentItem(KinAmount(2), destination.id))),
            eq(KinMemo.NONE),
            any(),
            any()
        )

        doAnswer {
            Promise.of(transactionToBeSent)
        }.whenever(mockService).buildAndSignTransaction(
            eq(registeredAccount.key as Key.PrivateKey),
            eq(registeredAccount.key.asPublicKey()),
            eq(listOf(KinPaymentItem(KinAmount(3), destination.id))),
            eq(KinMemo.NONE),
            any(),
            any()
        )

        doAnswer {
            Promise.of(responseTransaction)
        }.whenever(mockService).submitTransaction(eq(transactionToBeSent))

        doAnswer {
            Promise.of(registeredAccount)
        }.whenever(mockStorage).updateAccountInStorage(eq(registeredAccount))

        doAnswer {
            Promise.of(listOf(responseTransaction))
        }.whenever(mockStorage).insertNewTransactionInStorage(
            eq(registeredAccount.id),
            eq(responseTransaction)
        )

        doAnswer {
            Promise.of(
                KinTransactions(
                    listOf(
                        historicalTransaction3,
                        historicalTransaction2,
                        historicalTransaction1
                    ), pagingToken, null
                )
            )
        }.whenever(mockStorage).getStoredTransactions(eq(registeredAccount.id))

        doAnswer {
            Promise.of(historicalTransactionList)
        }.whenever(mockService).getTransactionPage(
            eq(registeredAccount.id),
            eq(pagingToken),
            eq(KinService.Order.Ascending)
        )

        doAnswer {
            Promise.of(historicalTransactionList)
        }.whenever(mockStorage).upsertNewTransactionsInStorage(
            eq(registeredAccount.id),
            eq(historicalTransactionList.reversed())
        )

        // 2

        doAnswer {
            Promise.of(Optional.of(registeredAccount2))
        }.whenever(mockStorage2).getStoredAccount(eq(registeredAccount2.id))

        doAnswer {
            Promise.of(transactionToBeSent)
        }.whenever(mockService2).buildAndSignTransaction(
            eq(registeredAccount.key as Key.PrivateKey),
            eq(registeredAccount2.key.asPublicKey()),
            eq(listOf(KinPaymentItem(KinAmount(4), registeredAccount.id))),
            eq(KinMemo.NONE),
            any(),
            any()
        )

        doAnswer {
            Promise.of(transactionToBeSent)
        }.whenever(mockService2).buildAndSignTransaction(
            eq(registeredAccount.key as Key.PrivateKey),
            eq(registeredAccount2.key.asPublicKey()),
            eq(listOf(KinPaymentItem(KinAmount(5), registeredAccount.id))),
            eq(KinMemo.NONE),
            any(),
            any()
        )

        doAnswer {
            Promise.of(responseTransaction)
        }.whenever(mockService2).submitTransaction(eq(transactionToBeSent))

        doAnswer {
            Promise.of(registeredAccount2)
        }.whenever(mockStorage2).updateAccountInStorage(eq(registeredAccount2))

        doAnswer {
            Promise.of(listOf(responseTransaction))
        }.whenever(mockStorage2).insertNewTransactionInStorage(
            eq(registeredAccount2.id),
            eq(responseTransaction)
        )

        val amountSpent = KinAmount(123)
        val updatedAccountWithNewBalance = registeredAccount.merge(
            KinAccount(
                registeredAccount.key,
                balance = KinBalance(amountSpent)
            )
        )

        doAnswer {
            Promise.of(Optional.of(updatedAccountWithNewBalance))
        }.whenever(mockStorage).updateAccountBalance(
            eq(registeredAccount.id),
            eq(KinBalance(registeredAccount.balance.amount - amountSpent - responseTransaction.fee.toKin()))
        )

        doAnswer {
            Promise.of(Optional.of(updatedAccountWithNewBalance))
        }.whenever(mockStorage2)
            .updateAccountBalance(
                eq(registeredAccount2.id),
                eq(KinBalance(registeredAccount.balance.amount - amountSpent - responseTransaction.fee.toKin()))
            )

        val values = mutableListOf<List<String>>()

        val latch = CountDownLatch(2)
        sut.observePayments()
            .add {
                val localValues = mutableListOf<String>()
                it.forEach {
                    println(it.amount)
                    localValues += it.amount.toString(0)
                }
                values += localValues
                println("-----")
                latch.countDown()
            }

        latch.await(10, TimeUnit.SECONDS)
        assertEquals(listOf(listOf("3", "2", "1"), listOf("5", "4", "3", "2", "1")), values)
    }

    @Test
    fun sendMultipleKinPayments_receiveMultiplePayments_requestPreviousPage_success() {

        val destination = TestUtils.newKinAccount()
        val transactionToBeSent =
            SolanaKinTransaction(
                Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkPkcGwNdqhA/6R+ZuA0wcDK9Dlf/2uoJKlIUD58nNl7Ki6cZSUxX6NyQp7Ieh7maV7R5hVgzKxqgVTGOQ9Y8FAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW9XfCd1GR52OfreEYpI0+El+wlY/bIiBBO48k81/+47PBXOdIQ9PhPKO/g0xviztNdJriWX40Gi+LNZoEgxD3s0gbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCpNJwYNsvTYodJyw+tKWxTwIzLleQf2SwAj02FZb4ES9EBAwMBAgEJA6CGAQAAAAAA")!!,
                networkEnvironment = networkEnvironment
            )
        val responseTransaction = SolanaKinTransaction(
            transactionToBeSent.bytesValue,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                KinTransaction.ResultCode.Success
            ),
            networkEnvironment
        )
        val historicalTransaction1 = SolanaKinTransaction(
            Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkPkcGwNdqhA/6R+ZuA0wcDK9Dlf/2uoJKlIUD58nNl7Ki6cZSUxX6NyQp7Ieh7maV7R5hVgzKxqgVTGOQ9Y8FAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW9XfCd1GR52OfreEYpI0+El+wlY/bIiBBO48k81/+47PBXOdIQ9PhPKO/g0xviztNdJriWX40Gi+LNZoEgxD3s0gbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCpNJwYNsvTYodJyw+tKWxTwIzLleQf2SwAj02FZb4ES9EBAwMBAgEJA6CGAQAAAAAA")!!,
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                KinTransaction.ResultCode.Success,
                KinTransaction.PagingToken("1231412342453245")
            ),
            networkEnvironment
        )
        val historicalTransaction2 = SolanaKinTransaction(
            Base64.decodeBase64(" AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABJ/7uglDgA+Guf9/TlKIydsbNVKow3IMxNGJkXFEgYQjRBeWrUFxzbcTZ/Qm9Byd0g5Bg3L5llEB4ddDP9CgMAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW9XfCd1GR52OfreEYpI0+El+wlY/bIiBBO48k81/+47PBXOdIQ9PhPKO/g0xviztNdJriWX40Gi+LNZoEgxD3s0gbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCpNJwYNsvTYodJyw+tKWxTwIzLleQf2SwAj02FZb4ES9EBAwMBAgEJA0ANAwAAAAAA")!!,
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                KinTransaction.ResultCode.Success,
                KinTransaction.PagingToken("1231412342453245")
            ),
            networkEnvironment
        )
        val historicalTransaction3 = SolanaKinTransaction(
            Base64.decodeBase64(" AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADOYWAeT5yvPjE7XndVVObPE8sZTA2pE3uzDGMzLqATXzxy/BFV/5NMwTSuanQ3obq2mIV3q0U1xI5CV7n2Ab4DAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW9XfCd1GR52OfreEYpI0+El+wlY/bIiBBO48k81/+47PBXOdIQ9PhPKO/g0xviztNdJriWX40Gi+LNZoEgxD3s0gbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCpNJwYNsvTYodJyw+tKWxTwIzLleQf2SwAj02FZb4ES9EBAwMBAgEJA+CTBAAAAAAA")!!,
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                KinTransaction.ResultCode.Success,
                KinTransaction.PagingToken("1231412342453245")
            ),
            networkEnvironment
        )
        val historicalTransaction4 = SolanaKinTransaction(
            Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACnqf21k4NHre9V/YFu8ezrzoiPq+l4JpJDxDpl3Xj1dVFwpf7+MGRZoixbwFM7DleQccAWJ8Nj3ruL3rhuLdUPAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW9XfCd1GR52OfreEYpI0+El+wlY/bIiBBO48k81/+47PBXOdIQ9PhPKO/g0xviztNdJriWX40Gi+LNZoEgxD3s0gbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCpNJwYNsvTYodJyw+tKWxTwIzLleQf2SwAj02FZb4ES9EBAwMBAgEJA4AaBgAAAAAA")!!,
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                KinTransaction.ResultCode.Success,
                KinTransaction.PagingToken("1231412342453245")
            ),
            networkEnvironment
        )
        val historicalTransaction5 = SolanaKinTransaction(
            Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUW7tRJEwBNyDeZHPK6nz6EgkpP0hSnWNk4DwCBCLWlrgmjIern9Afwk4AYIZ3qe5do6a6PdjVZTxZVkqRT84IAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW9XfCd1GR52OfreEYpI0+El+wlY/bIiBBO48k81/+47PBXOdIQ9PhPKO/g0xviztNdJriWX40Gi+LNZoEgxD3s0gbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCpNJwYNsvTYodJyw+tKWxTwIzLleQf2SwAj02FZb4ES9EBAwMBAgEJAyChBwAAAAAA")!!,
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                KinTransaction.ResultCode.Success,
                KinTransaction.PagingToken("1231412342453245")
            ),
            networkEnvironment
        )

        val historicalTransactionList = listOf(
            historicalTransaction5,
            historicalTransaction4,
            historicalTransaction3,
            historicalTransaction2,
            historicalTransaction1
        )

        doAnswer {
            Promise.of(Optional.of(registeredAccount))
        }.whenever(mockStorage).getStoredAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(transactionToBeSent)
        }.whenever(mockService).buildAndSignTransaction(
            eq(registeredAccount.key as Key.PrivateKey),
            eq(registeredAccount.key.asPublicKey()),
            eq(listOf(KinPaymentItem(KinAmount(3), destination.id))),
            eq(KinMemo.NONE),
            any(),
            any()
        )
        doAnswer {
            Promise.of(transactionToBeSent)
        }.whenever(mockService).buildAndSignTransaction(
            eq(registeredAccount.key as Key.PrivateKey),
            eq(registeredAccount.key.asPublicKey()),
            eq(listOf(KinPaymentItem(KinAmount(4), destination.id))),
            eq(KinMemo.NONE),
            any(),
            any()
        )

        doAnswer {
            Promise.of(transactionToBeSent)
        }.whenever(mockService).buildAndSignTransaction(
            eq(registeredAccount.key as Key.PrivateKey),
            eq(registeredAccount.key.asPublicKey()),
            eq(listOf(KinPaymentItem(KinAmount(5), destination.id))),
            eq(KinMemo.NONE),
            any(),
            any()
        )

        doAnswer {
            Promise.of(responseTransaction)
        }.whenever(mockService).submitTransaction(eq(transactionToBeSent))

        doAnswer {
            Promise.of(registeredAccount)
        }.whenever(mockStorage).updateAccountInStorage(eq(registeredAccount))

        doAnswer {
            Promise.of(listOf(responseTransaction))
        }.whenever(mockStorage).insertNewTransactionInStorage(
            eq(registeredAccount.id),
            eq(responseTransaction)
        )

        doAnswer {
            Promise.of(
                KinTransactions(
                    listOf(
                        historicalTransaction5,
                        historicalTransaction4,
                        historicalTransaction3
                    ), null, pagingToken
                )
            )
        }.whenever(mockStorage).getStoredTransactions(eq(registeredAccount.id))

        doAnswer {
            Promise.of(listOf(historicalTransaction2, historicalTransaction1))
        }.whenever(mockService).getTransactionPage(
            eq(registeredAccount.id),
            eq(pagingToken),
            eq(KinService.Order.Descending)
        )

        doAnswer {
            Promise.of(historicalTransactionList)
        }.whenever(mockStorage).upsertOldTransactionsInStorage(
            eq(registeredAccount.id),
            eq(listOf(historicalTransaction2, historicalTransaction1))
        )

        val amountSpent = KinAmount(123)
        val updatedAccountWithNewBalance = registeredAccount.merge(
            KinAccount(
                registeredAccount.key,
                balance = KinBalance(amountSpent)
            )
        )

        doAnswer {
            Promise.of(Optional.of(updatedAccountWithNewBalance))
        }.whenever(mockStorage).updateAccountBalance(eq(registeredAccount.id), eq(KinBalance(registeredAccount.balance.amount - amountSpent)))

        val values = mutableListOf<List<String>>()

        sut.observePayments()
            .add {
                val localValues = mutableListOf<String>()
                it.forEach {
                    println(it.amount)
                    localValues += it.amount.toString(0)
                }
                values += localValues
                println("-----")
            }
            .requestPreviousPage()
            .test(timeout = 10) {
                assertEquals(listOf(listOf("5", "4", "3"), listOf("5", "4", "3", "2", "1")), values)
            }
    }

    @Test
    fun observePayments_actively_success() {
        val transactionToBeSent =
            SolanaKinTransaction(
                Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABhp+xHw13QTBg0/2iNL+W0wZ/W9fAF76qqZH8Al+djtT4xw1fIufa+fmDQ5EFlwziYixu4Xt3tqgIwcJm41DgCAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW94cG3LT0a+kX6wqa2O0qYbqC6rMebhyR0LBQgHRjoebjh8kk+ML92HthfRee5O1nEhhSh3G/n3/omSR1jJoT8qQbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCp/emg+dGNKfC5/F0EbUpvD4rCYPM6q/9+KMsPHsXv4esBAwMBAgEJA+CuuwAAAAAA")!!,
                networkEnvironment = networkEnvironment
            )
        val responseTransaction = SolanaKinTransaction(
            transactionToBeSent.bytesValue,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                KinTransaction.ResultCode.Success
            ),
            networkEnvironment
        )

        val subject = ValueSubject<KinAccount>()

        subject.onNext(registeredAccount)

        doAnswer {
            subject
        }.whenever(mockService).streamAccount(eq(registeredAccount.id))

        val stream = ValueSubject<KinTransaction>()
        stream.onNext(responseTransaction)
        whenever(mockService.streamNewTransactions(eq(registeredAccount.id)))
            .thenReturn(stream)

        doAnswer {
            Promise.of(registeredAccount)
        }.whenever(mockStorage).updateAccountInStorage(eq(registeredAccount))

        doAnswer {
            Promise.of(null)
        }.whenever(mockStorage).getStoredTransactions(eq(registeredAccount.id))

        doAnswer {
            Promise.of(listOf(responseTransaction))
        }.whenever(mockService).getLatestTransactions(eq(registeredAccount.id))

        doAnswer {
            Promise.of(listOf(responseTransaction))
        }.whenever(mockStorage).upsertNewTransactionsInStorage(
            eq(registeredAccount.id),
            eq(listOf(responseTransaction))
        )

        val lifecycle = DisposeBag()

        sut.observePayments(ObservationMode.Active).test {
            assertEquals(responseTransaction.asKinPayments(), value)
        }.disposedBy(lifecycle)

        lifecycle.dispose()
    }

    @Test
    fun observePayments_newActiveOnly_success() {
        val transactionToBeSent =
            SolanaKinTransaction(
                Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABhp+xHw13QTBg0/2iNL+W0wZ/W9fAF76qqZH8Al+djtT4xw1fIufa+fmDQ5EFlwziYixu4Xt3tqgIwcJm41DgCAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW94cG3LT0a+kX6wqa2O0qYbqC6rMebhyR0LBQgHRjoebjh8kk+ML92HthfRee5O1nEhhSh3G/n3/omSR1jJoT8qQbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCp/emg+dGNKfC5/F0EbUpvD4rCYPM6q/9+KMsPHsXv4esBAwMBAgEJA+CuuwAAAAAA")!!,
                networkEnvironment = networkEnvironment
            )
        val responseTransaction = SolanaKinTransaction(
            transactionToBeSent.bytesValue,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                KinTransaction.ResultCode.Success
            ),
            networkEnvironment
        )

        val stream = ValueSubject<KinTransaction>()
        stream.onNext(responseTransaction)
        whenever(mockService.streamNewTransactions(eq(registeredAccount.id)))
            .thenReturn(stream)

        val lifecycle = DisposeBag()

        sut.observePayments(ObservationMode.ActiveNewOnly).test {
            assertEquals(responseTransaction.asKinPayments(), value)
        }.disposedBy(lifecycle)

        lifecycle.dispose()
    }

    @Test
    fun test_getPaymentsForTransactionHash() {

        val transaction =
            SolanaKinTransaction(
                Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABhp+xHw13QTBg0/2iNL+W0wZ/W9fAF76qqZH8Al+djtT4xw1fIufa+fmDQ5EFlwziYixu4Xt3tqgIwcJm41DgCAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW94cG3LT0a+kX6wqa2O0qYbqC6rMebhyR0LBQgHRjoebjh8kk+ML92HthfRee5O1nEhhSh3G/n3/omSR1jJoT8qQbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCp/emg+dGNKfC5/F0EbUpvD4rCYPM6q/9+KMsPHsXv4esBAwMBAgEJA+CuuwAAAAAA")!!,
                networkEnvironment = networkEnvironment
            )

        doAnswer {
            Promise.of(transaction)
        }.whenever(mockService).getTransaction(eq(TransactionHash("aabbcc12")))

        sut.getPaymentsForTransactionHash(TransactionHash("aabbcc12")).test {
            assertEquals(transaction.asKinPayments().first(), value?.first())
        }
    }
}
