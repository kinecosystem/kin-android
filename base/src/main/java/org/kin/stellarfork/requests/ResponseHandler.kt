package org.kin.stellarfork.requests

import com.google.gson.reflect.TypeToken
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.kin.stellarfork.responses.ClientProtocolException
import org.kin.stellarfork.responses.GsonSingleton
import org.kin.stellarfork.responses.HttpResponseException
import org.kin.stellarfork.responses.ServerGoneException
import java.io.IOException
import java.net.URI

class ResponseHandler<T>
/**
 * "Generics on a type are typically erased at runtime, except when the type is compiled with the generic parameter
 * bound. In that case, the compiler inserts the generic type information into the compiled class. In other cases,
 * that is not possible." More info: http://stackoverflow.com/a/14506181
 */(
    private val httpClient: OkHttpClient,
    private val type: TypeToken<T>
) {
    companion object {
        private const val TEMPORARY_REDIRECT = 307
        private const val LOCATION_HEADER = "Location"
    }

    private fun newGetRequest(uri: URI): Request {
        return Request.Builder()
            .url(uri.toString())
            .build()
    }

    private fun newPostRequest(uri: URI, body: RequestBody): Request {
        return Request.Builder()
            .url(uri.toString())
            .post(body)
            .build()
    }

    @Throws(IOException::class, TooManyRequestsException::class)
    fun handleGetRequest(uri: URI): T? {
        return handleResponse(httpClient.newCall(newGetRequest(uri)).execute())
    }

    @Throws(IOException::class, TooManyRequestsException::class)
    fun handlePostRequest(uri: URI, body: RequestBody): T? {
        var response: Response? = null
        try {
            response = httpClient.newCall(newPostRequest(uri, body)).execute()
            if (response != null) {
                val location = response.header(LOCATION_HEADER)
                return if (response.code() == TEMPORARY_REDIRECT && location != null) {
                    handlePostRequest(modifyURI(uri, location), body)
                } else {
                    handleResponse(response, true)
                }
            }
        } finally {
            response?.close()
        }
        return null
    }

    private fun modifyURI(uri: URI, location: String): URI {
        return HttpUrl.get(URI.create(location))!!
            .newBuilder()
            .addPathSegments(uri.path)
            .build()
            .uri()
    }

    @Throws(IOException::class, TooManyRequestsException::class)
    private fun handleResponse(response: Response?, shouldParseBodyIfError: Boolean = false): T? {
        return if (response == null) {
            null
        } else try { // Too Many Requests
            val responseBody = response.body()
            when {
                response.code() == 429 -> {
                    val retryAfterString = response.header("Retry-After")
                    if (retryAfterString != null) {
                        try {
                            val retryAfter = retryAfterString.toInt()
                            throw TooManyRequestsException(retryAfter)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    throw TooManyRequestsException(0)
                }
                response.code() == 410 -> {
                    throw ServerGoneException(response.code(), response.message())
                }
                responseBody != null && (response.code() == 200 || shouldParseBodyIfError) -> {
                    GsonSingleton.instance?.fromJson<T?>(
                        responseBody.string(),
                        type.type
                    ).apply {
                        (this as? org.kin.stellarfork.responses.Response)?.setHeaders(
                            response.header("X-Ratelimit-Limit"),
                            response.header("X-Ratelimit-Remaining"),
                            response.header("X-Ratelimit-Reset")
                        )
                    } ?: handleResponse(response, false)
                }
                // Other errors
                response.code() >= 300 -> {
                    var responseBodyString = try {
                        responseBody?.string()
                    } catch (t: Throwable) {
                        null
                    }
                    throw HttpResponseException(response.code(), response.message(), responseBodyString)
                }
                // No content
                else -> throw ClientProtocolException("Response contains no content")
            }
        } finally {
            response.close()
        }
    }
}
