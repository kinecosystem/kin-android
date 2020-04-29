package org.kin.stellarfork.requests

import com.google.gson.reflect.TypeToken
import com.here.oksse.ServerSentEvent
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import java.io.IOException
import java.net.URI
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections

interface StreamingProtocol<ListenerType> {
    fun stream(listener: EventListener<ListenerType>): ServerSentEvent
}


/**
 * Abstract class for request builders.
 */
abstract class RequestBuilder internal constructor(
    val httpClient: OkHttpClient,
    serverURI: URI,
    vararg defaultSegments: String?
) {
    /**
     * Requests specific `uri` and returns a Response of [T].
     * This method is helpful for getting the next set of results when T is a Page<X> where X is a Response type
     *
     * @return Response of [T]
     * @throws TooManyRequestsException when too many requests were sent to the Horizon server.
     * @throws IOException
     */
    @Throws(IOException::class, TooManyRequestsException::class)
    inline fun <reified T> execute(uri: URI): T? {
        return ResponseHandler(
            httpClient,
            object : TypeToken<T>() {}
        ).handleGetRequest(uri)
    }

    @Throws(IOException::class, TooManyRequestsException::class)
    inline fun <reified T> executePost(uri: URI, requestBody: RequestBody): T? {
        return ResponseHandler(
            httpClient,
            object : TypeToken<T>() {}
        ).handlePostRequest(uri, requestBody)
    }

    protected val uriBuilder: HttpUrl.Builder = HttpUrl.parse(serverURI.toString())!!.newBuilder()
    private val segments: ArrayList<String> = ArrayList()
    private var segmentsAdded = false

    init {
        segments.addAll(Arrays.asList<String>(*defaultSegments))
    }

    protected fun setSegments(vararg segments: String?): RequestBuilder {
        if (segmentsAdded) {
            throw RuntimeException("URL segments have been already added.")
        }
        segmentsAdded = true
        // Remove default segments
        this.segments.clear()
        Collections.addAll<String>(this.segments, *segments)
        return this
    }

    /**
     * Sets `cursor` parameter on the request.
     * A cursor is a value that points to a specific location in a collection of resources.
     * The cursor attribute itself is an opaque value meaning that users should not try to parse it.
     *
     * @param cursor
     * @see [Page documentation](https://www.stellar.org/developers/horizon/reference/resources/page.html)
     */
    open fun cursor(cursor: String?): RequestBuilder {
        uriBuilder.addQueryParameter("cursor", cursor)
        return this
    }

    /**
     * Sets `limit` parameter on the request.
     * It defines maximum number of records to return.
     * For range and default values check documentation of the endpoint requested.
     *
     * @param number maxium number of records to return
     */
    open fun limit(number: Int): RequestBuilder {
        uriBuilder.addQueryParameter("limit", number.toString())
        return this
    }

    /**
     * Sets `order` parameter on the request.
     *
     * @param direction [org.kin.stellarfork.requests.RequestBuilder.Order]
     */
    open fun order(direction: Order): RequestBuilder {
        uriBuilder.addQueryParameter("order", direction.value)
        return this
    }

    fun buildUri(): URI {
        for (segment in segments) {
            uriBuilder.addPathSegment(segment)
        }
        segments.clear()
        return URI.create(uriBuilder.build().toString())
    }

    /**
     * Represents possible `order` parameter values.
     */
    enum class Order(val value: String) {

        ASC("asc"), DESC("desc");

    }
}
