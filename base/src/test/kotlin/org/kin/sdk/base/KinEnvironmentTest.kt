package org.kin.sdk.base

import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.asKinAccountId
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.network.services.KinService
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.storage.Storage
import org.kin.sdk.base.tools.NetworkOperationsHandlerImpl
import org.kin.sdk.base.tools.Promise
import org.kin.sdk.base.tools.TestUtils
import org.kin.sdk.base.tools.test
import org.slf4j.LoggerFactory
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KinEnvironmentTest {

    lateinit var mockStorage: Storage
    lateinit var mockService: KinService

    @Before
    fun setUp() {
        mockStorage = mock {}
        mockService = mock {}
    }


    @Test
    fun testHorizonConstruction() {

        val sutTest = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
            .setStorage(mockStorage).build()

        val sutTest2 = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
            .setOkHttpClient(OkHttpClient())
            .setNetworkOperationsHandler(NetworkOperationsHandlerImpl())
            .setLogger(LoggerFactory.getILoggerFactory())
            .setKinService(mockService)
            .setStorage(mockStorage)
            .build()

        val sutMain = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarMainNet)
            .setStorage(mockStorage)
            .build()
    }

    @Test
    fun testImportPrivateKey_new_success() {
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
            .setStorage(mockStorage)
            .build()

        val privateKey = TestUtils.newPrivateKey()

        whenever(mockStorage.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(null)

        whenever(mockStorage.addAccount(eq(KinAccount(privateKey))))
            .thenReturn(true)

        sut.importPrivateKey(privateKey).test {
            assertTrue(value!!)
        }
    }

    @Test
    fun testImportPrivateKey_existing_from_storage_success() {
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
            .setStorage(mockStorage)
            .build()

        val privateKey = TestUtils.newPrivateKey()

        whenever(mockStorage.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(KinAccount(privateKey))

        whenever(mockStorage.addAccount(eq(KinAccount(privateKey))))
            .thenReturn(true)

        sut.importPrivateKey(privateKey).test {
            assertTrue(value!!)
        }
    }

    @Test
    fun testImportPrivateKey_existing_from_network_success() {
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
            .setStorage(mockStorage)
            .build()

        val privateKey = TestUtils.newPrivateKey()

        whenever(mockStorage.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(null)

        whenever(mockStorage.addAccount(eq(KinAccount(privateKey))))
            .thenReturn(true)

        whenever(mockService.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(
                Promise.of(KinAccount(
                    id = privateKey.asKinAccountId(),
                    key = privateKey.asPublicKey(),
                    status = KinAccount.Status.Registered(123),
                    balance = KinBalance(KinAmount(10))
                )))

        sut.importPrivateKey(privateKey).test {
            assertTrue(value!!)
        }
    }

    @Test
    fun testImportPrivateKey_existing_from_network_success_but_fails_to_store() {
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
            .setStorage(mockStorage)
            .build()

        val privateKey = TestUtils.newPrivateKey()

        whenever(mockStorage.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(null)

        whenever(mockStorage.addAccount(eq(KinAccount(privateKey))))
            .thenReturn(false)

        whenever(mockService.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(
                Promise.of(KinAccount(
                    id = privateKey.asKinAccountId(),
                    key = privateKey.asPublicKey(),
                    status = KinAccount.Status.Registered(123),
                    balance = KinBalance(KinAmount(10))
                )))

        sut.importPrivateKey(privateKey).test {
            assertFalse(value!!)
        }
    }

    @Test
    fun testImportPrivateKey_existing_from_network_success_but_storage_exception() {
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
            .setStorage(mockStorage)
            .build()

        val privateKey = TestUtils.newPrivateKey()

        whenever(mockStorage.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(null)

        whenever(mockStorage.addAccount(eq(KinAccount(privateKey))))
            .then { throw IOException() }

        whenever(mockService.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(
                Promise.of(KinAccount(
                    id = privateKey.asKinAccountId(),
                    key = privateKey.asPublicKey(),
                    status = KinAccount.Status.Registered(123),
                    balance = KinBalance(KinAmount(10))
                )))

        sut.importPrivateKey(privateKey).test {
            error is IOException
        }
    }

    @Test
    fun testImportPrivateKey_existing_from_network_does_not_exist() {
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
            .setStorage(mockStorage)
            .build()

        val privateKey = TestUtils.newPrivateKey()

        whenever(mockStorage.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(null)

        whenever(mockStorage.addAccount(eq(KinAccount(privateKey))))
            .thenReturn(true)

        whenever(mockService.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(Promise.error(IOException()))

        sut.importPrivateKey(privateKey).test {
            assertTrue(value!!)
        }
    }

    @Test
    fun testImportPrivateKey_existing_from_network_does_not_exist_but_fails_to_store() {
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
            .setStorage(mockStorage)
            .build()

        val privateKey = TestUtils.newPrivateKey()

        whenever(mockStorage.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(null)

        whenever(mockStorage.addAccount(eq(KinAccount(privateKey))))
            .thenReturn(false)

        whenever(mockService.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(Promise.error(IOException()))

        sut.importPrivateKey(privateKey).test {
            assertFalse(value!!)
        }
    }

    @Test
    fun testImportPrivateKey_existing_from_network_does_not_exist_but_storage_exception() {
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
            .setStorage(mockStorage)
            .build()

        val privateKey = TestUtils.newPrivateKey()

        whenever(mockStorage.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(null)

        whenever(mockStorage.addAccount(eq(KinAccount(privateKey))))
            .then { throw IOException() }

        whenever(mockService.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(Promise.error(IOException()))

        sut.importPrivateKey(privateKey).test {
            error is IOException
        }
    }

    @Test
    fun testImportPrivateKey_storage_failed() {
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
            .setStorage(mockStorage)
            .build()

        val privateKey = TestUtils.newPrivateKey()

        whenever(mockStorage.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(null)

        whenever(mockStorage.addAccount(eq(KinAccount(privateKey))))
            .thenReturn(false)

        sut.importPrivateKey(privateKey).test {
            assertFalse(value!!)
        }
    }

    @Test
    fun testImportPrivateKey_storage_exception() {
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
            .setStorage(mockStorage)
            .build()

        val privateKey = TestUtils.newPrivateKey()

        whenever(mockStorage.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(null)

        whenever(mockStorage.addAccount(eq(KinAccount(privateKey))))
            .then { throw IOException() }

        whenever(mockService.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(
                Promise.of(KinAccount(
                    id = privateKey.asKinAccountId(),
                    key = privateKey.asPublicKey(),
                    status = KinAccount.Status.Registered(123),
                    balance = KinBalance(KinAmount(10))
                )))

        sut.importPrivateKey(privateKey).test {
            error is IOException
        }
    }

    @Test
    fun testImportPrivateKey_storage_exception_and_service_exception() {
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
            .setStorage(mockStorage)
            .build()

        val privateKey = TestUtils.newPrivateKey()

        whenever(mockStorage.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(null)

        whenever(mockStorage.addAccount(eq(KinAccount(privateKey))))
            .then { throw IOException() }

        whenever(mockService.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(Promise.error(IOException()))

        sut.importPrivateKey(privateKey).test {
            error is IOException
        }
    }

    @Test
    fun testAllAccountIds() {
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
            .setStorage(mockStorage)
            .build()

        val kinAccount1 = TestUtils.newKinAccount()
        val kinAccount2 = TestUtils.newKinAccount()
        val kinAccount3 = TestUtils.newKinAccount()

        whenever(mockStorage.getAllAccountIds())
            .thenReturn(listOf(kinAccount1.id, kinAccount2.id, kinAccount3.id))

        sut.allAccountIds().test {
            assertEquals(listOf(kinAccount1.id, kinAccount2.id, kinAccount3.id), value)
        }
    }

    @Test
    fun testAllAccountIds_error() {
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
            .setStorage(mockStorage)
            .build()

        val ex = RuntimeException()

        whenever(mockStorage.getAllAccountIds())
            .thenThrow(ex)

        sut.allAccountIds().test {
            assertEquals(ex, error)
        }
    }
}
