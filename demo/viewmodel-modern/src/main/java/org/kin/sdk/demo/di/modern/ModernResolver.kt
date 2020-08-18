package org.kin.sdk.demo.di.modern

import org.kin.sdk.base.KinAccountContext
import org.kin.sdk.base.KinEnvironment
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.repository.InMemoryKinAccountContextRepositoryImpl
import org.kin.sdk.base.repository.InvoiceRepository
import org.kin.sdk.base.repository.KinAccountContextRepository
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.demo.di.DemoResolver
import org.kin.sdk.demo.viewmodel.CreateInvoiceViewModel
import org.kin.sdk.demo.viewmodel.FullInvoiceViewModel
import org.kin.sdk.demo.viewmodel.HomeViewModel
import org.kin.sdk.demo.viewmodel.InvoicesViewModel
import org.kin.sdk.demo.viewmodel.DemoNavigator
import org.kin.sdk.demo.viewmodel.SendTransactionViewModel
import org.kin.sdk.demo.viewmodel.TransactionLoadTestingViewModel
import org.kin.sdk.demo.viewmodel.WalletViewModel
import org.kin.sdk.demo.viewmodel.modern.ModernCreateInvoiceViewModel
import org.kin.sdk.demo.viewmodel.modern.ModernFullInvoiceViewModel
import org.kin.sdk.demo.viewmodel.modern.ModernHomeViewModel
import org.kin.sdk.demo.viewmodel.modern.ModernInvoicesViewModel
import org.kin.sdk.demo.viewmodel.modern.ModernSendTransactionViewModel
import org.kin.sdk.demo.viewmodel.modern.ModernTransactionLoadTestingViewModel
import org.kin.sdk.demo.viewmodel.modern.ModernWalletViewModel

class ModernResolver(
    val testNetKinEnvironment: KinEnvironment,
    val mainNetKinEnvironment: KinEnvironment,
    private val invoiceRepository: InvoiceRepository,
    private val kinAccountContextRepository: KinAccountContextRepository = InMemoryKinAccountContextRepositoryImpl(testNetKinEnvironment)
) : DemoResolver {

    private val accountContexts =
        mutableMapOf<KinAccount.Id, MutableMap<NetworkEnvironment, KinAccountContext>>()

    override fun resolve(args: HomeViewModel.NavigationArgs, navigator: DemoNavigator) =
        ModernHomeViewModel(navigator, args, testNetKinEnvironment, mainNetKinEnvironment)

    override fun resolve(args: SendTransactionViewModel.NavigationArgs, navigator: DemoNavigator) =
        ModernSendTransactionViewModel(
            navigator,
            args,
            kinAccountContextRepository.getKinAccountContext(KinAccount.Id(args.publicAddress))!!
        )

    override fun resolve(args: WalletViewModel.NavigationArgs, navigator: DemoNavigator) =
        ModernWalletViewModel(
            navigator,
            args,
            kinAccountContextRepository.getKinAccountContext(KinAccount.Id(args.publicAddress))!!
        )

    override fun resolve(
        args: TransactionLoadTestingViewModel.NavigationArgs,
        navigator: DemoNavigator
    ) = ModernTransactionLoadTestingViewModel(
        navigator,
        args,
        kinAccountContextRepository.getKinAccountContext(KinAccount.Id(args.publicAddress))!!
    )

    override fun resolve(
        args: CreateInvoiceViewModel.NavigationArgs,
        navigator: DemoNavigator
    ) = ModernCreateInvoiceViewModel(navigator, args, invoiceRepository)

    override fun resolve(
        args: InvoicesViewModel.NavigationArgs,
        navigator: DemoNavigator
    ): InvoicesViewModel = ModernInvoicesViewModel(navigator, args, invoiceRepository)

    override fun resolve(
        args: FullInvoiceViewModel.NavigationArgs,
        navigator: DemoNavigator
    ): FullInvoiceViewModel = ModernFullInvoiceViewModel(navigator, args, invoiceRepository,kinAccountContextRepository)
}
