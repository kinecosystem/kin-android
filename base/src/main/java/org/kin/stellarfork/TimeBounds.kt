package org.kin.stellarfork

import org.kin.stellarfork.xdr.Uint64

/**
 *
 * TimeBounds represents the time interval that a transaction is valid.
 *
 * @see Transaction
 */
data class TimeBounds(
    val minTime: Long,
    val maxTime: Long
) {
    /**
     * @param minTime 64bit Unix timestamp
     * @param maxTime 64bit Unix timestamp
     */
    init {
        require(maxTime >= minTime) { "minTime must be >= maxTime" }
    }

    fun toXdr(): org.kin.stellarfork.xdr.TimeBounds {
        val timeBounds = org.kin.stellarfork.xdr.TimeBounds()
        val minTime = Uint64()
        val maxTime = Uint64()
        minTime.uint64 = this.minTime
        maxTime.uint64 = this.maxTime
        timeBounds.minTime = minTime
        timeBounds.maxTime = maxTime
        return timeBounds
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as TimeBounds
        return if (minTime != that.minTime) false else maxTime == that.maxTime
    }

    override fun hashCode(): Int {
        var result = minTime.hashCode()
        result = 31 * result + maxTime.hashCode()
        return result
    }

    companion object {
        @JvmStatic
        fun fromXdr(timeBounds: org.kin.stellarfork.xdr.TimeBounds?): TimeBounds? {
            return if (timeBounds == null) {
                null
            } else TimeBounds(
                timeBounds.minTime!!.uint64!!,
                timeBounds.maxTime!!.uint64!!
            )
        }
    }
}
