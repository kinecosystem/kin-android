package org.kin.sdk.base.tools

import com.here.oksse.ServerSentEvent
import org.kin.stellarfork.requests.EventListener
import org.kin.stellarfork.requests.StreamingProtocol
import java.util.concurrent.CopyOnWriteArrayList

class ManagedServerSentEventStream<ResponseType>(private val requestBuilder: StreamingProtocol<ResponseType>) {

    private val lock = Any()
    val listeners = CopyOnWriteArrayList<EventListener<ResponseType>>()
    private var connection: ServerSentEvent? = null
    private var lastReceivedResponse: ResponseType? = null
    private val listener = ResponseTypeEventListener()

    fun addListener(listener: EventListener<ResponseType>): ManagedServerSentEventStream<ResponseType> {
        listeners.add(listener)
        lastReceivedResponse?.let { listener.onEvent(it) }
        connectIfNecessary()
        return this
    }

    fun removeListener(listener: EventListener<ResponseType>): ManagedServerSentEventStream<ResponseType> {
        listeners.remove(listener)
        closeIfNecessary()
        return this
    }

    fun hasConnection(): Boolean {
        return connection != null
    }

    /**
     *  Starts a connection if not already;
     *  criteria for connection is at least one active listener.
     */
    private fun connectIfNecessary() {
        synchronized(lock) {
            if (connection == null && listeners.isNotEmpty()) {
                connection = requestBuilder.stream(listener)
            }
        }
    }

    private fun closeIfNecessary() {
        synchronized(lock) {
            connection?.let {
                if (listeners.isEmpty()) {
                    it.close()
                    connection = null
                }
            }
        }
    }

    private inner class ResponseTypeEventListener : EventListener<ResponseType> {
        override fun onEvent(data: ResponseType) {
            synchronized(lock) {
                lastReceivedResponse = data
            }
            listeners.indices
                .asSequence()
                .map { listeners[it] }
                .forEach { it.onEvent(data) }
        }
    }
}


