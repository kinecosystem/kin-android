package kin.sdk

/**
 * Represents a listener to events, that can be removed using [.remove].
 */
class ListenerRegistration internal constructor(private val removeListener: () -> Unit) {
    /**
     * Remove and unregisters this listener.
     */
    fun remove() {
        removeListener()
    }
}
