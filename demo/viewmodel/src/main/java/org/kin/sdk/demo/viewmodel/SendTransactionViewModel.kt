package org.kin.sdk.demo.viewmodel

import org.kin.sdk.demo.viewmodel.tools.ViewModel
import java.math.BigInteger

interface SendTransactionViewModel : ViewModel<SendTransactionViewModel.NavigationArgs, SendTransactionViewModel.State> {

    data class NavigationArgs(val walletIndex: Int, val publicAddress: String)

    data class State(
        val destinationAddress: String,
        val amount: BigInteger,
        val fee: BigInteger,
        val memo: String
    )

    fun onDestinationAddressUpdated(value: String)

    fun onAmountUpdated(value: BigInteger)

    fun onFeeUpdated(value: BigInteger)

    fun onMemoUpdated(value: String)

    fun onSendTapped(completed: (error: Throwable?) -> Unit)
}
