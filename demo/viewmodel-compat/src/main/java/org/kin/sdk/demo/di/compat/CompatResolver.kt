package org.kin.sdk.demo.di.compat

import android.content.Context
import kin.sdk.Environment
import kin.sdk.KinClient
import org.kin.sdk.demo.di.DemoResolver
import org.kin.sdk.demo.viewmodel.CreateInvoiceViewModel
import org.kin.sdk.demo.viewmodel.FullInvoiceViewModel
import org.kin.sdk.demo.viewmodel.HomeViewModel
import org.kin.sdk.demo.viewmodel.InvoicesViewModel
import org.kin.sdk.demo.viewmodel.DemoNavigator
import org.kin.sdk.demo.viewmodel.SendTransactionViewModel
import org.kin.sdk.demo.viewmodel.TransactionLoadTestingViewModel
import org.kin.sdk.demo.viewmodel.WalletViewModel
import org.kin.sdk.demo.viewmodel.compat.CompatHomeViewModel
import org.kin.sdk.demo.viewmodel.compat.CompatSendTransactionViewModel
import org.kin.sdk.demo.viewmodel.compat.CompatTransactionLoadTestingViewModel
import org.kin.sdk.demo.viewmodel.compat.CompatWalletViewModel

class CompatResolver(private val applicationContext: Context) : DemoResolver {

    object ViewModelNotSupportedException : IllegalStateException()

    private val testNet: KinClient by lazy {
        KinClient.testMigration()
        KinClient(
            applicationContext,
            Environment.TEST,
            "appi",
            "testnet"
        )
    }

    private val mainNet: KinClient by lazy {
        KinClient(
            applicationContext,
            Environment.PRODUCTION,
            "appi",
            "mainnet"
        )
    }

    override fun resolve(args: HomeViewModel.NavigationArgs, navigator: DemoNavigator) =
        CompatHomeViewModel(
            navigator,
            HomeViewModel.NavigationArgs(args.resolverType),
            testNet,
            mainNet
        )

    override fun resolve(args: SendTransactionViewModel.NavigationArgs, navigator: DemoNavigator) =
        CompatSendTransactionViewModel(
            navigator,
            args,
            testNet
        )

    override fun resolve(args: WalletViewModel.NavigationArgs, navigator: DemoNavigator) =
        CompatWalletViewModel(
            navigator,
            args,
            testNet
        )

    override fun resolve(
        args: TransactionLoadTestingViewModel.NavigationArgs,
        navigator: DemoNavigator
    ) =
        CompatTransactionLoadTestingViewModel(
            navigator,
            args,
            testNet
        )

    override fun resolve(
        args: CreateInvoiceViewModel.NavigationArgs,
        navigator: DemoNavigator
    ) =
        throw ViewModelNotSupportedException

    override fun resolve(
        args: InvoicesViewModel.NavigationArgs,
        navigator: DemoNavigator
    ): InvoicesViewModel = throw ViewModelNotSupportedException

    override fun resolve(
        args: FullInvoiceViewModel.NavigationArgs,
        navigator: DemoNavigator
    ): FullInvoiceViewModel = throw ViewModelNotSupportedException
}
