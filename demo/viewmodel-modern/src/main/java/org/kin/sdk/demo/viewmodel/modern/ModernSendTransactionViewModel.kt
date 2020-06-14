package org.kin.sdk.demo.viewmodel.modern

import org.kin.sdk.base.KinAccountContext
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinMemo
import org.kin.sdk.demo.viewmodel.Navigator
import org.kin.sdk.demo.viewmodel.SendTransactionViewModel
import org.kin.sdk.demo.viewmodel.tools.BaseViewModel
import java.math.BigInteger

class ModernSendTransactionViewModel(
    navigator: Navigator,
    args: SendTransactionViewModel.NavigationArgs,
    private val kinAccountContext: KinAccountContext
) : SendTransactionViewModel,
    BaseViewModel<SendTransactionViewModel.NavigationArgs, SendTransactionViewModel.State>(args) {

    override fun getDefaultState() = SendTransactionViewModel.State(
        "",
        BigInteger.ZERO,
        BigInteger.ZERO,
        ""
    )

    override fun onDestinationAddressUpdated(value: String) {
        updateState {
            it.copy(destinationAddress = value)
        }
    }

    override fun onAmountUpdated(value: BigInteger) {
        updateState {
            it.copy(amount = value)
        }
    }

    override fun onFeeUpdated(value: BigInteger) {
        updateState {
            it.copy(fee = value)
        }
    }

    override fun onMemoUpdated(value: String) {
        updateState {
            it.copy(memo = value)
        }
    }

    override fun onSendTapped(completed: (error: Throwable?) -> Unit) {
        updateState {
            kinAccountContext.sendKinPayment(
                KinAmount(it.amount),
                KinAccount.Id(it.destinationAddress),
                KinMemo.NONE
            ).then({
                completed(null)
            }, completed)
            it
        }
    }
}
