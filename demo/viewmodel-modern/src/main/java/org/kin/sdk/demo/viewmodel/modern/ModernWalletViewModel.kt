package org.kin.sdk.demo.viewmodel.modern

import org.kin.sdk.base.KinAccountContext
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.KinPayment
import org.kin.sdk.base.tools.ListObserver
import org.kin.sdk.base.tools.Observer
import org.kin.sdk.demo.viewmodel.Navigator
import org.kin.sdk.demo.viewmodel.SendTransactionViewModel
import org.kin.sdk.demo.viewmodel.TransactionLoadTestingViewModel
import org.kin.sdk.demo.viewmodel.WalletViewModel
import org.kin.sdk.demo.viewmodel.tools.BaseViewModel
import java.math.BigDecimal

class ModernWalletViewModel(
    private val navigator: Navigator,
    args: WalletViewModel.NavigationArgs,
    private val kinAccountContext: KinAccountContext
) : WalletViewModel, BaseViewModel<WalletViewModel.NavigationArgs, WalletViewModel.State>(args) {

    private inner class SendTransactionActionViewModel :
        WalletViewModel.SendTransactionActionViewModel {
        override fun onItemTapped() {
            navigator.navigateTo(
                SendTransactionViewModel.NavigationArgs(
                    args.walletIndex,
                    args.publicAddress
                )
            )
        }
    }

    private inner class LatencyTestTransactionActionViewModel :
        WalletViewModel.LatencyTestTransactionActionViewModel {
        override fun onItemTapped() {
            navigator.navigateTo(
                TransactionLoadTestingViewModel.NavigationArgs(
                    args.walletIndex,
                    args.publicAddress
                )
            )
        }
    }

    private inner class CopyAddressActionViewModel : WalletViewModel.CopyAddressActionViewModel {
        override val publicAddress: String
            get() = kinAccountContext.accountId.encodeAsString()
    }

    private inner class ShowScanCodeActionViewModel : WalletViewModel.ShowScanCodeActionViewModel {
        override val publicAddress: String
            get() = kinAccountContext.accountId.encodeAsString()
    }

    private inner class OnboardActionViewModel : WalletViewModel.OnboardActionViewModel {
        override fun onItemTapped(completed: (ex: Throwable?) -> Unit) {
            TODO("not implemented")
        }
    }

    private inner class DeleteWalletActionViewModel : WalletViewModel.DeleteWalletActionViewModel {
        override fun onItemTapped(completed: (ex: Throwable?) -> Unit) {
            kinAccountContext.clearStorage().then({ completed(null) }, completed)
        }
    }

    override fun getDefaultState(): WalletViewModel.State = WalletViewModel.State(
        kinAccountContext.accountId.encodeAsString(),
        null,
        WalletViewModel.WalletStatus.Unknown,
        listOf(
            CopyAddressActionViewModel(),
            SendTransactionActionViewModel(),
            LatencyTestTransactionActionViewModel(),
//            ExportActionViewModel(),
//            ShowScanCodeActionViewModel(),
//            OnboardActionViewModel(),
            DeleteWalletActionViewModel()
        ),
        emptyList()
    )

    private inner class PaymentHistoryItemViewModel(
        override val amount: BigDecimal,
        override val memo: String,
        override val sourceWallet: String,
        override val date: Long
    ) : WalletViewModel.PaymentHistoryItemViewModel

    private val listeners = mutableListOf<Observer<*>>()

    init {
        listeners.add(observeBalance())
        listeners.add(observePayments())
    }

    private fun observeBalance(): Observer<KinBalance> {
        return kinAccountContext.observeBalance()
            .add { balance ->
                updateState { previousState ->
                    previousState.copy(
                        balance = balance.amount.value,
                        walletStatus = if (balance.amount != KinAmount.ZERO) WalletViewModel.WalletStatus.Active else WalletViewModel.WalletStatus.Inactive
                    )
                }
            }
    }

    private fun observePayments(): ListObserver<KinPayment> {
        return kinAccountContext.observePayments()
            .add {
                updateState { previousState ->
                    previousState.copy(paymentHistoryItems = it.map {
                        val destination = it.destinationAccountId.encodeAsString()
                        val source = it.sourceAccountId.encodeAsString()
                        val incoming = destination == kinAccountContext.accountId.encodeAsString()

                        val amount = if (incoming) {
                            it.amount.value
                        } else {
                            it.amount.value.negate()
                        }

                        val address = if (incoming) {
                            source
                        } else {
                            destination
                        }

                        PaymentHistoryItemViewModel(amount, String(it.memo.rawValue), address, 0L)
                    })
                }
            }
    }

    override fun cleanup() {
        listeners.forEach(Observer<*>::dispose)
    }
}
