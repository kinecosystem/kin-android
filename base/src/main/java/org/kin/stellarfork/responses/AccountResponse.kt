package org.kin.stellarfork.responses

import com.google.gson.annotations.SerializedName
import org.kin.stellarfork.Asset
import org.kin.stellarfork.AssetTypeNative
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.TransactionBuilderAccount

/**
 * Represents account response.
 *
 * @see [Account documentation](https://www.stellar.org/developers/horizon/reference/resources/account.html)
 *
 * @see org.kin.stellarfork.requests.AccountsRequestBuilder
 *
 * @see Server.accounts
 */
data class AccountResponse @JvmOverloads constructor(
    /* KeyPairTypeAdapter used */
    @SerializedName("account_id") override var keypair: KeyPair,
    @SerializedName("sequence") override var sequenceNumber: Long,
    @SerializedName("paging_token") val pagingToken: String? = null,
    @SerializedName("subentry_count") val subentryCount: Int? = null,
    @SerializedName("inflation_destination") val inflationDestination: String? = null,
    @SerializedName("home_domain") val homeDomain: String? = null,
    @SerializedName("thresholds") val thresholds: Thresholds? = null,
    @SerializedName("flags") val flags: Flags? = null,
    @SerializedName("balances") val balances: Array<Balance> = emptyArray(),
    @SerializedName("signers") val signers: Array<Signer> = emptyArray(),
    @SerializedName("_links") val links: Links? = null
) : Response(), TransactionBuilderAccount {

    override var incrementedSequenceNumber: Long = 0
        get() = sequenceNumber + 1
        set(value) { field = value}

    override fun incrementSequenceNumber() {
        sequenceNumber = incrementedSequenceNumber
    }

    /**
     * Represents account thresholds.
     */
    data class Thresholds internal constructor(
        @field:SerializedName("low_threshold") val lowThreshold: Int,
        @field:SerializedName("med_threshold") val medThreshold: Int,
        @field:SerializedName("high_threshold") val highThreshold: Int
    )

    /**
     * Represents account flags.
     */
    data class Flags internal constructor(
        @field:SerializedName("auth_required") val authRequired: Boolean, @field:SerializedName(
            "auth_revocable"
        ) val authRevocable: Boolean
    )

    /**
     * Represents account balance.
     */
    data class Balance internal constructor(
        @SerializedName("asset_type")
        val assetType: String,
        @SerializedName("asset_code")
        val assetCode: String,
        @SerializedName("asset_issuer")
        private val assetIssuer: String,
        @SerializedName("balance")
        val balance: String,
        @SerializedName("limit")
        val limit: String
    ) {
        val asset: Asset
            get() = if (assetType == "native") {
                AssetTypeNative
            } else {
                Asset.createNonNativeAsset(assetCode, getAssetIssuer())
            }

        fun getAssetIssuer(): KeyPair {
            return KeyPair.fromAccountId(assetIssuer)
        }
    }

    /**
     * Represents account signers.
     */
    data class Signer internal constructor(
        @SerializedName("public_key")
        val accountId: String,
        @SerializedName("weight")
        val weight: Int
    )

    /**
     * Links connected to account.
     */
    data class Links internal constructor(
        @field:SerializedName("effects") val effects: Link,
        @field:SerializedName("offers") val offers: Link,
        @field:SerializedName("operations") val operations: Link,
        @field:SerializedName("self") val self: Link,
        @field:SerializedName("transactions") val transactions: Link
    )
}
