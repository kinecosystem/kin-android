package org.kin.sdk.demo.viewmodel.modern

import org.kin.sdk.base.KinAccountContext
import org.kin.sdk.base.models.KinBinaryMemo
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.demo.di.modern.DemoAppConfig.Companion.DEMO_APP_IDX
import org.kin.sdk.demo.viewmodel.DemoNavigator
import org.kin.sdk.demo.viewmodel.SendTransactionViewModel
import org.kin.sdk.design.viewmodel.tools.BaseViewModel
import java.math.BigInteger

class ModernSendTransactionViewModel(
    @Suppress("UNUSED_PARAMETER") navigator: DemoNavigator,
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

        withState {
            kinAccountContext.sendKinPayment(
                KinAmount(amount),
                KinAccount.Id(destinationAddress),
                KinBinaryMemo.Builder(DEMO_APP_IDX.value)
                    .setTranferType(KinBinaryMemo.TransferType.P2P)
                    .build()
                    .toKinMemo()
            ).then({
                completed(null)
            }, completed)
        }
    }
}
