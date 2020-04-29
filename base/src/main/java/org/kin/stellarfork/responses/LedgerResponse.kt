package org.kin.stellarfork.responses

import com.google.gson.annotations.SerializedName

/**
 * Represents ledger response.
 *
 * @see [Ledger documentation](https://www.stellar.org/developers/horizon/reference/resources/ledger.html)
 *
 * @see org.kin.stellarfork.requests.LedgersRequestBuilder
 *
 * @see Server.ledgers
 */
data class LedgerResponse internal constructor(
    @field:SerializedName("sequence") val sequence: Long,
    @field:SerializedName("hash") val hash: String,
    @field:SerializedName("paging_token") val pagingToken: String,
    @field:SerializedName("prev_hash") val prevHash: String,
    @field:SerializedName("transaction_count") val transactionCount: Int,
    @field:SerializedName("operation_count") val operationCount: Int,
    @field:SerializedName("closed_at") val closedAt: String,
    @field:SerializedName("total_coins") val totalCoins: String,
    @field:SerializedName("fee_pool") val feePool: String,
    @field:SerializedName("base_fee_in_stroops") val baseFee: Long,
    @field:SerializedName("base_reserve") val baseReserve: String,
    @field:SerializedName("max_tx_set_size") val maxTxSetSize: Int,
    @field:SerializedName("_links") val links: Links
) : Response() {

    /**
     * Links connected to ledger.
     */
    data class Links internal constructor(
        @field:SerializedName("effects") val effects: Link,
        @field:SerializedName("operations") val operations: Link,
        @field:SerializedName("self") val self: Link,
        @field:SerializedName("transactions") val transactions: Link
    )
}
