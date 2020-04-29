package org.kin.stellarfork.responses

import com.google.gson.annotations.SerializedName
import org.kin.stellarfork.codec.Base64
import org.kin.stellarfork.xdr.OperationType
import org.kin.stellarfork.xdr.TransactionResult
import org.kin.stellarfork.xdr.XdrDataInputStream
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.ArrayList

/**
 * Represents server response after submitting transaction.
 *
 * @see Server.submitTransaction
 */
class SubmitTransactionResponse internal constructor(
    /**
     * Additional information returned by a server. This will be `null` if transaction succeeded.
     */
    @field:SerializedName("extras") val extras: Extras,
    @field:SerializedName("ledger") val ledger: Long?,
    @field:SerializedName("hash") val hash: String,
    @field:SerializedName("envelope_xdr") private val envelopeXdr: String,
    @field:SerializedName("result_xdr") private val resultXdr: String
) : Response() {

    val isSuccess: Boolean
        get() = ledger != null

    fun getEnvelopeXdr(): String = if (isSuccess) envelopeXdr else extras.envelopeXdr

    fun getResultXdr(): String = if (isSuccess) resultXdr else extras.resultXdr

    /**
     * Helper method that returns Offer ID for ManageOffer from TransactionResult Xdr.
     * This is helpful when you need ID of an offer to update it later.
     *
     * @param position Position of ManageOffer operation. If ManageOffer is second operation in this transaction this should be equal `1`.
     * @return Offer ID or `null` when operation at `position` is not a ManageOffer operation or error has occurred.
     */
    fun getOfferIdFromResult(position: Int): Long? {
        if (!isSuccess) {
            return null
        }
        val base64Codec = Base64()
        val bytes = base64Codec.decode(getResultXdr())
        val inputStream = ByteArrayInputStream(bytes)
        val xdrInputStream = XdrDataInputStream(inputStream)
        val result: TransactionResult
        result = try {
            TransactionResult.decode(xdrInputStream)
        } catch (e: IOException) {
            return null
        }
        if (result.result?.results?.get(position) == null) {
            return null
        }
        if (result.result?.results?.get(position)?.tr?.discriminant != OperationType.MANAGE_OFFER) {
            return null
        }
        return if (result.result?.results?.get(0)?.tr?.manageOfferResult?.success?.offer?.offer == null) {
            null
        } else result.result?.results?.get(0)?.tr?.manageOfferResult?.success?.offer?.offer?.offerID?.uint64
    }

    /**
     * Additional information returned by a server.
     */
    data class Extras internal constructor(
        /**
         * Returns XDR TransactionEnvelope base64-encoded string.
         * Use [xdr-viewer](http://stellar.github.io/xdr-viewer/) to debug.
         */
        @field:SerializedName("envelope_xdr") val envelopeXdr: String,
        /**
         * Returns XDR TransactionResult base64-encoded string
         * Use [xdr-viewer](http://stellar.github.io/xdr-viewer/) to debug.
         */
        @field:SerializedName("result_xdr") val resultXdr: String,
        /**
         * Returns ResultCodes object that contains result codes for transaction.
         */
        @field:SerializedName("result_codes") val resultCodes: ResultCodes
    ) {
        /**
         * Contains result codes for this transaction.
         *
         * @see [Possible values](https://github.com/stellar/horizon/blob/master/src/github.com/stellar/horizon/codes/main.go)
         */
        data class ResultCodes(
            @field:SerializedName("transaction") val transactionResultCode: String,
            @field:SerializedName("operations") val operationsResultCodes: ArrayList<String>
        )
    }
}
