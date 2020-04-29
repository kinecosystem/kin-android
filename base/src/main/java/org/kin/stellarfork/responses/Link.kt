package org.kin.stellarfork.responses

import com.google.gson.annotations.SerializedName
import java.net.URI
import java.net.URISyntaxException

/**
 * Represents links in responses.
 */
data class Link internal constructor(
    @field:SerializedName("href") val href: String,
    @field:SerializedName("templated") val isTemplated: Boolean
) {
    val uri: URI
        get() =
            try {
                URI(href)
            } catch (e: URISyntaxException) {
                throw RuntimeException(e)
            }
}
