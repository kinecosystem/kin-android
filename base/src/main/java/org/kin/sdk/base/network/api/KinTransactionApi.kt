package org.kin.sdk.base.network.api

import org.kin.sdk.base.models.InvoiceList
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.TransactionHash
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.stellarfork.codec.Base64

interface KinTransactionApi {

    data class GetTransactionHistoryRequest(
        val accountId: KinAccount.Id,
        val pagingToken: KinTransaction.PagingToken? = null,
        val order: Order = Order.Descending
    ) {
        sealed class Order(val value: Int) {
            object Ascending : Order(0)
            object Descending : Order(1)

            override fun toString(): String {
                return "Order(value=${if (value == 0) "ASC" else "DESC"})"
            }
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

    data class GetMinFeeForTransactionResponse(
        val result: Result,
        val minFee: QuarkAmount? = null
    ) {
        sealed class Result(val value: Int) {
            object UpgradeRequiredError : Result(-3)
            data class UndefinedError(val error: Throwable) : Result(-2)
            data class TransientFailure(val error: Throwable) : Result(-1)
            object Ok : Result(0)
        }
    }

    data class SubmitTransactionRequest(
        val transactionEnvelopeXdr: ByteArray,
        val invoiceList: InvoiceList? = null
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is SubmitTransactionRequest) return false

            if (!transactionEnvelopeXdr.contentEquals(other.transactionEnvelopeXdr)) return false
            if (invoiceList != other.invoiceList) return false

            return true
        }

        override fun hashCode(): Int {
            var result = transactionEnvelopeXdr.contentHashCode()
            result = 31 * result + (invoiceList?.hashCode() ?: 0)
            return result
        }

        override fun toString(): String {
            return "SubmitTransactionRequest(transactionEnvelopeXdr=${Base64.encodeBase64String(transactionEnvelopeXdr)}, invoiceList=$invoiceList)"
        }
    }

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

    fun getTransactionHistory(
        request: GetTransactionHistoryRequest,
        onCompleted: (GetTransactionHistoryResponse) -> Unit
    )

    fun getTransaction(
        request: GetTransactionRequest,
        onCompleted: (GetTransactionResponse) -> Unit
    )

    fun getTransactionMinFee(
        onCompleted: (GetMinFeeForTransactionResponse) -> Unit
    )

    fun submitTransaction(
        request: SubmitTransactionRequest,
        onCompleted: (SubmitTransactionResponse) -> Unit
    )
}




