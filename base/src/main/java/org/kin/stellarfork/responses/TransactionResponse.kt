package org.kin.stellarfork.responses

import com.google.gson.annotations.SerializedName
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.LedgerEntryChanges
import org.kin.stellarfork.Memo
import org.kin.stellarfork.Operation
import org.kin.stellarfork.Util
import org.kin.stellarfork.xdr.Transaction
import org.kin.stellarfork.xdr.TransactionMeta
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.util.ArrayList

/**
 * Represents transaction response.
 *
 * @see [Transaction documentation](https://www.stellar.org/developers/horizon/reference/resources/transaction.html)
 *
 * @see org.kin.stellarfork.requests.TransactionsRequestBuilder
 *
 * @see Server.transactions
 */
data class TransactionResponse internal constructor(
    @field:SerializedName("hash") val hash: String,
    @field:SerializedName("ledger") val ledger: Long,
    @field:SerializedName("created_at") val createdAt: String,
    @field:SerializedName("source_account") val sourceAccount: KeyPair,
    @field:SerializedName("paging_token") val pagingToken: String,
    @field:SerializedName("source_account_sequence") val sourceAccountSequence: Long,
    @field:SerializedName("fee_paid") val feePaid: Long,
    @field:SerializedName("operation_count") val operationCount: Int,
    @field:SerializedName("envelope_xdr") val envelopeXdr: String,
    @field:SerializedName("result_xdr") val resultXdr: String,
    @field:SerializedName("result_meta_xdr") val resultMetaXdr: String,
    // GSON won't serialize `transient` variables automatically. We need this behaviour
// because Memo is an abstract class and GSON tries to instantiate it.
    @field:Transient private var memo: Memo?,
    @field:SerializedName("_links") val links: Links
) : Response() {

    fun getMemo(): Memo? {
        return memo
    }

    val operations: List<Operation>?
        get() {
            val envelopeXdr = envelopeXdr
            try {
                val transaction = extractTransaction(envelopeXdr)
                val xdrOperations =
                    transaction.operations
                val operationsList =
                    ArrayList<Operation>(xdrOperations.size)
                for (xdrOperation in xdrOperations) {
                    operationsList.add(Operation.fromXdr(xdrOperation!!))
                }
                return operationsList
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

    @Throws(IOException::class)
    private fun extractTransaction(envelopeXdr: String): Transaction {
        val xdrDataInputStream =
            Util.createXdrDataInputStream(envelopeXdr)
        return Transaction.decode(xdrDataInputStream)
    }

    val ledgerChanges: List<LedgerEntryChanges>?
        get() {
            val resultMetaXdr = resultMetaXdr
            try {
                val transactionMeta = extractTransactionMeta(resultMetaXdr)
                val operationMetas = transactionMeta.operations
                val ledgerChangesList =
                    ArrayList<LedgerEntryChanges>(operationMetas.size)
                for (operationMeta in operationMetas) {
                    ledgerChangesList.add(LedgerEntryChanges.fromXdr(operationMeta!!.changes!!))
                }
                return ledgerChangesList
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

    @Throws(IOException::class)
    private fun extractTransactionMeta(envelopeXdr: String): TransactionMeta {
        val xdrDataInputStream =
            Util.createXdrDataInputStream(envelopeXdr)
        return TransactionMeta.decode(xdrDataInputStream)
    }

    fun setMemo(memo: Memo?) {
        if (this.memo != null) {
            throw RuntimeException("Memo has been already set.")
        }
        this.memo = memo
    }

    /**
     * Links connected to transaction.
     */
    data class Links internal constructor(
        @field:SerializedName("account") val account: Link,
        @field:SerializedName("effects") val effects: Link,
        @field:SerializedName("ledger") val ledger: Link,
        @field:SerializedName("operations") val operations: Link,
        @field:SerializedName("self") val self: Link,
        @field:SerializedName("precedes") val precedes: Link,
        @field:SerializedName("succeeds") val succeeds: Link
    )
}
