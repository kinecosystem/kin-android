package org.kin.sdk.base.repository

import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.kin.sdk.base.KinEnvironment
import org.kin.sdk.base.models.AppIdx
import org.kin.sdk.base.models.AppInfo
import org.kin.sdk.base.models.AppUserCreds
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.asKinAccount
import org.kin.sdk.base.network.services.AppInfoProvider
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.storage.KinFileStorage
import org.kin.stellarfork.KeyPair
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class InMemoryKinAccountContextRepositoryImplTest {

    class DummyAppInfoProvider : AppInfoProvider {
        override val appInfo: AppInfo =
            AppInfo(AppIdx.TEST_APP_IDX, KinAccount.Id(ByteArray(0)), "", 123)

        override fun getPassthroughAppUserCredentials(): AppUserCreds {
            return AppUserCreds("", "")
        }
    }

    @Rule
    @JvmField
    public var tempFolder = TemporaryFolder()

    companion object {
        val kinAccount = KeyPair.random().asKinAccount()
        val kinAccount2Id = KinAccount.Id("GAQJH2KSSWOTX3LCEESPIJPY73QRCH55OBCCM3SYZ3EEWSTMJ4NYNT6S")
    }

    lateinit var env: KinEnvironment

    lateinit var sut: KinAccountContextRepository

    @Before
    fun setUp() {
        env = KinEnvironment.Agora.Builder(NetworkEnvironment.KinStellarTestNetKin3)
            .setAppInfoProvider(DummyAppInfoProvider())
            .setStorage(
                KinFileStorage.Builder(tempFolder.root.invariantSeparatorsPath)
                    .setNetworkEnvironment(NetworkEnvironment.KinStellarTestNetKin3)
            )
            .build()

        sut = InMemoryKinAccountContextRepositoryImpl(env)
    }

    @Test
    fun getKinAccountContext_needs_create() {
        env.storage.addAccount(kinAccount)
        assertNotNull(sut.getKinAccountContext(kinAccount.id))
    }

    @Test
    fun getKinAccountContext_exists() {
        env.storage.addAccount(kinAccount)
        val first = sut.getKinAccountContext(kinAccount.id)
        val second = sut.getKinAccountContext(kinAccount.id)

        assertNotNull(first)
        assertNotNull(second)

        assertSame(first, second)
    }

    @Test
    fun getKinAccountContext_not_available() {
        assertNull(sut.getKinAccountContext(kinAccount2Id))
    }
}
