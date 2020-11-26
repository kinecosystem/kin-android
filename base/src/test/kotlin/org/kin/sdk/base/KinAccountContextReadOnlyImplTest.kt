package org.kin.sdk.base

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
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.network.services.KinService
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.storage.Storage
import org.kin.sdk.base.tools.ExecutorServices
import org.kin.sdk.base.tools.Optional
import org.kin.sdk.base.tools.Promise
import org.kin.sdk.base.tools.TestUtils
import org.kin.sdk.base.tools.latchOperationValueCapture
import org.kin.sdk.base.tools.test
import org.kin.sdk.base.tools.updateStatus
import org.kin.stellarfork.codec.Base64
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class KinAccountContextReadOnlyImplTest {

    companion object {
        val registeredAccount = TestUtils.newSigningKinAccount()

        //            .updateStatus(KinAccount.Status.Registered(1234))
        val registeredAccount2 = TestUtils.newSigningKinAccount()
            .updateStatus(KinAccount.Status.Registered(1234))
        val unregisteredAccount = TestUtils.newSigningKinAccount()

        val fee = QuarkAmount(100)

        val pagingToken = KinTransaction.PagingToken("16576645322248192")
        val historicalKinTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=",
            KinTransaction.RecordType.Historical(
                KinDateFormat("2019-12-12T21:32:43Z").timestamp,
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                pagingToken
            )
        )
        val networkEnvironment = NetworkEnvironment.KinStellarTestNetKin3
    }

    private lateinit var sut: KinAccountContextReadOnly

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
            on { getStoredAccount(eq(registeredAccount.id)) } doReturn
                    Promise.of(Optional.of(registeredAccount))
        }

        mockService2 = mock {
            on { canWhitelistTransactions() } doReturn Promise.of(false)
            on { getMinFee() } doReturn Promise.of(fee)
        }
        mockStorage2 = mock {
            on { getMinFee() } doReturn Promise.of(Optional.of(fee))
        }

        sut = KinAccountContext.Builder(
            KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNetKin3)
                .setKinService(mockService)
                .setExecutorServices(excecutors)
                .setStorage(mockStorage)
        ).useExistingAccountReadOnly(registeredAccount.id).build()

        sut2 = KinAccountContext.Builder(
            KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNetKin3)
                .setKinService(mockService2)
                .setExecutorServices(excecutors)
                .setStorage(mockStorage2)
        ).useExistingAccount(registeredAccount2.id).build()
    }

    @Test
    fun getAccount_success() {
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
    fun getAccount_needs_fetch_success() {
        doAnswer {
            Promise.of(Optional.empty<KinAccount>())
        }.whenever(mockStorage).getStoredAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(registeredAccount)
        }.whenever(mockService).getAccount(eq(registeredAccount.id))

        doAnswer {
            Promise.of(registeredAccount)
        }.whenever(mockStorage).updateAccountInStorage(eq(registeredAccount))

        sut.getAccount().test {
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
    fun getAccount_force_fetch_success() {
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
            sut.observeBalance().add {
                capture(it)
            }
        }.apply {
            assertNull(error)
            assertEquals(listOf(KinBalance(KinAmount.ZERO), KinBalance(KinAmount(123))), values)
            verify(mockStorage, times(2)).getStoredAccount(eq(registeredAccount.id))
            verify(mockService).getAccount(eq(registeredAccount.id))
            verify(mockStorage, times(2)).updateAccountInStorage(eq(accountFromService))
        }
    }
}
