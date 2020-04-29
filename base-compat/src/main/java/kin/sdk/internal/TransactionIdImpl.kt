package kin.sdk.internal

import kin.sdk.TransactionId
import org.kin.sdk.base.models.TransactionHash

internal data class TransactionIdImpl(private val txnHash: TransactionHash) : TransactionId {
    override fun id(): String = txnHash.toString()
}
