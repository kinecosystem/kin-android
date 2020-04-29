package org.kin.stellarfork.requests

import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.here.oksse.OkSse
import com.here.oksse.ServerSentEvent
import okhttp3.Request
import okhttp3.Response
import org.kin.stellarfork.responses.GsonSingleton
import java.net.URI

class StreamHandler<T>
    (
    /**
     * "Generics on a type are typically erased at runtime, except when the type is compiled with the
     * generic parameter bound. In that case, the compiler inserts the generic type information into
     * the compiled class. In other cases, that is not possible."
     * More info: http://stackoverflow.com/a/14506181
     *
     * @param type
     */
    private val type: TypeToken<T>
) {
    private val okSse = OkSse()

    fun handleStream(
        uri: URI,
        listener: EventListener<T>?
    ): ServerSentEvent {
        val request = Request.Builder()
            .url(uri.toString())
            .build()
        return okSse.newServerSentEvent(request, object : ServerSentEvent.Listener {
            override fun onOpen(
                sse: ServerSentEvent,
                response: Response
            ) {
                /* Do Nothing */
            }

            override fun onMessage(
                sse: ServerSentEvent,
                id: String?,
                event: String,
                data: String
            ) {
                if (OPEN_MESSAGE_DATA == data) {
                    return
                }
                try {
                    val obj: T? = GsonSingleton.instance?.fromJson(data, type.type)
                    if (obj != null) {
                        listener?.onEvent(obj)
                    }
                } catch (e: JsonParseException) {
                    /* swallow */
                }
            }

            override fun onComment(
                sse: ServerSentEvent,
                comment: String
            ) {
                /* Do Nothing */
            }

            override fun onRetryTime(
                sse: ServerSentEvent,
                milliseconds: Long
            ): Boolean = true

            override fun onRetryError(
                sse: ServerSentEvent,
                throwable: Throwable,
                response: Response?
            ): Boolean = true

            override fun onClosed(sse: ServerSentEvent) {}

            override fun onPreRetry(
                sse: ServerSentEvent,
                originalRequest: Request
            ): Request = originalRequest
        })
    }

    companion object {
        /**
         * Opening message contains "hello" string
         */
        private const val OPEN_MESSAGE_DATA = "\"hello\""
    }

}
