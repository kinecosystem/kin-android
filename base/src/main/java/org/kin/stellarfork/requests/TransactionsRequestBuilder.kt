package org.kin.stellarfork.requests

import com.google.gson.reflect.TypeToken
import com.here.oksse.ServerSentEvent
import okhttp3.FormBody
import okhttp3.OkHttpClient
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.Transaction
import org.kin.stellarfork.responses.Page
import org.kin.stellarfork.responses.SubmitTransactionResponse
import org.kin.stellarfork.responses.TransactionResponse
import java.io.IOException
import java.net.URI

/**
 * Builds requests connected to transactions.
 */
class TransactionsRequestBuilder(
    httpClient: OkHttpClient,
    serverURI: URI?
) : RequestBuilder(httpClient, serverURI!!, "transactions"),
    StreamingProtocol<TransactionResponse> {

    companion object {
        private const val TEMPORARY_REDIRECT = 307
        private const val LOCATION_HEADER = "Location"
    }

    /**
     * Requests specific `uri` and returns [TransactionResponse].
     * This method is helpful for getting the links.
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun transaction(uri: URI): TransactionResponse? {
        return execute(uri)
    }

    /**
     * Requests `GET /transactions/{transactionId}`
     *
     * @param transactionId Transaction to fetch
     * @throws IOException
     * @see [Transaction Details](https://www.stellar.org/developers/horizon/reference/transactions-single.html)
     */
    @Throws(IOException::class)
    fun transaction(transactionId: String?): TransactionResponse? {
        setSegments("transactions", transactionId)
        return this.transaction(buildUri())
    }

    /**
     * Builds request to `GET /accounts/{account}/transactions`
     *
     * @param account Account for which to get transactions
     * @see [Transactions for Account](https://www.stellar.org/developers/horizon/reference/transactions-for-account.html)
     */
    fun forAccount(account: KeyPair): TransactionsRequestBuilder {
        setSegments("accounts", account.accountId, "transactions")
        return this
    }

    /**
     * Builds request to `GET /ledgers/{ledgerSeq}/transactions`
     *
     * @param ledgerSeq Ledger for which to get transactions
     * @see [Transactions for Ledger](https://www.stellar.org/developers/horizon/reference/transactions-for-ledger.html)
     */
    fun forLedger(ledgerSeq: Long): TransactionsRequestBuilder {
        setSegments("ledgers", ledgerSeq.toString(), "transactions")
        return this
    }


    /**
     * Submits transaction to the network.
     *
     * @param transaction transaction to submit to the network.
     * @return [SubmitTransactionResponse]
     */
    @Throws(IOException::class)
    fun submitTransaction(transaction: Transaction): SubmitTransactionResponse? =
        submitTransaction(transaction.toEnvelopeXdrBase64())

    /**
     * Submits transaction to the network.
     *
     * @param transactionXdrBase64 the base64 encoded string of the transaction xdr to submit to the network.
     * @return [SubmitTransactionResponse]
     */
    @Throws(IOException::class)
    fun submitTransaction(transactionXdrBase64: String): SubmitTransactionResponse? {
        setSegments("transactions")
        return executePost(
            buildUri(),
            FormBody.Builder()
                .add("tx", transactionXdrBase64)
                .build()
        )
    }

    /**
     * Allows to stream SSE events from horizon.
     * Certain endpoints in Horizon can be called in streaming mode using Server-Sent Events.
     * This mode will keep the connection to horizon open and horizon will continue to return
     * responses as ledgers close.
     *
     * @param listener [EventListener] implementation with [TransactionResponse] type
     * @return ServerSentEvent object, so you can `close()` connection when not needed anymore
     * @see [Server-Sent Events](http://www.w3.org/TR/eventsource/)
     *
     * @see [Response Format documentation](https://www.stellar.org/developers/horizon/learn/responses.html)
     */
    override fun stream(listener: EventListener<TransactionResponse>): ServerSentEvent {
        return StreamHandler(object : TypeToken<TransactionResponse>() {})
            .handleStream(buildUri(), listener)
    }

    override fun cursor(cursor: String?): TransactionsRequestBuilder {
        super.cursor(cursor)
        return this
    }

    override fun limit(number: Int): TransactionsRequestBuilder {
        super.limit(number)
        return this
    }

    override fun order(direction: Order): TransactionsRequestBuilder {
        super.order(direction)
        return this
    }

    fun execute(): Page<TransactionResponse>? {
        return super.execute(buildUri())
    }
}
