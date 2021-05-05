package org.kin.sdk.demo.viewmodel

import org.kin.base.viewmodel.PaymentFlowViewModel
import org.kin.sdk.design.viewmodel.Navigator

interface DemoNavigator : Navigator {
    fun navigateTo(args: HomeViewModel.NavigationArgs)

    fun navigateTo(args: WalletViewModel.NavigationArgs)

    fun navigateTo(args: SendTransactionViewModel.NavigationArgs)

    fun navigateTo(args: TransactionLoadTestingViewModel.NavigationArgs)

    fun navigateTo(args: InvoicesViewModel.NavigationArgs)

    fun navigateTo(args: CreateInvoiceViewModel.NavigationArgs)

    fun navigateTo(args: FullInvoiceViewModel.NavigationArgs)

    fun navigateTo(navigationArgs: BackupViewModel.NavigationArgs)

    fun navigateTo(navigationArgs: RestoreViewModel.NavigationArgs)

    fun navigateToForResult(
        args: PaymentFlowViewModel.NavigationArgs,
        onResult: (PaymentFlowViewModel.Result) -> Unit
    )
}
