package org.kin.sdk.demo.viewmodel.modern

import org.kin.sdk.base.KinAccountContext
import org.kin.sdk.base.models.KinBinaryMemo
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.toKin
import org.kin.sdk.demo.di.modern.DemoAppConfig
import org.kin.sdk.demo.viewmodel.DemoNavigator
import org.kin.sdk.demo.viewmodel.TransactionLoadTestingViewModel
import org.kin.sdk.design.viewmodel.tools.BaseViewModel
import kotlin.math.abs
import kotlin.math.floor
import kotlin.random.Random

class ModernTransactionLoadTestingViewModel(
    @Suppress("UNUSED_PARAMETER") navigator: DemoNavigator,
    args: TransactionLoadTestingViewModel.NavigationArgs,
    private val kinAccountContext: KinAccountContext
) : TransactionLoadTestingViewModel,
    BaseViewModel<TransactionLoadTestingViewModel.NavigationArgs, TransactionLoadTestingViewModel.State>(
        args
    ) {
    class TestRunViewModel(override val duration: Float) :
        TransactionLoadTestingViewModel.TestRunViewModel

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

    private fun randomQuarkAmount(lessThan: Long = 100): QuarkAmount =
        QuarkAmount(abs(Random(System.currentTimeMillis()).nextLong() % lessThan))

    private fun runTransaction(destinationAddress: String, completed: (Float?) -> Unit) {
        val startTime = System.nanoTime()

        // send the transaction to the blockchain
        kinAccountContext.sendKinPayment(
            randomQuarkAmount(100).toKin(),
            KinAccount.Id(destinationAddress),
            KinBinaryMemo.Builder(DemoAppConfig.DEMO_APP_IDX.value)
                .setTransferType(KinBinaryMemo.TransferType.P2P)
                .build()
                .toKinMemo()
        ).then({
            val endTime = System.nanoTime()
            val totalTime = endTime - startTime

            completed(totalTime / 1000000000.0f)
        }, {
            completed(null)
        })
    }

    override fun onStartTapped(completed: (Throwable?) -> Unit) {
        updateState {
            val maxRuns = 10

            fun runTest(iteration: Int) {
                runTransaction(it.destinationAddress) { duration ->
                    if (duration != null) {
                        updateState { state ->
                            state.copy(testRuns = state.testRuns + TestRunViewModel(duration))
                        }

                        if (iteration < maxRuns) {
                            runTest(iteration + 1)
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

            runTest(0)

            it
        }
    }
}
