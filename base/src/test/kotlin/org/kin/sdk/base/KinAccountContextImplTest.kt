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
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.KinDateFormat
import org.kin.sdk.base.models.KinMemo
import org.kin.sdk.base.models.KinPayment
import org.kin.sdk.base.models.KinPaymentItem
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.TransactionHash
import org.kin.sdk.base.models.asKinPayments
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.models.merge
import org.kin.sdk.base.models.toKin
import org.kin.sdk.base.network.services.KinService
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.KinTransactions
import org.kin.sdk.base.stellar.models.NetworkEnvironment
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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KinAccountContextImplTest {

    companion object {
        val registeredAccount = TestUtils.newKinAccount()
            .updateStatus(KinAccount.Status.Registered(1234))
        val registeredAccount2 = TestUtils.newSigningKinAccount()
            .updateStatus(KinAccount.Status.Registered(1234))
        val unregisteredAccount = TestUtils.newKinAccount()

        val fee = QuarkAmount(100)

        val pagingToken = KinTransaction.PagingToken("16576645322248192")
        val historicalKinTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=",
            KinTransaction.RecordType.Historical(
                KinDateFormat("2019-12-12T21:32:43Z").timestamp,
                com.sun.org.apache.xml.internal.security.utils.Base64.decode("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA="),
                pagingToken
            )
        )
        val networkEnvironment = NetworkEnvironment.KinStellarTestNet
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
            on { canWhitelistTransactions() } doReturn Promise.of(false)
            on { getMinFee() } doReturn Promise.of(fee)
        }
        mockStorage = mock {
            on { getMinFee() } doReturn Promise.of(Optional.of(fee))
        }

        mockService2 = mock {
            on { canWhitelistTransactions() } doReturn Promise.of(false)
            on { getMinFee() } doReturn Promise.of(fee)
        }
        mockStorage2 = mock {
            on { getMinFee() } doReturn Promise.of(Optional.of(fee))
        }

        sut = KinAccountContext.Builder(
            KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
                .setKinService(mockService)
                .setExecutorServices(excecutors)
                .setStorage(mockStorage)
        ).useExistingAccount(registeredAccount.id).build()

        sut2 = KinAccountContext.Builder(
            KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
                .setKinService(mockService2)
                .setExecutorServices(excecutors)
                .setStorage(mockStorage2)
        ).useExistingAccount(registeredAccount2.id).build()
    }

    @Test
    fun testNewAccountBuilder() {
        val context = KinAccountContext.Builder(
            KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
                .setKinService(mockService)
                .setExecutorServices(excecutors)
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
            Promise.error<KinAccount>(KinService.SDKUpgradeRequired)
        }.whenever(mockService).createAccount(any())

        sut.getAccount().test {
            assertNotNull(error)
            assertNull(value)

            verify(mockStorage).getStoredAccount(eq(registeredAccount.id))
            verify(mockService).createAccount(any())
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
    fun getSigningAccount_needs_create_success() {
        val accountId = registeredAccount.id
        val unregisteredAccount = registeredAccount.updateStatus(KinAccount.Status.Unregistered)
        val registeredAccountNoPrivKey =
            KinAccount(registeredAccount.key.asPublicKey(), status = registeredAccount.status)
        doAnswer {
            Promise.of(registeredAccountNoPrivKey)
        }.whenever(mockService).createAccount(eq(accountId))

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

            verify(mockService).createAccount(eq(accountId))
            verify(mockStorage).getStoredAccount(eq(accountId))
            verify(mockStorage).updateAccount(eq(registeredAccount))
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
            assertEquals(listOf(KinBalance(KinAmount.ZERO), KinBalance(KinAmount(123))), values)
            verify(mockStorage).getStoredAccount(eq(registeredAccount.id))
            verify(mockService).getAccount(eq(registeredAccount.id))
            verify(mockStorage).updateAccountInStorage(eq(accountFromService))
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
            assertEquals(listOf(KinBalance(KinAmount.ZERO), KinBalance(KinAmount(123))), values)
            verify(mockStorage).getStoredAccount(eq(registeredAccount.id))
            verify(mockService).getAccount(eq(registeredAccount.id))
            verify(mockStorage).updateAccountInStorage(eq(accountFromService))
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
            assertEquals(listOf(KinBalance(KinAmount.ZERO), KinBalance(KinAmount(123))), values)
            verify(mockStorage).getStoredAccount(eq(registeredAccount.id))
            verify(mockService).getAccount(eq(registeredAccount.id))
            verify(mockStorage).updateAccountInStorage(eq(accountFromService))
        }
    }

    @Test
    fun watchPayments_noneInStorage_fetchFromService_success() {

        val latestKinTransactions = listOf(historicalKinTransaction)
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

        val latestKinTransactions = listOf(historicalKinTransaction)
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
            KinTransaction(
                Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")!!,
                networkEnvironment = networkEnvironment
            )
        val responseTransaction = KinTransaction(
            transactionToBeSent.envelopeXdrBytes,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
            ),
            networkEnvironment
        )

        doAnswer {
            Promise.of(Optional.of(registeredAccount))
        }.whenever(mockStorage).getStoredAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(transactionToBeSent)
        }.whenever(mockService).buildAndSignTransaction(
            eq(registeredAccount),
            eq(listOf(KinPaymentItem(KinAmount(123), destination.id))),
            eq(KinMemo.NONE),
            eq(fee)
        )

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
        }.whenever(mockStorage).deductFromAccountBalance(
            eq(registeredAccount.id),
            eq(amountSpent + responseTransaction.fee.toKin())
        )

        doAnswer {
            Promise.of(KinTransactions(listOf(responseTransaction), null, null))
        }.whenever(mockStorage).getStoredTransactions(eq(registeredAccount.id))

        sut.sendKinPayment(KinAmount(123), destination.id).test(10) {
            assertNull(error)
            assertEquals(responseTransaction.asKinPayments().first(), value)

            verify(mockStorage).getStoredAccount(eq(registeredAccount.id))
            verify(mockService).buildAndSignTransaction(
                eq(registeredAccount),
                eq(listOf(KinPaymentItem(KinAmount(123), destination.id))),
                eq(KinMemo.NONE),
                eq(fee)
            )
            verify(mockService).canWhitelistTransactions()
            verify(mockStorage).getMinFee()
            verify(mockService).submitTransaction(eq(transactionToBeSent))
            verify(mockStorage).advanceSequence(eq(registeredAccount.id))
            verify(mockStorage).insertNewTransactionInStorage(
                eq(registeredAccount.id),
                eq(responseTransaction)
            )
            verify(mockStorage).deductFromAccountBalance(
                eq(registeredAccount.id),
                eq(amountSpent + responseTransaction.fee.toKin())
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
            KinTransaction(
                Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")!!,
                networkEnvironment = networkEnvironment
            )
        val responseTransaction = KinTransaction(
            transactionToBeSent.envelopeXdrBytes,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
            ),
            networkEnvironment
        )

        doAnswer {
            Promise.of(Optional.of(registeredAccount))
        }.whenever(mockStorage).getStoredAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(transactionToBeSent)
        }.whenever(mockService).buildAndSignTransaction(
            eq(registeredAccount),
            eq(listOf(KinPaymentItem(KinAmount(123), destination.id))),
            eq(KinMemo.NONE),
            eq(fee)
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

        val amountSpent = KinAmount(123)
        val updatedAccountWithNewBalance = registeredAccount.merge(
            KinAccount(
                registeredAccount.key,
                balance = KinBalance(amountSpent)
            )
        )

        doAnswer {
            Promise.of(Optional.of(updatedAccountWithNewBalance))
        }.whenever(mockStorage).deductFromAccountBalance(
            eq(registeredAccount.id),
            eq(amountSpent + responseTransaction.fee.toKin())
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

        verify(mockStorage, times(3)).getStoredAccount(eq(registeredAccount.id))
        verify(mockService, times(3)).buildAndSignTransaction(
            eq(registeredAccount),
            eq(listOf(KinPaymentItem(KinAmount(123), destination.id))),
            eq(KinMemo.NONE),
            eq(fee)
        )
        verify(mockService, times(3)).submitTransaction(eq(transactionToBeSent))
        verify(mockStorage, times(3)).advanceSequence(eq(registeredAccount.id))
        verify(mockStorage, times(3)).insertNewTransactionInStorage(
            eq(registeredAccount.id),
            eq(responseTransaction)
        )
        verify(mockStorage, times(3)).deductFromAccountBalance(
            eq(registeredAccount.id),
            eq(amountSpent + responseTransaction.fee.toKin())
        )
        verify(mockStorage, times(3)).getStoredTransactions(eq(registeredAccount.id))
        verify(mockService, times(3)).canWhitelistTransactions()
        verify(mockStorage, times(3)).getMinFee()
        verifyNoMoreInteractions(mockService)
        verifyNoMoreInteractions(mockStorage)
    }

    @Test
    fun sendThreeKinPayments_with_bad_sequence_number_for_first_two() {
        val destination = TestUtils.newKinAccount()
        val transactionToBeSent =
            KinTransaction(
                Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")!!,
                networkEnvironment = networkEnvironment
            )
        val responseTransaction = KinTransaction(
            transactionToBeSent.envelopeXdrBytes,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
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
            eq(registeredAccount),
            eq(listOf(KinPaymentItem(KinAmount(123), destination.id))),
            eq(KinMemo.NONE),
            eq(fee)
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
            if (++i == 0 || i == 2) {
                Promise.error(KinService.BadSequenceNumberInRequest)
            } else {
                Promise.of(responseTransaction)
            }
        }.whenever(mockService).submitTransaction(eq(transactionToBeSent))

        val amountSpent = KinAmount(123)
        val updatedAccountWithNewBalance = registeredAccount.merge(
            KinAccount(
                registeredAccount.key,
                balance = KinBalance(amountSpent)
            )
        )

        doAnswer {
            Promise.of(Optional.of(updatedAccountWithNewBalance))
        }.whenever(mockStorage).deductFromAccountBalance(
            eq(registeredAccount.id),
            eq(amountSpent + responseTransaction.fee.toKin())
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

        verify(mockStorage, times(3)).getStoredAccount(eq(registeredAccount.id))
        verify(mockService, times(2)).getAccount(eq(registeredAccount.id))
        verify(mockService, times(3)).buildAndSignTransaction(
            eq(registeredAccount),
            eq(listOf(KinPaymentItem(KinAmount(123), destination.id))),
            eq(KinMemo.NONE),
            eq(fee)
        )
        verify(
            mockService,
            times(5)
        ).submitTransaction(eq(transactionToBeSent)) // 5 due to 2 retries
        verify(mockStorage, times(3))
            .advanceSequence(eq(registeredAccount.id))
        verify(mockStorage, times(2)).updateAccountInStorage(eq(registeredAccount))
        verify(mockStorage, times(3)).insertNewTransactionInStorage(
            eq(registeredAccount.id),
            eq(responseTransaction)
        )
        verify(mockStorage, times(3)).deductFromAccountBalance(
            eq(registeredAccount.id),
            eq(amountSpent + responseTransaction.fee.toKin())
        )
        verify(mockStorage, times(3)).getStoredTransactions(eq(registeredAccount.id))
        verify(mockService, times(3)).canWhitelistTransactions()
        verify(mockStorage, times(3)).getMinFee()
        verifyNoMoreInteractions(mockService)
        verifyNoMoreInteractions(mockStorage)
    }

    @Test
    fun sendThreeKinPayments_with_insufficient_fee_for_first_two() {
        val destination = TestUtils.newKinAccount()
        val transactionToBeSent =
            KinTransaction(
                Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")!!,
                networkEnvironment = networkEnvironment
            )
        val responseTransaction = KinTransaction(
            transactionToBeSent.envelopeXdrBytes,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
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
            eq(registeredAccount),
            eq(listOf(KinPaymentItem(KinAmount(123), destination.id))),
            eq(KinMemo.NONE),
            eq(fee)
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

        doAnswer {
            Promise.of(Optional.of(fee))
        }.whenever(mockStorage).setMinFee(eq(fee))

        var i = -1

        doAnswer {
            if (++i == 0 || i == 2) {
                Promise.error(KinService.InsufficientFeeInRequest)
            } else {
                Promise.of(responseTransaction)
            }
        }.whenever(mockService).submitTransaction(eq(transactionToBeSent))

        val amountSpent = KinAmount(123)
        val updatedAccountWithNewBalance = registeredAccount.merge(
            KinAccount(
                registeredAccount.key,
                balance = KinBalance(amountSpent)
            )
        )

        doAnswer {
            Promise.of(Optional.of(updatedAccountWithNewBalance))
        }.whenever(mockStorage).deductFromAccountBalance(
            eq(registeredAccount.id),
            eq(amountSpent + responseTransaction.fee.toKin())
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

        verify(mockStorage, times(3)).getStoredAccount(eq(registeredAccount.id))
        verify(mockService, times(3)).buildAndSignTransaction(
            eq(registeredAccount),
            eq(listOf(KinPaymentItem(KinAmount(123), destination.id))),
            eq(KinMemo.NONE),
            eq(fee)
        )
        verify(
            mockService,
            times(5)
        ).submitTransaction(eq(transactionToBeSent)) // 5 due to 2 retries
        verify(mockStorage, times(3))
            .advanceSequence(eq(registeredAccount.id))
        verify(mockService, times(2)).getMinFee()
        verify(mockStorage, times(2)).setMinFee(eq(fee))
        verify(mockStorage, times(3)).insertNewTransactionInStorage(
            eq(registeredAccount.id),
            eq(responseTransaction)
        )
        verify(mockStorage, times(3)).deductFromAccountBalance(
            eq(registeredAccount.id),
            eq(amountSpent + responseTransaction.fee.toKin())
        )
        verify(mockStorage, times(3)).getStoredTransactions(eq(registeredAccount.id))
        verify(mockService, times(3)).canWhitelistTransactions()
        verify(mockStorage, times(3)).getMinFee()
        verifyNoMoreInteractions(mockService)
        verifyNoMoreInteractions(mockStorage)
    }

    @Test
    fun sendMultipleKinPayments_receiveMultiplePayments_requestNextPage_success() {
        val destination = TestUtils.newKinAccount()
        val transactionToBeSent =
            KinTransaction(
                Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")!!,
                networkEnvironment = networkEnvironment
            )
        val responseTransaction = KinTransaction(
            transactionToBeSent.envelopeXdrBytes,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
            ),
            networkEnvironment
        )
        val historicalTransaction1 = KinTransaction(
            Base64.decodeBase64("AAAAADvAUdgKnIY9yZNndqpn4OnROmd5vaKDQ4j1txU1cK4YAAAAZABYP0MAAAABAAAAAAAAAAAAAAABAAAAAAAAAAEAAAAAHfURkIfDkRfVhMKqq6jqU/KoudnDigo2YXBpKWrcxQQAAAAAAAAAAAABhqAAAAAAAAAAATVwrhgAAABAf3uEfHbs4JpaOWthX9wTq0PetHyX8WexBA4nx8cQDljAkcW/9n+4DWyAGoHQKebIRg1M6zWWg0RTqhuQ4hyfAQ==")!!,
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                KinTransaction.PagingToken("1231412342453245")
            ),
            networkEnvironment
        )
        val historicalTransaction2 = KinTransaction(
            Base64.decodeBase64("AAAAADvAUdgKnIY9yZNndqpn4OnROmd5vaKDQ4j1txU1cK4YAAAAZABYP0MAAAACAAAAAAAAAAAAAAABAAAAAAAAAAEAAAAAHfURkIfDkRfVhMKqq6jqU/KoudnDigo2YXBpKWrcxQQAAAAAAAAAAAADDUAAAAAAAAAAATVwrhgAAABAGeunbexebrTLt7dEdXeFFlig7dWVNVYJyXa/W7TB/thjE67fTkmhZbW12fmTvvuPg6sCNqnVfNDdL79UdlIdDg==")!!,
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                KinTransaction.PagingToken("1231412342453245")
            ),
            networkEnvironment
        )
        val historicalTransaction3 = KinTransaction(
            Base64.decodeBase64("AAAAADvAUdgKnIY9yZNndqpn4OnROmd5vaKDQ4j1txU1cK4YAAAAZABYP0MAAAADAAAAAAAAAAAAAAABAAAAAAAAAAEAAAAAHfURkIfDkRfVhMKqq6jqU/KoudnDigo2YXBpKWrcxQQAAAAAAAAAAAAEk+AAAAAAAAAAATVwrhgAAABAVfNN1H1DykJlsRsBYXo+6MQEuLSVRiw6scxe4fEsUnYjaAPYv6vajfThorq+xMtrd1ZIsR6lkidKJ7ZFe8FFBw==\n")!!,
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                KinTransaction.PagingToken("1231412342453245")
            ),
            networkEnvironment
        )
        val historicalTransaction4 = KinTransaction(
            Base64.decodeBase64("AAAAAB31EZCHw5EX1YTCqquo6lPyqLnZw4oKNmFwaSlq3MUEAAAAZABYP0QAAAABAAAAAAAAAAAAAAABAAAAAAAAAAEAAAAAO8BR2Aqchj3Jk2d2qmfg6dE6Z3m9ooNDiPW3FTVwrhgAAAAAAAAAAAAGGoAAAAAAAAAAAWrcxQQAAABAVlo9avGN5377ziEKULCDYJARlDTFrY8wKXbl3NJNGjcm8gH8Gdn1LGGD0RCd/sHpam29l8g/xlBnb+/zMrVqDg==")!!,
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                KinTransaction.PagingToken("1231412342453245")
            ),
            networkEnvironment
        )
        val historicalTransaction5 = KinTransaction(
            Base64.decodeBase64("AAAAAB31EZCHw5EX1YTCqquo6lPyqLnZw4oKNmFwaSlq3MUEAAAAZABYP0QAAAACAAAAAAAAAAAAAAABAAAAAAAAAAEAAAAAO8BR2Aqchj3Jk2d2qmfg6dE6Z3m9ooNDiPW3FTVwrhgAAAAAAAAAAAAHoSAAAAAAAAAAAWrcxQQAAABAznP+g/Hot6I5o37RMBC4tTIApC4IcWNQcGODzv6Gr9NuLpea8Xy3DhA8KMLyEJ0Bdt21ri2ec5NztYV4bQ6YAA==")!!,
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
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
            eq(registeredAccount),
            eq(listOf(KinPaymentItem(KinAmount(1), destination.id))),
            eq(KinMemo.NONE),
            eq(fee)
        )

        doAnswer {
            Promise.of(transactionToBeSent)
        }.whenever(mockService).buildAndSignTransaction(
            eq(registeredAccount),
            eq(listOf(KinPaymentItem(KinAmount(2), destination.id))),
            eq(KinMemo.NONE),
            eq(fee)
        )

        doAnswer {
            Promise.of(transactionToBeSent)
        }.whenever(mockService).buildAndSignTransaction(
            eq(registeredAccount),
            eq(listOf(KinPaymentItem(KinAmount(3), destination.id))),
            eq(KinMemo.NONE),
            eq(fee)
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
            eq(registeredAccount2),
            eq(listOf(KinPaymentItem(KinAmount(4), registeredAccount.id))),
            eq(KinMemo.NONE),
            eq(fee)
        )

        doAnswer {
            Promise.of(transactionToBeSent)
        }.whenever(mockService2).buildAndSignTransaction(
            eq(registeredAccount2),
            eq(listOf(KinPaymentItem(KinAmount(5), registeredAccount.id))),
            eq(KinMemo.NONE),
            eq(fee)
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
        }.whenever(mockStorage).deductFromAccountBalance(
            eq(registeredAccount.id),
            eq(amountSpent + responseTransaction.fee.toKin())
        )

        doAnswer {
            Promise.of(Optional.of(updatedAccountWithNewBalance))
        }.whenever(mockStorage2)
            .deductFromAccountBalance(
                eq(registeredAccount2.id),
                eq(amountSpent + responseTransaction.fee.toKin())
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
            KinTransaction(
                Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")!!,
                networkEnvironment = networkEnvironment
            )
        val responseTransaction = KinTransaction(
            transactionToBeSent.envelopeXdrBytes,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
            ),
            networkEnvironment
        )
        val historicalTransaction1 = KinTransaction(
            Base64.decodeBase64("AAAAADvAUdgKnIY9yZNndqpn4OnROmd5vaKDQ4j1txU1cK4YAAAAZABYP0MAAAABAAAAAAAAAAAAAAABAAAAAAAAAAEAAAAAHfURkIfDkRfVhMKqq6jqU/KoudnDigo2YXBpKWrcxQQAAAAAAAAAAAABhqAAAAAAAAAAATVwrhgAAABAf3uEfHbs4JpaOWthX9wTq0PetHyX8WexBA4nx8cQDljAkcW/9n+4DWyAGoHQKebIRg1M6zWWg0RTqhuQ4hyfAQ==")!!,
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                KinTransaction.PagingToken("1231412342453245")
            ),
            networkEnvironment
        )
        val historicalTransaction2 = KinTransaction(
            Base64.decodeBase64("AAAAADvAUdgKnIY9yZNndqpn4OnROmd5vaKDQ4j1txU1cK4YAAAAZABYP0MAAAACAAAAAAAAAAAAAAABAAAAAAAAAAEAAAAAHfURkIfDkRfVhMKqq6jqU/KoudnDigo2YXBpKWrcxQQAAAAAAAAAAAADDUAAAAAAAAAAATVwrhgAAABAGeunbexebrTLt7dEdXeFFlig7dWVNVYJyXa/W7TB/thjE67fTkmhZbW12fmTvvuPg6sCNqnVfNDdL79UdlIdDg==")!!,
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                KinTransaction.PagingToken("1231412342453245")
            ),
            networkEnvironment
        )
        val historicalTransaction3 = KinTransaction(
            Base64.decodeBase64("AAAAADvAUdgKnIY9yZNndqpn4OnROmd5vaKDQ4j1txU1cK4YAAAAZABYP0MAAAADAAAAAAAAAAAAAAABAAAAAAAAAAEAAAAAHfURkIfDkRfVhMKqq6jqU/KoudnDigo2YXBpKWrcxQQAAAAAAAAAAAAEk+AAAAAAAAAAATVwrhgAAABAVfNN1H1DykJlsRsBYXo+6MQEuLSVRiw6scxe4fEsUnYjaAPYv6vajfThorq+xMtrd1ZIsR6lkidKJ7ZFe8FFBw==\n")!!,
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                KinTransaction.PagingToken("1231412342453245")
            ),
            networkEnvironment
        )
        val historicalTransaction4 = KinTransaction(
            Base64.decodeBase64("AAAAAB31EZCHw5EX1YTCqquo6lPyqLnZw4oKNmFwaSlq3MUEAAAAZABYP0QAAAABAAAAAAAAAAAAAAABAAAAAAAAAAEAAAAAO8BR2Aqchj3Jk2d2qmfg6dE6Z3m9ooNDiPW3FTVwrhgAAAAAAAAAAAAGGoAAAAAAAAAAAWrcxQQAAABAVlo9avGN5377ziEKULCDYJARlDTFrY8wKXbl3NJNGjcm8gH8Gdn1LGGD0RCd/sHpam29l8g/xlBnb+/zMrVqDg==")!!,
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                KinTransaction.PagingToken("1231412342453245")
            ),
            networkEnvironment
        )
        val historicalTransaction5 = KinTransaction(
            Base64.decodeBase64("AAAAAB31EZCHw5EX1YTCqquo6lPyqLnZw4oKNmFwaSlq3MUEAAAAZABYP0QAAAACAAAAAAAAAAAAAAABAAAAAAAAAAEAAAAAO8BR2Aqchj3Jk2d2qmfg6dE6Z3m9ooNDiPW3FTVwrhgAAAAAAAAAAAAHoSAAAAAAAAAAAWrcxQQAAABAznP+g/Hot6I5o37RMBC4tTIApC4IcWNQcGODzv6Gr9NuLpea8Xy3DhA8KMLyEJ0Bdt21ri2ec5NztYV4bQ6YAA==")!!,
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
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
            eq(registeredAccount),
            eq(listOf(KinPaymentItem(KinAmount(3), destination.id))),
            eq(KinMemo.NONE),
            eq(fee)
        )
        doAnswer {
            Promise.of(transactionToBeSent)
        }.whenever(mockService).buildAndSignTransaction(
            eq(registeredAccount),
            eq(listOf(KinPaymentItem(KinAmount(4), destination.id))),
            eq(KinMemo.NONE),
            eq(fee)
        )

        doAnswer {
            Promise.of(transactionToBeSent)
        }.whenever(mockService).buildAndSignTransaction(
            eq(registeredAccount),
            eq(listOf(KinPaymentItem(KinAmount(5), destination.id))),
            eq(KinMemo.NONE),
            eq(fee)
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
        }.whenever(mockStorage).deductFromAccountBalance(eq(registeredAccount.id), eq(amountSpent))

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
        val destination = TestUtils.newKinAccount()
        val transactionToBeSent =
            KinTransaction(
                Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")!!,
                networkEnvironment = networkEnvironment
            )
        val responseTransaction = KinTransaction(
            transactionToBeSent.envelopeXdrBytes,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
            ),
            networkEnvironment
        )

        val subject = ValueSubject<KinAccount>()

        subject.onNext(registeredAccount)

        doAnswer {
            subject
        }.whenever(mockService).streamAccount(eq(registeredAccount.id))

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
        val destination = TestUtils.newKinAccount()
        val transactionToBeSent =
            KinTransaction(
                Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")!!,
                networkEnvironment = networkEnvironment
            )
        val responseTransaction = KinTransaction(
            transactionToBeSent.envelopeXdrBytes,
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
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
            KinTransaction(
                Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")!!,
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
