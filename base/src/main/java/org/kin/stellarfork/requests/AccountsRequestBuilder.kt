package org.kin.stellarfork.requests

import com.google.gson.reflect.TypeToken
import com.here.oksse.ServerSentEvent
import okhttp3.OkHttpClient
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.responses.AccountResponse
import org.kin.stellarfork.responses.Page
import java.io.IOException
import java.net.URI

/**
 * Builds requests connected to accounts.
 */
class AccountsRequestBuilder(
    httpClient: OkHttpClient,
    serverURI: URI
) : RequestBuilder(httpClient, serverURI, "accounts"), StreamingProtocol<AccountResponse> {
    /**
     * Requests specific `uri` and returns [AccountResponse].
     * This method is helpful for getting the links.
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun account(uri: URI): AccountResponse? {
        return execute(uri)
    }

    /**
     * Requests `GET /accounts/{account}`
     *
     * @param account Account to fetch
     * @throws IOException
     * @see [Account Details](https://www.stellar.org/developers/horizon/reference/accounts-single.html)
     */
    @Throws(IOException::class)
    fun account(account: KeyPair): AccountResponse? {
        setSegments("accounts", account.accountId)
        return this.account(buildUri())
    }

    fun forAccount(account: KeyPair): AccountsRequestBuilder {
        setSegments("accounts", account.accountId)
        return this
    }

    /**
     * Allows to stream SSE events from horizon.
     * Certain endpoints in Horizon can be called in streaming mode using Server-Sent Events.
     * This mode will keep the connection to horizon open and horizon will continue to return
     * responses as ledgers close.
     *
     * @param listener [EventListener] implementation with [AccountResponse] type
     * @return ServerSentEvent object, so you can `close()` connection when not needed anymore
     * @see [Server-Sent Events](http://www.w3.org/TR/eventsource/)
     *
     * @see [Response Format documentation](https://www.stellar.org/developers/horizon/learn/responses.html)
     */
    override fun stream(listener: EventListener<AccountResponse>): ServerSentEvent {
        return StreamHandler(object : TypeToken<AccountResponse>() {})
            .handleStream(buildUri(), listener)
    }

    override fun cursor(cursor: String?): AccountsRequestBuilder {
        super.cursor(cursor)
        return this
    }

    override fun limit(number: Int): AccountsRequestBuilder {
        super.limit(number)
        return this
    }

    override fun order(direction: Order): AccountsRequestBuilder {
        super.order(direction)
        return this
    }

    /**
     * Build and execute request. **Warning!** [AccountResponse]s in [Page] will contain only `keypair` field.
     *
     * @return [Page] of [AccountResponse]
     * @throws TooManyRequestsException when too many requests were sent to the Horizon server.
     * @throws IOException
     */
    @Throws(IOException::class, TooManyRequestsException::class)
    fun execute(): Page<AccountResponse>? {
        return super.execute(buildUri())
    }
}
