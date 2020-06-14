package org.kin.sdk.demo.di.compat

import android.content.Context
import kin.sdk.Environment
import kin.sdk.KinClient
import org.kin.sdk.demo.di.Resolver
import org.kin.sdk.demo.viewmodel.HomeViewModel
import org.kin.sdk.demo.viewmodel.Navigator
import org.kin.sdk.demo.viewmodel.SendTransactionViewModel
import org.kin.sdk.demo.viewmodel.TransactionLoadTestingViewModel
import org.kin.sdk.demo.viewmodel.WalletViewModel
import org.kin.sdk.demo.viewmodel.compat.CompatHomeViewModel
import org.kin.sdk.demo.viewmodel.compat.CompatSendTransactionViewModel
import org.kin.sdk.demo.viewmodel.compat.CompatTransactionLoadTestingViewModel
import org.kin.sdk.demo.viewmodel.compat.CompatWalletViewModel

class CompatResolver(private val applicationContext: Context) : Resolver {
    private val testNet: KinClient by lazy { KinClient(applicationContext, Environment.TEST, "appi", "testnet") }

    private val mainNet: KinClient by lazy { KinClient(applicationContext, Environment.PRODUCTION, "appi", "mainnet") }

    override fun resolve(args: HomeViewModel.NavigationArgs, navigator: Navigator) =
        CompatHomeViewModel(
            navigator,
            HomeViewModel.NavigationArgs(args.resolverType),
            testNet,
            mainNet
        )

    override fun resolve(args: SendTransactionViewModel.NavigationArgs, navigator: Navigator) =
        CompatSendTransactionViewModel(
            navigator,
            args,
            testNet
        )

    override fun resolve(args: WalletViewModel.NavigationArgs, navigator: Navigator) =
        CompatWalletViewModel(
            navigator,
            args,
            testNet
        )

    override fun resolve(args: TransactionLoadTestingViewModel.NavigationArgs, navigator: Navigator) =
        CompatTransactionLoadTestingViewModel(
            navigator,
            args,
            testNet
        )
}
