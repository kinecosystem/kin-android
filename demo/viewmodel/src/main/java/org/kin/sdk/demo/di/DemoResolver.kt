package org.kin.sdk.demo.di

import org.kin.sdk.demo.viewmodel.HomeViewModel
import org.kin.sdk.demo.viewmodel.CreateInvoiceViewModel
import org.kin.sdk.demo.viewmodel.FullInvoiceViewModel
import org.kin.sdk.demo.viewmodel.InvoicesViewModel
import org.kin.sdk.demo.viewmodel.DemoNavigator
import org.kin.sdk.demo.viewmodel.SendTransactionViewModel
import org.kin.sdk.demo.viewmodel.TransactionLoadTestingViewModel
import org.kin.sdk.demo.viewmodel.WalletViewModel
import org.kin.sdk.design.di.Resolver

interface DemoResolver : Resolver {
    fun resolve(args: HomeViewModel.NavigationArgs, navigator: DemoNavigator): HomeViewModel

    fun resolve(args: SendTransactionViewModel.NavigationArgs, navigator: DemoNavigator): SendTransactionViewModel

    fun resolve(args: WalletViewModel.NavigationArgs, navigator: DemoNavigator): WalletViewModel

    fun resolve(args: TransactionLoadTestingViewModel.NavigationArgs, navigator: DemoNavigator): TransactionLoadTestingViewModel

    fun resolve(args: CreateInvoiceViewModel.NavigationArgs, navigator: DemoNavigator): CreateInvoiceViewModel

    fun resolve(args: InvoicesViewModel.NavigationArgs, navigator: DemoNavigator): InvoicesViewModel

    fun resolve(args: FullInvoiceViewModel.NavigationArgs, navigator: DemoNavigator): FullInvoiceViewModel
}
