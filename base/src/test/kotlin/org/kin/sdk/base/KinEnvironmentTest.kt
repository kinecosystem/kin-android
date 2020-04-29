package org.kin.sdk.base

import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.asKinAccountId
import org.kin.sdk.base.network.services.KinService
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.storage.Storage
import org.kin.sdk.base.tools.NetworkOperationsHandlerImpl
import org.kin.sdk.base.tools.TestUtils
import org.kin.sdk.base.tools.test
import org.slf4j.LoggerFactory
import java.io.IOException
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
    fun testImportPrivateKey_existing_success() {
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

        sut.importPrivateKey(privateKey).test {
            error is IOException
        }
    }
}
