package org.kin.stellarfork

import org.kin.stellarfork.codec.Base64
import org.kin.stellarfork.xdr.AccountID
import org.kin.stellarfork.xdr.Operation.OperationBody
import org.kin.stellarfork.xdr.OperationType
import org.kin.stellarfork.xdr.XdrDataOutputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.math.BigDecimal

/**
 * Abstract class for operations.
 */
abstract class Operation internal constructor() {
    /**
     * Generates Operation XDR object.
     */
    fun toXdr(): org.kin.stellarfork.xdr.Operation {
        return org.kin.stellarfork.xdr.Operation().apply {
            this@Operation.sourceAccount?.let { keyPair ->
                sourceAccount = AccountID().apply {
                    accountID = keyPair.xdrPublicKey
                }
            }
            body = toOperationBody()
        }
    }

    /**
     * Returns base64-encoded Operation XDR object.
     */
    fun toXdrBase64(): String {
        return try {
            val operation = toXdr()
            val outputStream = ByteArrayOutputStream()
            val xdrOutputStream = XdrDataOutputStream(outputStream)
            org.kin.stellarfork.xdr.Operation.encode(xdrOutputStream, operation)
            val base64Codec = Base64()
            base64Codec.encodeAsString(outputStream.toByteArray())
        } catch (e: IOException) {
            throw AssertionError(e)
        }
    }

    /**
     * Returns operation source account.
     */
    /**
     * Sets operation source account.
     *
     * @param keypair
     */
    var sourceAccount: KeyPair? = null

    /**
     * Generates OperationBody XDR object
     *
     * @return OperationBody XDR object
     */
    abstract fun toOperationBody(): OperationBody

    companion object {
        private val ONE = BigDecimal(10).pow(5)

        @JvmStatic
        fun toXdrAmount(value: String): Long = BigDecimal(value).multiply(ONE).longValueExact()

        @JvmStatic
        fun fromXdrAmount(value: Long): String = BigDecimal(value).divide(ONE).toPlainString()

        /**
         * Returns new Operation object from Operation XDR object.
         *
         * @param xdr XDR object
         */
        @JvmStatic
        fun fromXdr(xdr: org.kin.stellarfork.xdr.Operation): Operation {
            val body = xdr.body!!
            val operation: Operation = when (body.discriminant) {
                OperationType.CREATE_ACCOUNT -> CreateAccountOperation.Builder(body.createAccountOp!!).build()
                OperationType.PAYMENT -> PaymentOperation.Builder(body.paymentOp!!).build()
                OperationType.PATH_PAYMENT -> PathPaymentOperation.Builder(body.pathPaymentOp!!).build()
                OperationType.MANAGE_OFFER -> ManageOfferOperation.Builder(body.manageOfferOp!!).build()
                OperationType.CREATE_PASSIVE_OFFER -> CreatePassiveOfferOperation.Builder(body.createPassiveOfferOp!!).build()
                OperationType.SET_OPTIONS -> SetOptionsOperation.Builder(body.setOptionsOp!!).build()
                OperationType.CHANGE_TRUST -> ChangeTrustOperation.Builder(body.changeTrustOp!!).build()
                OperationType.ALLOW_TRUST -> AllowTrustOperation.Builder(body.allowTrustOp!!).build()
                OperationType.ACCOUNT_MERGE -> AccountMergeOperation.Builder(body).build()
                OperationType.MANAGE_DATA -> ManageDataOperation.Builder(body.manageDataOp!!).build()
                else -> throw RuntimeException("Unknown operation body " + body.discriminant)
            }
            xdr.sourceAccount?.let {
                operation.sourceAccount = KeyPair.fromXdrPublicKey(xdr.sourceAccount!!.accountID!!)
            }
            return operation
        }
    }
}
