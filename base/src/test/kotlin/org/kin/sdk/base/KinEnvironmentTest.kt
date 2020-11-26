package org.kin.sdk.base

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.grpc.okhttp.OkHttpChannelBuilder
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import org.kin.sdk.base.models.AppIdx
import org.kin.sdk.base.models.AppInfo
import org.kin.sdk.base.models.AppUserCreds
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.asKinAccountId
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.network.api.KinAccountCreationApi
import org.kin.sdk.base.network.api.KinTransactionWhitelistingApi
import org.kin.sdk.base.network.services.AppInfoProvider
import org.kin.sdk.base.network.services.KinService
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.storage.KinFileStorage
import org.kin.sdk.base.storage.Storage
import org.kin.sdk.base.tools.Callback
import org.kin.sdk.base.tools.ExecutorServices
import org.kin.sdk.base.tools.KinLoggerFactoryImpl
import org.kin.sdk.base.tools.NetworkOperationsHandlerImpl
import org.kin.sdk.base.tools.Optional
import org.kin.sdk.base.tools.Promise
import org.kin.sdk.base.tools.TestUtils
import org.kin.sdk.base.tools.test
import java.io.IOException
import java.util.concurrent.CountDownLatch
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KinEnvironmentTest {

    class DummyAppInfoProvider : AppInfoProvider {
        override val appInfo: AppInfo = AppInfo(AppIdx.TEST_APP_IDX, KinAccount.Id(ByteArray(0)), "", 123)

        override fun getPassthroughAppUserCredentials(): AppUserCreds {
            return AppUserCreds("uid0", "pass123")
        }
    }

    lateinit var mockStorage: Storage
    lateinit var mockService: KinService
    lateinit var mockAccountCreationApi: KinAccountCreationApi
    lateinit var mockWhitelistingApi: KinTransactionWhitelistingApi


    @Before
    fun setUp() {
        mockStorage = mock {
            on { getMinApiVersion() } doReturn Promise.of(Optional.empty())
        }
        mockService = mock {}
        mockAccountCreationApi = mock {}
        mockWhitelistingApi = mock {}
    }


    @Suppress("UNUSED_VARIABLE")
    @Test
    fun testHorizonConstruction() {
        val logger = KinLoggerFactoryImpl(true)
        val sutTest = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setStorage(mockStorage).build()

        val sutTest2 = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setOkHttpClient(OkHttpClient())
            .setNetworkOperationsHandler(NetworkOperationsHandlerImpl(logger= logger))
            .setLogger(logger)
            .setKinService(mockService)
            .setStorage(mockStorage)
            .build()

        val sutTest3 = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setOkHttpClient(OkHttpClient())
            .setNetworkOperationsHandler(NetworkOperationsHandlerImpl(logger = logger))
            .setLogger(logger)
            .setKinService(mockService)
            .setKinAccountCreationApi(mockAccountCreationApi)
            .setKinTransactionWhitelistingApi(mockWhitelistingApi)
            .setStorage(
                KinFileStorage.Builder("storage/files/somwhere/kin")
                    .setNetworkEnvironment(NetworkEnvironment.KinStellarTestNetKin3)
                    .setExecutors(ExecutorServices())
            )
            .build()

        val sutMain = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarMainNetKin3)
            .setStorage(mockStorage)
            .build()
    }

    @Suppress("UNUSED_VARIABLE")
    @Test
    fun testAgoraConstruction() {
        val logger = KinLoggerFactoryImpl(true)
        val sutTest = KinEnvironment.Agora.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setAppInfoProvider(DummyAppInfoProvider())
            .setStorage(mockStorage)
            .build()

        val sutTest2 = KinEnvironment.Agora.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setNetworkOperationsHandler(NetworkOperationsHandlerImpl(logger = logger))
            .setLogger(logger)
            .setMinApiVersion(3)
            .setAppInfoProvider(DummyAppInfoProvider())
            .setKinService(mockService)
            .setStorage(mockStorage)
            .build()

        val sutTest3 = KinEnvironment.Agora.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setAppInfoProvider(DummyAppInfoProvider())
            .setNetworkOperationsHandler(NetworkOperationsHandlerImpl(logger = logger))
            .setLogger(logger)
            .setKinService(mockService)
            .setExecutorServices(ExecutorServices())
            .setManagedChannel(
                OkHttpChannelBuilder.forAddress("somewhere.dev", 9000).build()
            )
            .setStorage(
                KinFileStorage.Builder("storage/files/somwhere/kin")
                    .setNetworkEnvironment(NetworkEnvironment.KinStellarTestNetKin3)
                    .setExecutors(ExecutorServices())
            )
            .build()

        val sutMain = KinEnvironment.Agora.Builder(NetworkEnvironment.KinStellarMainNetKin3)
            .setAppInfoProvider(DummyAppInfoProvider())
            .setStorage(mockStorage)
            .build()
    }

    @Test
    fun testImportPrivateKey_new_success() {
        val sut = KinEnvironment.Agora.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setAppInfoProvider(DummyAppInfoProvider())
            .setKinService(mockService)
            .setStorage(mockStorage)
            .build()

        val privateKey = TestUtils.newPrivateKey()

        whenever(mockStorage.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(null)

        whenever(mockStorage.addAccount(eq(KinAccount(privateKey))))
            .thenReturn(true)

        whenever(mockService.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(Promise.of(KinAccount(privateKey)))

        sut.importPrivateKey(privateKey).test {
            assertTrue(value!!)
        }

        val latch = CountDownLatch(1)
        sut.importPrivateKey(privateKey, object : Callback<Boolean> {
            override fun onCompleted(value: Boolean?, error: Throwable?) {
                assertTrue(value!!)
                latch.countDown()
            }
        })
    }

    @Test(expected = KinEnvironment.KinEnvironmentBuilderException::class)
    fun testImportPrivateKey_no_appInfoProvider() {
        val sut = KinEnvironment.Agora.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setKinService(mockService)
            .setStorage(mockStorage)
            .build()

        val privateKey = TestUtils.newPrivateKey()

        whenever(mockStorage.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(null)

        whenever(mockStorage.addAccount(eq(KinAccount(privateKey))))
            .thenReturn(true)

        whenever(mockService.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(Promise.of(KinAccount(privateKey)))

        sut.importPrivateKey(privateKey).test {
            assertTrue(value!!)
        }

        val latch = CountDownLatch(1)
        sut.importPrivateKey(privateKey, object : Callback<Boolean> {
            override fun onCompleted(value: Boolean?, error: Throwable?) {
                assertTrue(value!!)
                latch.countDown()
            }
        })
    }

    @Test
    fun testImportPrivateKey_existing_from_storage_success() {
        val sut = KinEnvironment.Agora.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setAppInfoProvider(DummyAppInfoProvider())
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
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setStorage(mockStorage)
            .build()

        val privateKey = TestUtils.newPrivateKey()

        whenever(mockStorage.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(null)

        whenever(mockStorage.addAccount(eq(KinAccount(privateKey))))
            .thenReturn(true)

        whenever(mockService.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(
                Promise.of(
                    KinAccount(
                        id = privateKey.asKinAccountId(),
                        key = privateKey.asPublicKey(),
                        status = KinAccount.Status.Registered(123),
                        balance = KinBalance(KinAmount(10))
                    )
                )
            )

        sut.importPrivateKey(privateKey).test {
            assertTrue(value!!)
        }
    }

    @Test
    fun testImportPrivateKey_existing_from_network_success_but_fails_to_store() {
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setStorage(mockStorage)
            .build()

        val privateKey = TestUtils.newPrivateKey()

        whenever(mockStorage.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(null)

        whenever(mockStorage.addAccount(eq(KinAccount(privateKey))))
            .thenReturn(false)

        whenever(mockService.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(
                Promise.of(
                    KinAccount(
                        id = privateKey.asKinAccountId(),
                        key = privateKey.asPublicKey(),
                        status = KinAccount.Status.Registered(123),
                        balance = KinBalance(KinAmount(10))
                    )
                )
            )

        sut.importPrivateKey(privateKey).test {
            assertFalse(value!!)
        }
    }

    @Test
    fun testImportPrivateKey_existing_from_network_success_but_storage_exception() {
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setStorage(mockStorage)
            .build()

        val privateKey = TestUtils.newPrivateKey()

        whenever(mockStorage.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(null)

        whenever(mockStorage.addAccount(eq(KinAccount(privateKey))))
            .then { throw IOException() }

        whenever(mockService.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(
                Promise.of(
                    KinAccount(
                        id = privateKey.asKinAccountId(),
                        key = privateKey.asPublicKey(),
                        status = KinAccount.Status.Registered(123),
                        balance = KinBalance(KinAmount(10))
                    )
                )
            )

        sut.importPrivateKey(privateKey).test {
            error is IOException
        }
    }

    @Test
    fun testImportPrivateKey_existing_from_network_does_not_exist() {
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNetKin3)
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
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNetKin3)
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
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNetKin3)
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
        val sut = KinEnvironment.Agora.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setKinService(mockService)
            .setAppInfoProvider(DummyAppInfoProvider())
            .setStorage(mockStorage)
            .build()

        val privateKey = TestUtils.newPrivateKey()

        whenever(mockStorage.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(null)

        whenever(mockStorage.addAccount(eq(KinAccount(privateKey))))
            .thenReturn(false)

        whenever(mockService.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(Promise.of(KinAccount(privateKey)))

        sut.importPrivateKey(privateKey).test {
            assertFalse(value!!)
        }
    }

    @Test
    fun testImportPrivateKey_storage_exception() {
        val sut = KinEnvironment.Agora.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setAppInfoProvider(DummyAppInfoProvider())
            .setKinService(mockService)
            .setStorage(mockStorage)
            .build()

        val privateKey = TestUtils.newPrivateKey()

        whenever(mockStorage.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(null)

        whenever(mockStorage.addAccount(eq(KinAccount(privateKey))))
            .then { throw IOException() }

        whenever(mockService.getAccount(eq(privateKey.asKinAccountId())))
            .thenReturn(
                Promise.of(
                    KinAccount(
                        id = privateKey.asKinAccountId(),
                        key = privateKey.asPublicKey(),
                        status = KinAccount.Status.Registered(123),
                        balance = KinBalance(KinAmount(10))
                    )
                )
            )

        sut.importPrivateKey(privateKey).test {
            error is IOException
        }
    }

    @Test
    fun testImportPrivateKey_storage_exception_and_service_exception() {
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNetKin3)
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
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNetKin3)
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
        val sut = KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setStorage(mockStorage)
            .build()

        val ex = RuntimeException()

        whenever(mockStorage.getAllAccountIds())
            .thenThrow(ex)

        sut.allAccountIds().test {
            assertEquals(ex, error)
        }
    }

    @Test
    fun testAppUserCreds() {
        val sut = KinEnvironment.Agora.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setAppInfoProvider(DummyAppInfoProvider())
            .setStorage(mockStorage)
            .build()

        val creds = sut.appInfoProvider.getPassthroughAppUserCredentials()
        assertEquals("uid0", creds.appUserId)
        assertEquals("pass123", creds.appUserPasskey)
    }

    @Test
    fun testMinApiVersions_ok() {
        KinEnvironment.Agora.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setMinApiVersion(3)
            .setKinService(mockService)
            .setAppInfoProvider(DummyAppInfoProvider())
            .setStorage(mockStorage)
            .build()

        KinEnvironment.Agora.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setMinApiVersion(4)
            .setKinService(mockService)
            .setAppInfoProvider(DummyAppInfoProvider())
            .setStorage(mockStorage)
            .build()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testMinApiVersions_toolow() {
        val sut = KinEnvironment.Agora.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setMinApiVersion(2)
            .setKinService(mockService)
            .setAppInfoProvider(DummyAppInfoProvider())
            .setStorage(mockStorage)
            .build()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testMinApiVersions_toohigh() {
        val sut = KinEnvironment.Agora.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setMinApiVersion(5)
            .setKinService(mockService)
            .setAppInfoProvider(DummyAppInfoProvider())
            .setStorage(mockStorage)
            .build()
    }

    @Test(expected = NotImplementedError::class)
    fun testHorizoinUnsupportedEnv_1() {
        KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNetKin2)
            .setKinService(mockService)
            .setStorage(mockStorage)
            .build()
    }

    @Test(expected = NotImplementedError::class)
    fun testHorizoinUnsupportedEnv_2() {
        KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarMainNetKin2)
            .setKinService(mockService)
            .setStorage(mockStorage)
            .build()
    }
}
