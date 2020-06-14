package org.kin.sdk.demo.viewmodel.compat

import kin.sdk.KinClient
import kin.sdk.Transaction
import kin.sdk.TransactionId
import kin.utils.ResultCallback
import org.kin.sdk.demo.viewmodel.Navigator
import org.kin.sdk.demo.viewmodel.SendTransactionViewModel
import org.kin.sdk.demo.viewmodel.tools.BaseViewModel
import java.lang.Exception
import java.math.BigInteger

class CompatSendTransactionViewModel(
    navigator: Navigator,
    args: SendTransactionViewModel.NavigationArgs,
    client: KinClient
) : SendTransactionViewModel, BaseViewModel<SendTransactionViewModel.NavigationArgs, SendTransactionViewModel.State>(args) {
    private val wallet = client.getAccount(args.walletIndex)

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

    override fun onSendTapped(completed: (Throwable?) -> Unit) {
        updateState {
            // build a transaction with the collected information
            val transaction = wallet.buildTransaction(
                it.destinationAddress,
                it.amount.toBigDecimal(),
                it.fee.toInt(),
                it.memo)

            // send the transaction to the blockchain
            transaction
                .run(object : ResultCallback<Transaction> {
                    override fun onResult(transaction: Transaction) {
                        // send the transaction after it completes
                        wallet.sendTransaction(transaction).run(object :
                            ResultCallback<TransactionId> {
                            override fun onResult(id: TransactionId?) {
                                if (id == null) {
                                    completed(IllegalStateException("Transaction ID not returned"))
                                    return
                                }

                                completed(null)
                            }

                            override fun onError(e: Exception) {
                                completed(e)
                            }
                        })
                    }

                    override fun onError(e: Exception) {
                        completed(e)
                    }
                })
            it
        }
    }
}
