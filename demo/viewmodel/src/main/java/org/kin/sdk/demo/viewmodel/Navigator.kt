package org.kin.sdk.demo.viewmodel

interface Navigator {
    fun navigateTo(args: HomeViewModel.NavigationArgs)

    fun navigateTo(args: WalletViewModel.NavigationArgs)

    fun navigateTo(args: SendTransactionViewModel.NavigationArgs)

    fun navigateTo(args: TransactionLoadTestingViewModel.NavigationArgs)

    fun close()
}
