package org.kin.stellarfork.responses

import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import org.kin.stellarfork.requests.ResponseHandler
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.util.ArrayList

/**
 * Represents page of objects.
 *
 * @see [Page documentation](https://www.stellar.org/developers/horizon/reference/resources/page.html)
 */
data class Page<T> @JvmOverloads internal constructor(
    @SerializedName("records")
    val records: ArrayList<T> = ArrayList(),
    @SerializedName("links")
    val links: Links? = null
) : Response() {
    /**
     * @param httpClient
     * @return The next page of results or null when there is no more results
     * @throws URISyntaxException
     * @throws IOException
     */
    @Throws(URISyntaxException::class, IOException::class)
    fun getNextPage(httpClient: OkHttpClient): Page<T>? {
        return if (links?.next == null) {
            null
        } else ResponseHandler(
            httpClient,
            object : TypeToken<Page<T>>() {}
        ).handleGetRequest(nextPageURI)
    }

    @get:Throws(URISyntaxException::class)
    val nextPageURI: URI
        get() = URI(links!!.next.href)

    /**
     * Links connected to page response.
     */
    data class Links internal constructor(
        @field:SerializedName("next") val next: Link,
        @field:SerializedName("prev") val prev: Link,
        @field:SerializedName("self") val self: Link
    )
}
