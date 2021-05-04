package org.kin.sdk.base.viewmodel.di

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.kin.base.viewmodel.PaymentFlowViewModel
import org.kin.base.viewmodel.tools.SpendNavigator
import org.kin.sdk.base.KinAccountContext
import org.kin.sdk.base.KinEnvironment
import org.kin.sdk.base.models.AppIdx
import org.kin.sdk.base.models.AppInfo
import org.kin.sdk.base.models.AppUserCreds
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.network.services.AppInfoProvider
import org.kin.sdk.base.repository.AppInfoRepository
import org.kin.sdk.base.repository.InvoiceRepository
import org.kin.sdk.base.repository.KinAccountContextRepository
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.storage.KinFileStorage

class SpendResolverImplTest {

    class DummyAppInfoProvider : AppInfoProvider {
        override val appInfo: AppInfo =
            AppInfo(AppIdx.TEST_APP_IDX, KinAccount.Id(ByteArray(0)), "", 123)

        override fun getPassthroughAppUserCredentials(): AppUserCreds {
            return AppUserCreds("", "")
        }
    }

    lateinit var invoiceRepository: InvoiceRepository
    lateinit var appInfoRepository: AppInfoRepository
    lateinit var kinAccountContextRepository: KinAccountContextRepository
    lateinit var spendNavigator: SpendNavigator
    lateinit var kinAccountContext: KinAccountContext

    lateinit var sut: SpendResolver

    @Before
    fun setUp() {
        kinAccountContext = mock {

        }
        invoiceRepository = mock {

        }
        appInfoRepository = mock {

        }
        kinAccountContextRepository = mock {
            on {
                getKinAccountContext(
                    eq(KinAccount.Id("GAQJH2KSSWOTX3LCEESPIJPY73QRCH55OBCCM3SYZ3EEWSTMJ4NYNT6S"))
                )
            } doReturn kinAccountContext

            on {
                getKinAccountContext(
                    eq(KinAccount.Id("GAZGCY6DQN7ESXHCYHL2P3CFZMBPUOYXI5UWYJMO26UXQV55FDSQ4LUY"))
                )
            } doReturn null
        }
        spendNavigator = mock {

        }

        val env = KinEnvironment.Agora.Builder(NetworkEnvironment.TestNet)
            .setAppInfoProvider(DummyAppInfoProvider())
            .setStorage(
                KinFileStorage.Builder("local")
                    .setNetworkEnvironment(NetworkEnvironment.TestNet)
            )
            .build()
        sut = SpendResolverImpl(
            env,
            invoiceRepository,
            appInfoRepository,
            kinAccountContextRepository
        )
    }

    @Test
    fun resolve_success() {
        val navArgs = PaymentFlowViewModel.NavigationArgs(
            "asdf",
            "GAQJH2KSSWOTX3LCEESPIJPY73QRCH55OBCCM3SYZ3EEWSTMJ4NYNT6S",
            0
        )
        val viewModel = sut.resolve(navArgs, spendNavigator)

        verify(kinAccountContextRepository).getKinAccountContext(
            eq(KinAccount.Id("GAQJH2KSSWOTX3LCEESPIJPY73QRCH55OBCCM3SYZ3EEWSTMJ4NYNT6S"))
        )
    }

    @Test(expected = RuntimeException::class)
    fun resolve_missing_account_id() {
        val navArgs = PaymentFlowViewModel.NavigationArgs(
            "asdf",
            "GAZGCY6DQN7ESXHCYHL2P3CFZMBPUOYXI5UWYJMO26UXQV55FDSQ4LUY",
            0
        )
        val viewModel = sut.resolve(navArgs, spendNavigator)

        verify(kinAccountContextRepository).getKinAccountContext(
            eq(KinAccount.Id("GAZGCY6DQN7ESXHCYHL2P3CFZMBPUOYXI5UWYJMO26UXQV55FDSQ4LUY"))
        )
    }
}
