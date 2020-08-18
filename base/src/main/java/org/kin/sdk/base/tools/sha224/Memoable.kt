package org.kin.sdk.base.tools.sha224

/**
 * Interface for Memoable objects. Memoable objects allow the taking of a snapshot of their internal state
 * via the copy() method and then reseting the object back to that state later using the reset() method.
 */
interface Memoable {
    /**
     * Produce a copy of this object with its configuration and in its current state.
     *
     *
     * The returned object may be used simply to store the state, or may be used as a similar object
     * starting from the copied state.
     */
    fun copy(): Memoable

    /**
     * Restore a copied object state into this object.
     *
     *
     * Implementations of this method *should* try to avoid or minimise memory allocation to perform the reset.
     *
     * @param other an object originally [copied][.copy] from an object of the same type as this instance.
     * @throws ClassCastException if the provided object is not of the correct type.
     * @throws MemoableResetException if the **other** parameter is in some other way invalid.
     */
    fun reset(other: Memoable)
}
