package org.kin.stellarfork

import okhttp3.OkHttpClient
import org.kin.stellarfork.requests.AccountsRequestBuilder
import org.kin.stellarfork.requests.LedgersRequestBuilder
import org.kin.stellarfork.requests.TransactionsRequestBuilder
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL

/**
 * Main class used to connect to Horizon server.
 */
class Server(
    uri: String,
    val httpClient: OkHttpClient
) : KinServer {
    private fun createUri(uri: String): URI {
        return try {
            URL(uri).toURI()
        } catch (e: MalformedURLException) {
            throw RuntimeException(e)
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
    }

    private val serverURI = createUri(uri)

    override fun accounts(): AccountsRequestBuilder =
        AccountsRequestBuilder(httpClient, serverURI)

    override fun ledgers(): LedgersRequestBuilder =
        LedgersRequestBuilder(httpClient, serverURI)

    override fun transactions(): TransactionsRequestBuilder =
        TransactionsRequestBuilder(httpClient, serverURI)
}
