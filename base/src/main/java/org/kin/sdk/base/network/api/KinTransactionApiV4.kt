package org.kin.sdk.base.network.api

import org.kin.sdk.base.models.InvoiceList
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.TransactionHash
import org.kin.sdk.base.models.solana.Hash
import org.kin.sdk.base.models.solana.Transaction
import org.kin.sdk.base.stellar.models.KinTransaction

interface KinTransactionApiV4 {

    object GetServiceConfigRequest

    /**
     * @param subsidizerAccount  The public key of the account that the service will use to sign transactions for funding.
     *                           If not specified, the service is _not_ configured to fund transactions.
     */
    data class GetServiceConfigResponse(
        val result: Result,
        val subsidizerAccount: KinAccount.Id?,

        /** TODO: remove these two after we've locked in some tokens **/
        val tokenProgram: KinAccount.Id?,
        val token: KinAccount.Id?,
    ) {
        sealed class Result(val value: Int) {
            object UpgradeRequiredError : Result(-3)
            data class UndefinedError(val error: Throwable) : Result(-2)
            data class TransientFailure(val error: Throwable) : Result(-1)
            object Ok : Result(0)
        }
    }

    object GetMiniumumKinVersionRequest

    data class GetMiniumumKinVersionResponse(
        val result: Result,
        val version: Int
    ) {
        sealed class Result(val value: Int) {
            data class UndefinedError(val error: Throwable) : Result(-2)
            data class TransientFailure(val error: Throwable) : Result(-1)
            object Ok : Result(0)
        }
    }

    object GetRecentBlockHashRequest

    data class GetRecentBlockHashResponse(
        val result: Result,
        val blockHash: Hash?
    ) {
        sealed class Result(val value: Int) {
            object UpgradeRequiredError : Result(-3)
            data class UndefinedError(val error: Throwable) : Result(-2)
            data class TransientFailure(val error: Throwable) : Result(-1)
            object Ok : Result(0)
        }
    }

    data class GetMinimumBalanceForRentExemptionRequest(val size: Long)

    data class GetMinimumBalanceForRentExemptionResponse(
        val result: Result,
        val lamports: Long?
    ) {
        sealed class Result(val value: Int) {
            object UpgradeRequiredError : Result(-3)
            data class UndefinedError(val error: Throwable) : Result(-2)
            data class TransientFailure(val error: Throwable) : Result(-1)
            object Ok : Result(0)
        }
    }

    data class GetTransactionRequest(val transactionHash: TransactionHash)

    data class GetTransactionResponse(
        val result: Result,
        val transaction: KinTransaction? = null
    ) {
        sealed class Result(val value: Int) {
            object UpgradeRequiredError : Result(-3)
            data class UndefinedError(val error: Throwable) : Result(-2)
            data class TransientFailure(val error: Throwable) : Result(-1)
            object Ok : Result(0)
            object NotFound : Result(1)
        }
    }

    data class SubmitTransactionRequest(
        val transaction: Transaction,
        val invoiceList: InvoiceList? = null
    )

    data class SubmitTransactionResponse(
        val result: Result,
        val transaction: KinTransaction? = null
    ) {
        sealed class Result(val value: Int) {
            object UpgradeRequiredError : Result(-3)
            data class UndefinedError(val error: Throwable) : Result(-2)
            data class TransientFailure(val error: Throwable) : Result(-1)
            object Ok : Result(0)
            object InsufficientBalance : Result(1)
            object InsufficientFee : Result(2)
            object BadSequenceNumber : Result(3)
            object NoAccount : Result(4)
            object WebhookRejected : Result(5)
            data class InvoiceErrors(val errors: List<InvoiceError>) : Result(6) {
                sealed class InvoiceError(val value: Int) {
                    object UNKNOWN : InvoiceError(0)

                    /**
                     * The provided invoice has already been paid for.
                     *
                     * This is only applicable when the memo transaction type
                     *  is SPEND.
                     */
                    object ALREADY_PAID : InvoiceError(1)

                    /**
                     * The destination in the operation corresponding to this invoice
                     * is incorrect.
                     */
                    object WRONG_DESTINATION : InvoiceError(2)

                    /**
                     * One or more SKUs in the invoice was not found.
                     */
                    object SKU_NOT_FOUND : InvoiceError(3)
                }
            }
        }
    }

    data class GetTransactionHistoryRequest(
        val accountId: KinAccount.Id,
        val pagingToken: KinTransaction.PagingToken? = null,
        val order: Order = Order.Descending
    ) {
        sealed class Order(val value: Int) {
            object Ascending : Order(0)
            object Descending : Order(1)
        }
    }

    data class GetTransactionHistoryResponse(
        val result: Result,
        val transactions: List<KinTransaction>? = null
    ) {
        sealed class Result(val value: Int) {
            object UpgradeRequiredError : Result(-3)
            data class UndefinedError(val error: Throwable) : Result(-2)
            data class TransientFailure(val error: Throwable) : Result(-1)
            object Ok : Result(0)
            object NotFound : Result(1)
        }
    }

    fun getServiceConfig(
        request: GetServiceConfigRequest = GetServiceConfigRequest,
        onCompleted: (GetServiceConfigResponse) -> Unit
    )

    fun getMinKinVersion(
        request: GetMiniumumKinVersionRequest = GetMiniumumKinVersionRequest,
        onCompleted: (GetMiniumumKinVersionResponse) -> Unit
    )

    fun getRecentBlockHash(
        request: GetRecentBlockHashRequest = GetRecentBlockHashRequest,
        onCompleted: (GetRecentBlockHashResponse) -> Unit
    )

    fun getMinimumBalanceForRentExemption(
        request: GetMinimumBalanceForRentExemptionRequest,
        onCompleted: (GetMinimumBalanceForRentExemptionResponse) -> Unit
    )

    fun getTransaction(
        request: GetTransactionRequest,
        onCompleted: (GetTransactionResponse) -> Unit
    )

    fun submitTransaction(
        request: SubmitTransactionRequest,
        onCompleted: (SubmitTransactionResponse) -> Unit
    )

    fun getTransactionHistory(
        request: GetTransactionHistoryRequest,
        onCompleted: (GetTransactionHistoryResponse) -> Unit
    )
}
