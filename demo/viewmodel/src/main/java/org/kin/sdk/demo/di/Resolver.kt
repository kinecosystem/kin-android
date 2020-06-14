package org.kin.sdk.demo.di

import org.kin.sdk.demo.viewmodel.HomeViewModel
import org.kin.sdk.demo.viewmodel.Navigator
import org.kin.sdk.demo.viewmodel.SendTransactionViewModel
import org.kin.sdk.demo.viewmodel.TransactionLoadTestingViewModel
import org.kin.sdk.demo.viewmodel.WalletViewModel

interface Resolver {
    fun resolve(args: HomeViewModel.NavigationArgs, navigator: Navigator): HomeViewModel

    fun resolve(args: SendTransactionViewModel.NavigationArgs, navigator: Navigator): SendTransactionViewModel

    fun resolve(args: WalletViewModel.NavigationArgs, navigator: Navigator): WalletViewModel

    fun resolve(args: TransactionLoadTestingViewModel.NavigationArgs, navigator: Navigator): TransactionLoadTestingViewModel
}
