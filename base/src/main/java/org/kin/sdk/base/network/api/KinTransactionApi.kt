package org.kin.sdk.base.network.api

import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.TransactionHash
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.tools.Observer

interface KinTransactionApi {

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
            object UpgradeRequiredError: Result(-3)
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
            object UpgradeRequiredError: Result(-3)
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
            object UpgradeRequiredError: Result(-3)
            data class UndefinedError(val error: Throwable) : Result(-2)
            data class TransientFailure(val error: Throwable) : Result(-1)
            object Ok : Result(0)
        }
    }

    data class SubmitTransactionRequest(val transactionEnvelopeXdr: ByteArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as SubmitTransactionRequest

            if (!transactionEnvelopeXdr.contentEquals(other.transactionEnvelopeXdr)) return false

            return true
        }

        override fun hashCode(): Int {
            return transactionEnvelopeXdr.contentHashCode()
        }
    }

    data class SubmitTransactionResponse(
        val result: Result,
        val transaction: KinTransaction? = null
    ) {
        sealed class Result(val value: Int) {
            object UpgradeRequiredError: Result(-3)
            data class UndefinedError(val error: Throwable) : Result(-2)
            data class TransientFailure(val error: Throwable) : Result(-1)
            object Ok : Result(0)
            object InsufficientBalance : Result(1)
            object InsufficientFee : Result(2)
            object BadSequenceNumber : Result(3)
            object NoAccount : Result(4)
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

    fun streamNewTransactions(kinAccountId: KinAccount.Id): Observer<KinTransaction>
}




