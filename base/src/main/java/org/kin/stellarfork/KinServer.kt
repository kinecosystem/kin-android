package org.kin.stellarfork

import org.kin.stellarfork.requests.AccountsRequestBuilder
import org.kin.stellarfork.requests.LedgersRequestBuilder
import org.kin.stellarfork.requests.TransactionsRequestBuilder

interface KinServer {
    /**
     * Returns [AccountsRequestBuilder] instance.
     */
    fun accounts(): AccountsRequestBuilder

    /**
     * Returns [LedgersRequestBuilder] instance.
     */
    fun ledgers(): LedgersRequestBuilder

    /**
     * Returns [TransactionsRequestBuilder] instance.
     */
    fun transactions(): TransactionsRequestBuilder
}
