package org.kin.sdk.demo.viewmodel.compat

import android.app.Activity
import android.util.Log
import kin.backupandrestore.BackupAndRestoreManager
import kin.sdk.AccountStatus
import kin.sdk.Balance
import kin.sdk.KinClient
import kin.sdk.ListenerRegistration
import kin.utils.ResultCallback
import org.kin.sdk.demo.viewmodel.DemoNavigator
import org.kin.sdk.demo.viewmodel.SendTransactionViewModel
import org.kin.sdk.demo.viewmodel.TransactionLoadTestingViewModel
import org.kin.sdk.demo.viewmodel.WalletOnboarding
import org.kin.sdk.demo.viewmodel.WalletViewModel
import org.kin.sdk.design.viewmodel.tools.BaseViewModel
import java.math.BigDecimal
import java.math.BigInteger

class CompatWalletViewModel(
    private val navigator: DemoNavigator,
    args: WalletViewModel.NavigationArgs,
    private val client: KinClient
) : WalletViewModel, BaseViewModel<WalletViewModel.NavigationArgs, WalletViewModel.State>(args) {

    private inner class DeleteWalletActionViewModel : WalletViewModel.DeleteWalletActionViewModel {
        override fun onItemTapped(completed: (ex: Throwable?) -> Unit) {
            if (client.deleteAccount(args.walletIndex)) {
                completed(null)
                navigator.close()
            } else {
                completed(null)
                TODO("report error")
            }
        }
    }

    private inner class OnboardActionViewModel : WalletViewModel.OnboardActionViewModel {
        override fun onItemTapped(completed: (ex: Throwable?) -> Unit) {
            try {
                WalletOnboarding().activateAccount(
                    wallet.publicAddress!!,
                    BigInteger.valueOf(6000)
                ) { ex ->
                    if (ex != null) {
                        Log.e("KIN", "error funding wallet", ex)
                    }

                    checkBalance()

                    completed(ex)
                }
            } catch (ex: Throwable) {
                completed(ex)
            }
        }
    }

    private inner class CopyAddressActionViewModel(override val publicAddress: String) :
        WalletViewModel.CopyAddressActionViewModel

    private inner class ExportWalletActionViewModel : WalletViewModel.ExportWalletActionViewModel {
        override fun onItemTapped(activity: Any) {
            BackupAndRestoreManager(activity as Activity, 1, 2).backup(client, wallet)
        }
    }

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

    private data class PaymentHistoryItemViewModel(
        override val amount: BigDecimal,
        override val memo: String,
        override val sourceWallet: String,
        override val date: Long
    ) : WalletViewModel.PaymentHistoryItemViewModel {
        override fun onItemTapped() {
        }
    }

    private val wallet = client.getAccount(args.walletIndex)

    override fun getDefaultState() = WalletViewModel.State(
        WalletViewModel.WalletHeaderViewModel(
            wallet.publicAddress!!,
        null
        ),
        WalletViewModel.WalletStatus.Unknown,
        listOf(
            CopyAddressActionViewModel(wallet.publicAddress!!),
            DeleteWalletActionViewModel()
        ),
        emptyList()
    )

    private val listeners = mutableListOf<ListenerRegistration>()

    init {
        listeners.add(observeBalance())
        listeners.add(observeAccountCreation())
        listeners.add(observePayments())

        wallet.status.run(object : ResultCallback<Int> {
            override fun onResult(result: Int?) {
                when {
                    result == AccountStatus.CREATED -> {
// Fund Account
                        wallet.balance.run(object : ResultCallback<Balance> {
                            override fun onResult(result: Balance) {

                                if(result.value().toBigInteger() != BigInteger.ZERO) {
                                    return
                                }

                                try {
                                    WalletOnboarding().fundAccount(
                                        wallet.publicAddress!!,
                                        BigInteger.valueOf(10000)
                                    ) { ex ->
                                        if (ex != null) {
                                            Log.e("KIN", "error funding wallet", ex)
                                        }

                                        checkBalance()
                                    }
                                } catch (ex: Throwable) {

                                }
                            }

                            override fun onError(e: java.lang.Exception?) {

                            }
                        })
                    }
                    else -> {

                    }
                }
            }

            override fun onError(e: java.lang.Exception?) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun observePayments(): ListenerRegistration {
        val payments = mutableListOf<PaymentHistoryItemViewModel>()

        /**
         * Note this version of the SDK has no storage and so the payments you get here are only while the listener is active.
         */
        val paymentListener = wallet.addPaymentListener { info ->
            val destination = info.destinationPublicKey()
            val source = info.sourcePublicKey()
            val incoming = destination == wallet.publicAddress

            val amount = if (incoming) {
                info.amount()
            } else {
                info.amount().negate()
            }

            val address = if (incoming) {
                source
            } else {
                destination
            }

            val item = PaymentHistoryItemViewModel(amount, info.memo(), address, 0L)

            updateState { previousState ->
                payments.add(item)

                previousState.copy(paymentHistoryItems = payments)
            }
        }

        return paymentListener
    }

    private fun makeStateInactive(previousState: WalletViewModel.State): WalletViewModel.State {
        return previousState.copy(
            walletActions = listOf(
                CopyAddressActionViewModel(wallet.publicAddress!!),
                OnboardActionViewModel(),
                DeleteWalletActionViewModel()
            ), walletStatus = WalletViewModel.WalletStatus.Inactive
        )
    }

    private fun makeStateActive(previousState: WalletViewModel.State): WalletViewModel.State {

        if (previousState.walletStatus == WalletViewModel.WalletStatus.Active) {
            return previousState
        }

        return previousState.copy(
            walletActions = listOf(
                CopyAddressActionViewModel(wallet.publicAddress!!),
                SendTransactionActionViewModel(),
                LatencyTestTransactionActionViewModel(),
                ExportWalletActionViewModel(),
                DeleteWalletActionViewModel()
            ), walletStatus = WalletViewModel.WalletStatus.Active
        )
    }

    private fun checkBalance() {
        wallet.balance.run(object : ResultCallback<Balance> {
            override fun onResult(balance: Balance?) {
                updateState { previousState ->
                    if (balance == null) {
                        makeStateInactive(previousState)
                    } else {
                        makeStateActive(previousState).copy(walletHeaderViewModel = previousState.walletHeaderViewModel.copy(balance = balance.value()))
                    }
                }
            }

            override fun onError(e: Exception?) {
                updateState { previousState ->
                    makeStateInactive(previousState)
                }
            }
        })
    }

    private fun observeAccountCreation(): ListenerRegistration {
        val accountCreationListener = wallet.addAccountCreationListener {
            updateState { previousState ->
                makeStateActive(previousState)
            }
            checkBalance()
        }

        return accountCreationListener
    }

    private fun observeBalance(): ListenerRegistration {
        val balanceListener = wallet.addBalanceListener { balance ->
            updateState { previousState ->
                makeStateActive(previousState).copy(walletHeaderViewModel = previousState.walletHeaderViewModel.copy(balance = balance?.value() ?: BigDecimal.ZERO))
            }
        }

        checkBalance()

        return balanceListener
    }

    override fun cleanup() {
        super.cleanup()

        listeners.forEach { it.remove() }
        listeners.clear()
    }
}
