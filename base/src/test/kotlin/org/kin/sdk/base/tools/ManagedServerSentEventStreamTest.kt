package org.kin.sdk.base.tools

import com.google.gson.reflect.TypeToken
import com.here.oksse.ServerSentEvent
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test
import org.kin.stellarfork.requests.EventListener
import org.kin.stellarfork.requests.RequestBuilder
import org.kin.stellarfork.requests.StreamHandler
import org.kin.stellarfork.requests.StreamingProtocol
import java.net.URI
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ManagedServerSentEventStreamTest {

    private data class IntResponse(val value: Int)

    private class IntRequestBuilder(
        url: URI
    ) : RequestBuilder(OkHttpClient(), url), StreamingProtocol<IntResponse> {
        override fun stream(listener: EventListener<IntResponse>): ServerSentEvent {
            return StreamHandler(object : TypeToken<IntResponse>() {})
                .handleStream(
                    buildUri(),
                    listener
                )
        }
    }

    private lateinit var sut: ManagedServerSentEventStream<IntResponse>

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        sut = ManagedServerSentEventStream(IntRequestBuilder(mockWebServer.url("/").url().toURI()))
    }

    @Test
    fun test_recieving_events_and_lifecycle() {
        var listener: EventListener<IntResponse>? = null
        latchOperationValueCapture<IntResponse>(times = 2) { capture ->
            listener = object : EventListener<IntResponse> {
                override fun onEvent(data: IntResponse) {
                    capture(data)
                }
            }

            sut.addListener(listener!!)

            mockWebServer.enqueue(
                MockResponse()
                    .setBody(
                        "id: data\n\n" +
                                "data: " + "{ \"value\": 1 }" + "\n\n" +
                                "id: data\n\n" +
                                "data: " + "{ \"value\": 2 }" + "\n\n"
                    )
                    .setResponseCode(200)
                    .addHeader("Accept-Encoding", "")
                    .addHeader("Accept", "text/event-stream")
                    .addHeader("Cache-Control", "no-cache")
            )
        }.test {
            assertEquals(values, listOf(IntResponse(1), IntResponse(2)))
            assertEquals(sut.listeners.size, 1)
            assertTrue(sut.listeners.contains(listener))
            assertTrue(sut.hasConnection())
        }

        sut.removeListener(listener!!)

        assertEquals(sut.listeners.size, 0)
        assertFalse(sut.listeners.contains(listener))
        assertFalse(sut.hasConnection())
    }

    @Test
    fun test_add_two_listeners() {
        var listener: EventListener<IntResponse>? = null
        var listener2: EventListener<IntResponse>? = null
        latchOperationValueCapture<IntResponse>(times = 2) { capture ->
            listener = object : EventListener<IntResponse> {
                override fun onEvent(data: IntResponse) {
                    capture(data)
                }
            }
            listener2 = object : EventListener<IntResponse> {
                override fun onEvent(data: IntResponse) {
                    capture(data)
                }
            }

            sut.addListener(listener!!)

            mockWebServer.enqueue(
                MockResponse()
                    .setBody(
                        "id: data\n\n" +
                                "data: " + "{ \"value\": 1 }" + "\n\n"

                    )
                    .setResponseCode(200)
                    .addHeader("Accept-Encoding", "")
                    .addHeader("Accept", "text/event-stream")
                    .addHeader("Cache-Control", "no-cache")
            )

            sut.addListener(listener2!!)

        }.test {
            assertEquals(value, IntResponse(1))
            assertEquals(2, sut.listeners.size)
            assertTrue(sut.listeners.contains(listener))
            assertTrue(sut.hasConnection())
        }

        sut.removeListener(listener!!)
        sut.removeListener(listener2!!)

        assertEquals(0, sut.listeners.size)
        assertFalse(sut.listeners.contains(listener))
        assertFalse(sut.hasConnection())
    }

    @Test
    fun test_double_remove_listener() {
        val listener = object : EventListener<IntResponse> {
            override fun onEvent(data: IntResponse) {

            }
        }

        sut.addListener(listener)

        sut.removeListener(listener)
        sut.removeListener(listener)

        assertEquals(sut.listeners.size, 0)
        assertFalse(sut.listeners.contains(listener))
        assertFalse(sut.hasConnection())

    }

}
