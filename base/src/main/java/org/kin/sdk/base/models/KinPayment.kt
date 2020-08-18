package org.kin.sdk.base.models

data class KinPayment @JvmOverloads constructor(
    val id: Id,
    val status: Status,
    val sourceAccountId: KinAccount.Id,
    val destinationAccountId: KinAccount.Id,
    val amount: KinAmount,
    val fee: QuarkAmount,
    val memo: KinMemo,
    val timestamp: Long,
    val invoice: Invoice? = null
) {
    data class Id(val transactionHash: TransactionHash, val offset: Int) {
        val value: ByteArray by lazy {
            transactionHash.rawValue + offset.toByte()
        }
    }

    sealed class Status(val value: Int) {
        object InFlight : Status(0)
        object Success : Status(1)
        data class Error(val reason: Reason) : Status(2) {
            data class Reason(val errorCode: Int, val displayableReason: String)
        }
    }
}
