package org.kin.stellarfork

import org.kin.stellarfork.xdr.AccountID
import org.kin.stellarfork.xdr.Operation.OperationBody
import org.kin.stellarfork.xdr.OperationType
import org.kin.stellarfork.xdr.SetOptionsOp
import org.kin.stellarfork.xdr.Signer
import org.kin.stellarfork.xdr.SignerKey
import org.kin.stellarfork.xdr.String32
import org.kin.stellarfork.xdr.Uint32

/**
 * Represents [SetOptions](https://www.stellar.org/developers/learn/concepts/list-of-operations.html#set-options) operation.
 *
 * @see [List of Operations](https://www.stellar.org/developers/learn/concepts/list-of-operations.html)
 */
class SetOptionsOperation private constructor(
    /**
     * Account of the inflation destination.
     */
    val inflationDestination: KeyPair?,
    /**
     * Indicates which flags to clear. For details about the flags, please refer to the [accounts doc](https://www.stellar.org/developers/learn/concepts/accounts.html).
     * You can also use [AccountFlag] enum.
     */
    val clearFlags: Int?,
    /**
     * Indicates which flags to set. For details about the flags, please refer to the [accounts doc](https://www.stellar.org/developers/learn/concepts/accounts.html).
     * You can also use [AccountFlag] enum.
     */
    val setFlags: Int?,
    /**
     * Weight of the master key.
     */
    val masterKeyWeight: Int?,
    /**
     * A number from 0-255 representing the threshold this account sets on all operations it performs that have [a low threshold](https://www.stellar.org/developers/learn/concepts/multi-sig.html).
     */
    val lowThreshold: Int?,
    /**
     * A number from 0-255 representing the threshold this account sets on all operations it performs that have [a medium threshold](https://www.stellar.org/developers/learn/concepts/multi-sig.html).
     */
    val mediumThreshold: Int?,
    /**
     * A number from 0-255 representing the threshold this account sets on all operations it performs that have [a high threshold](https://www.stellar.org/developers/learn/concepts/multi-sig.html).
     */
    val highThreshold: Int?,
    /**
     * The home domain of an account.
     */
    val homeDomain: String?,
    /**
     * Additional signer added/removed in this operation.
     */
    val signer: SignerKey?,
    /**
     * Additional signer weight. The signer is deleted if the weight is 0.
     */
    val signerWeight: Int?
) : Operation() {
    override fun toOperationBody(): OperationBody {
        val op = SetOptionsOp()
        if (inflationDestination != null) {
            val inflationDestination = AccountID()
            inflationDestination.accountID = this.inflationDestination.xdrPublicKey
            op.inflationDest = inflationDestination
        }
        if (clearFlags != null) {
            val clearFlags = Uint32()
            clearFlags.uint32 = this.clearFlags
            op.clearFlags = clearFlags
        }
        if (setFlags != null) {
            val setFlags = Uint32()
            setFlags.uint32 = this.setFlags
            op.setFlags = setFlags
        }
        if (masterKeyWeight != null) {
            val uint32 = Uint32()
            uint32.uint32 = masterKeyWeight
            op.masterWeight = uint32
        }
        if (lowThreshold != null) {
            val uint32 = Uint32()
            uint32.uint32 = lowThreshold
            op.lowThreshold = uint32
        }
        if (mediumThreshold != null) {
            val uint32 = Uint32()
            uint32.uint32 = mediumThreshold
            op.medThreshold = uint32
        }
        if (highThreshold != null) {
            val uint32 = Uint32()
            uint32.uint32 = highThreshold
            op.highThreshold = uint32
        }
        if (homeDomain != null) {
            val homeDomain = String32()
            homeDomain.string32 = this.homeDomain
            op.homeDomain = homeDomain
        }
        if (signer != null) {
            val signer = Signer()
            val weight = Uint32()
            weight.uint32 = signerWeight!! and 0xFF
            signer.key = this.signer
            signer.weight = weight
            op.signer = signer
        }
        return OperationBody().apply {
            discriminant = OperationType.SET_OPTIONS
            setOptionsOp = op
        }
    }

    /**
     * Builds SetOptions operation.
     *
     * @see SetOptionsOperation
     */
    class Builder {
        private var inflationDestination: KeyPair? = null
        private var clearFlags: Int? = null
        private var setFlags: Int? = null
        private var masterKeyWeight: Int? = null
        private var lowThreshold: Int? = null
        private var mediumThreshold: Int? = null
        private var highThreshold: Int? = null
        private var homeDomain: String? = null
        private var signer: SignerKey? = null
        private var signerWeight: Int? = null
        private var sourceAccount: KeyPair? = null

        internal constructor(op: SetOptionsOp) {
            if (op.inflationDest != null) {
                inflationDestination = KeyPair.fromXdrPublicKey(
                    op.inflationDest!!.accountID!!
                )
            }
            if (op.clearFlags != null) {
                clearFlags = op.clearFlags!!.uint32
            }
            if (op.setFlags != null) {
                setFlags = op.setFlags!!.uint32
            }
            if (op.masterWeight != null) {
                masterKeyWeight = op.masterWeight!!.uint32!!.toInt()
            }
            if (op.lowThreshold != null) {
                lowThreshold = op.lowThreshold!!.uint32!!.toInt()
            }
            if (op.medThreshold != null) {
                mediumThreshold = op.medThreshold!!.uint32!!.toInt()
            }
            if (op.highThreshold != null) {
                highThreshold = op.highThreshold!!.uint32!!.toInt()
            }
            if (op.homeDomain != null) {
                homeDomain = op.homeDomain!!.string32
            }
            if (op.signer != null) {
                signer = op.signer!!.key
                signerWeight = op.signer!!.weight!!.uint32!!.toInt() and 0xFF
            }
        }

        /**
         * Creates a new SetOptionsOperation builder.
         */
        constructor() {}

        /**
         * Sets the inflation destination for the account.
         *
         * @param inflationDestination The inflation destination account.
         * @return Builder object so you can chain methods.
         */
        fun setInflationDestination(inflationDestination: KeyPair?): Builder {
            this.inflationDestination = inflationDestination
            return this
        }

        /**
         * Clears the given flags from the account.
         *
         * @param clearFlags For details about the flags, please refer to the [accounts doc](https://www.stellar.org/developers/learn/concepts/accounts.html).
         * @return Builder object so you can chain methods.
         */
        fun setClearFlags(clearFlags: Int): Builder {
            this.clearFlags = clearFlags
            return this
        }

        /**
         * Sets the given flags on the account.
         *
         * @param setFlags For details about the flags, please refer to the [accounts doc](https://www.stellar.org/developers/learn/concepts/accounts.html).
         * @return Builder object so you can chain methods.
         */
        fun setSetFlags(setFlags: Int): Builder {
            this.setFlags = setFlags
            return this
        }

        /**
         * Weight of the master key.
         *
         * @param masterKeyWeight Number between 0 and 255
         * @return Builder object so you can chain methods.
         */
        fun setMasterKeyWeight(masterKeyWeight: Int): Builder {
            this.masterKeyWeight = masterKeyWeight
            return this
        }

        /**
         * A number from 0-255 representing the threshold this account sets on all operations it performs that have a low threshold.
         *
         * @param lowThreshold Number between 0 and 255
         * @return Builder object so you can chain methods.
         */
        fun setLowThreshold(lowThreshold: Int): Builder {
            this.lowThreshold = lowThreshold
            return this
        }

        /**
         * A number from 0-255 representing the threshold this account sets on all operations it performs that have a medium threshold.
         *
         * @param mediumThreshold Number between 0 and 255
         * @return Builder object so you can chain methods.
         */
        fun setMediumThreshold(mediumThreshold: Int): Builder {
            this.mediumThreshold = mediumThreshold
            return this
        }

        /**
         * A number from 0-255 representing the threshold this account sets on all operations it performs that have a high threshold.
         *
         * @param highThreshold Number between 0 and 255
         * @return Builder object so you can chain methods.
         */
        fun setHighThreshold(highThreshold: Int): Builder {
            this.highThreshold = highThreshold
            return this
        }

        /**
         * Sets the account's home domain address used in [Federation](https://www.stellar.org/developers/learn/concepts/federation.html).
         *
         * @param homeDomain A string of the address which can be up to 32 characters.
         * @return Builder object so you can chain methods.
         */
        fun setHomeDomain(homeDomain: String): Builder {
            require(homeDomain.length <= 32) { "Home domain must be <= 32 characters" }
            this.homeDomain = homeDomain
            return this
        }

        /**
         * Add, update, or remove a signer from the account. Signer is deleted if the weight = 0;
         *
         * @param signer The signer key. Use [Signer] helper to create this object.
         * @param weight The weight to attach to the signer (0-255).
         * @return Builder object so you can chain methods.
         */
        fun setSigner(
            signer: SignerKey,
            weight: Int
        ): Builder {
            this.signer = signer
            signerWeight = weight and 0xFF
            return this
        }

        /**
         * Sets the source account for this operation.
         *
         * @param sourceAccount The operation's source account.
         * @return Builder object so you can chain methods.
         */
        fun setSourceAccount(sourceAccount: KeyPair?): Builder {
            this.sourceAccount = sourceAccount
            return this
        }

        /**
         * Builds an operation
         */
        fun build(): SetOptionsOperation {
            val operation = SetOptionsOperation(
                inflationDestination,
                clearFlags,
                setFlags,
                masterKeyWeight,
                lowThreshold,
                mediumThreshold,
                highThreshold,
                homeDomain,
                signer,
                signerWeight
            )
            if (sourceAccount != null) {
                operation.sourceAccount = sourceAccount
            }
            return operation
        }
    }

}
