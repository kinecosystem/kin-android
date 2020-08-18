package org.kin.base.viewmodel

import org.kin.sdk.design.viewmodel.tools.ViewModel
import java.math.BigInteger

interface PaymentFlowViewModel :
    ViewModel<PaymentFlowViewModel.NavigationArgs, PaymentFlowViewModel.State> {
    /**
     * @param invoiceId - the encoded hash of the Invoice
     * @param payerAccountId - the address of the account that is to make a payment for the [invoiceId]
     */
    data class NavigationArgs(
        val invoiceId: String,
        val payerAccountId: String,
        val processingAppIdx: Int
    )

    sealed class Result {
        data class Success(val transactionHash: String) : Result()
        data class Failure(val reason: Reason) : Result() {
            enum class Reason(val value: Int) {
                /**
                 * The user cancelled the flow.
                 */
                CANCELLED(0),

                /**
                 * The webhoook responded saying that this invoice has already been paid for.
                 * No Payment was just made.
                 */
                ALREADY_PURCHASED(1),

                /**
                 * Unknown Failure
                 */
                UNKNOWN_FAILURE(2),

                /**
                 * Cannot locate the invoice specified.
                 */
                UNKNOWN_INVOICE(3),

                /**
                 * The paying KinAccount is not found in storage.
                 */
                UNKNOWN_PAYER_ACCOUNT(4),

                /**
                 * Not enough kin to pay for the Invoice.
                 */
                INSUFFICIENT_BALANCE(5),

                /**
                 * Something is wrong with the request.
                 * Developer intervention is probably required.
                 */
                MISCONFIGURED_REQUEST(6),

                /**
                 * The webhook has denied the payment or refused to whitelist it.
                 */
                DENIED_BY_SERVICE(7),

                /**
                 * You require a newer SDK to communicate with the Kin Blockchain.
                 * As a developer you should direct users to upgrade your app if you get this.
                 */
                SDK_UPGRADE_REQUIRED(8),

                /**
                 * Not able to communicate with the server due to network problems.
                 * We likely timed out and failed retrying.
                 */
                BAD_NETWORK(9);

                companion object {
                    @JvmStatic
                    fun fromValue(value: Int): Reason {
                        return when (value) {
                            0 -> CANCELLED
                            1 -> ALREADY_PURCHASED
                            3 -> UNKNOWN_INVOICE
                            4 -> UNKNOWN_PAYER_ACCOUNT
                            5 -> INSUFFICIENT_BALANCE
                            6 -> MISCONFIGURED_REQUEST
                            7 -> DENIED_BY_SERVICE
                            8 -> SDK_UPGRADE_REQUIRED
                            9 -> BAD_NETWORK
                            else -> UNKNOWN_FAILURE
                        }
                    }
                }
            }
        }
    }

    data class State(val appIconId: Int?, val progression: Progression) {
        sealed class Progression(val value: Int) {
            object Init : Progression(0)
            data class PaymentConfirmation(
                val amount: BigInteger,
                val appName: String,
                val newBalanceAfter: BigInteger
            ) : Progression(1)

            object PaymentProcessing : Progression(2)
            data class PaymentSuccess(val transactionHash: String) : Progression(3)
            data class PaymentError(val reason: Result.Failure.Reason, val balance: BigInteger) :
                Progression(4)
        }
    }

    fun onCancelTapped(onCompleted: () -> Unit)

    fun onConfirmTapped()
}
