package org.kin.sdk.demo.viewmodel.compat

import kin.sdk.KinClient
import kin.sdk.Transaction
import kin.sdk.TransactionId
import kin.utils.ResultCallback
import org.kin.sdk.demo.viewmodel.Navigator
import org.kin.sdk.demo.viewmodel.TransactionLoadTestingViewModel
import org.kin.sdk.demo.viewmodel.tools.BaseViewModel
import java.math.BigDecimal
import kotlin.math.floor

class CompatTransactionLoadTestingViewModel(
    navigator: Navigator,
    args: TransactionLoadTestingViewModel.NavigationArgs,
    private val client: KinClient
) : TransactionLoadTestingViewModel,
    BaseViewModel<TransactionLoadTestingViewModel.NavigationArgs, TransactionLoadTestingViewModel.State>(
        args
    ) {
    class TestRunViewModel(override val duration: Float) :
        TransactionLoadTestingViewModel.TestRunViewModel

    private val wallet = client.getAccount(args.walletIndex)

    override fun getDefaultState() = TransactionLoadTestingViewModel.State(
        "",
        null,
        null,
        null,
        null,
        listOf()
    )

    override fun onDestinationAddressUpdated(value: String) {
        updateState {
            it.copy(destinationAddress = value)
        }
    }

    private fun runTransaction(fee: Int, destinationAddress: String, completed: (Float?) -> Unit) {
        val startTime = System.nanoTime()

        // build a transaction with the collected information
        val transaction = wallet.buildTransaction(
            destinationAddress,
            BigDecimal(10),
            fee,
            "Perf test"
        )

        // send the transaction to the blockchain
        transaction
            .run(object : ResultCallback<Transaction> {
                override fun onResult(transaction: Transaction) {
                    // send the transaction after it completes
                    wallet.sendTransaction(transaction).run(object :
                        ResultCallback<TransactionId> {
                        override fun onResult(id: TransactionId?) {
                            if (id == null) {
                                completed(null)
                                return
                            }

                            val endTime = System.nanoTime()
                            val totalTime = endTime - startTime

                            completed(totalTime / 1000000000.0f)
                        }

                        override fun onError(e: Exception) {
                            completed(null)
                        }
                    })
                }

                override fun onError(e: Exception) {
                    completed(null)
                }
            })
    }

    override fun onStartTapped(completed: (Throwable?) -> Unit) {
        updateState {
            val maxRuns = 10

            fun runTest(fee: Int, iteration: Int) {
                runTransaction(fee, it.destinationAddress) { duration ->
                    if (duration != null) {
                        updateState { state ->
                            state.copy(testRuns = state.testRuns + TestRunViewModel(duration))
                        }

                        if (iteration < maxRuns) {
                            runTest(fee, iteration + 1)
                        } else {
                            updateState {
                                val p50 = it.testRuns.map { it.duration }.sortedDescending()
                                    .asReversed()[floor(0.50f * it.testRuns.count()).toInt()]
                                val p95 = it.testRuns.map { it.duration }.sortedDescending()
                                    .asReversed()[floor(0.95f * it.testRuns.count()).toInt()]
                                val p99 = it.testRuns.map { it.duration }.sortedDescending()
                                    .asReversed()[floor(0.99f * it.testRuns.count()).toInt()]
                                it.copy(
                                    p50 = p50,
                                    p95 = p95,
                                    p99 = p99
                                )
                            }

                            completed(null)
                        }
                    } else {
                        // TODO show error
                        completed(Exception("Transaction Failed!"))
                    }
                }
            }

            client.minimumFee.run(object : ResultCallback<Long> {
                override fun onResult(fee: Long?) {
                    runTest(fee?.toInt() ?: 100, 0)
                }

                override fun onError(e: Exception?) {
                    completed(e)
                }
            })

            it
        }
    }
}
