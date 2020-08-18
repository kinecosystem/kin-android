package org.kin.sdk.demo.viewmodel

import org.kin.sdk.design.viewmodel.tools.ViewModel

interface TransactionLoadTestingViewModel : ViewModel<TransactionLoadTestingViewModel.NavigationArgs, TransactionLoadTestingViewModel.State> {

    data class NavigationArgs(val walletIndex: Int, val publicAddress: String)

    interface TestRunViewModel {
        val duration: Float
    }

    data class State(
        val destinationAddress: String,
        val trimmedMean: Float?,
        val p50: Float?,
        val p95: Float?,
        val p99: Float?,
        val testRuns: List<TransactionLoadTestingViewModel.TestRunViewModel>
    )

    fun onDestinationAddressUpdated(value: String)

    fun onStartTapped(completed: (error: Throwable?) -> Unit)
}
