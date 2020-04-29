package org.kin.stellarfork.requests

import com.google.gson.reflect.TypeToken
import com.here.oksse.ServerSentEvent
import okhttp3.OkHttpClient
import org.kin.stellarfork.responses.LedgerResponse
import org.kin.stellarfork.responses.Page
import java.io.IOException
import java.net.URI

/**
 * Builds requests connected to ledgers.
 */
class LedgersRequestBuilder(
    httpClient: OkHttpClient,
    serverURI: URI
) : RequestBuilder(httpClient, serverURI, "ledgers"), StreamingProtocol<LedgerResponse>{
    /**
     * Requests specific `uri` and returns [LedgerResponse].
     * This method is helpful for getting the links.
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun ledger(uri: URI): LedgerResponse? {
        return execute(uri)
    }

    /**
     * Requests `GET /ledgers/{ledgerSeq}`
     *
     * @param ledgerSeq Ledger to fetch
     * @throws IOException
     * @see [Ledger Details](https://www.stellar.org/developers/horizon/reference/ledgers-single.html)
     */
    @Throws(IOException::class)
    fun ledger(ledgerSeq: Long): LedgerResponse? {
        setSegments("ledgers", ledgerSeq.toString())
        return this.ledger(buildUri())
    }

    /**
     * Allows to stream SSE events from horizon.
     * Certain endpoints in Horizon can be called in streaming mode using Server-Sent Events.
     * This mode will keep the connection to horizon open and horizon will continue to return
     * responses as ledgers close.
     *
     * @param listener [EventListener] implementation with [LedgerResponse] type
     * @return ServerSentEvent object, so you can `close()` connection when not needed anymore
     * @see [Server-Sent Events](http://www.w3.org/TR/eventsource/)
     *
     * @see [Response Format documentation](https://www.stellar.org/developers/horizon/learn/responses.html)
     */
    override fun stream(listener: EventListener<LedgerResponse>): ServerSentEvent {
        return StreamHandler(object : TypeToken<LedgerResponse>() {})
            .handleStream(buildUri(), listener)
    }

    override fun cursor(cursor: String?): LedgersRequestBuilder {
        super.cursor(cursor)
        return this
    }

    override fun limit(number: Int): LedgersRequestBuilder {
        super.limit(number)
        return this
    }

    override fun order(direction: Order): LedgersRequestBuilder {
        super.order(direction)
        return this
    }

    /**
     * Build and execute request.
     *
     * @return [Page] of [LedgerResponse]
     * @throws TooManyRequestsException when too many requests were sent to the Horizon server.
     * @throws IOException
     */
    @Throws(IOException::class, TooManyRequestsException::class)
    fun execute(): Page<LedgerResponse>? {
        return super.execute(buildUri())
    }
}
