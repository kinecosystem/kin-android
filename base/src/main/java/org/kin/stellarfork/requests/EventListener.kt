package org.kin.stellarfork.requests

/**
 * This interface is used in [RequestBuilder] classes `stream` method.
 */
interface EventListener<T> {
    /**
     * This method will be called when new event is sent by a server.
     *
     * @param data object deserialized from the event data
     */
    fun onEvent(data: T)
}
