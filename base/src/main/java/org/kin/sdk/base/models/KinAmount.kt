package org.kin.sdk.base.models

import java.lang.Math.min
import java.math.BigDecimal
import java.math.BigInteger

data class KinAmount(private val amount: BigDecimal) {

    companion object {
        private const val MAX_PRECISION = 5

        @JvmField
        val ZERO: KinAmount = KinAmount(BigDecimal.ZERO)

        @JvmField
        val ONE: KinAmount = KinAmount(BigDecimal.ONE)

        fun max(amount: KinAmount, otherAmount: KinAmount): KinAmount {
            return KinAmount(amount.value.max(otherAmount.value))
        }
    }

    val value: BigDecimal = amount.setScale(MAX_PRECISION, BigDecimal.ROUND_HALF_UP)

    constructor(value: String) : this(BigDecimal(value))
    constructor(value: BigInteger) : this(BigDecimal(value))
    constructor(value: Long) : this(BigDecimal(value))
    constructor(value: Double) : this(BigDecimal(value))

    override fun toString(): String {
        return toString(MAX_PRECISION)
    }

    fun toString(precision: Int): String {
        return value.setScale(min(MAX_PRECISION, precision), BigDecimal.ROUND_HALF_UP).toString()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is KinAmount) {
            return false
        }
        return other.value == value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    operator fun plus(amount: KinAmount): KinAmount {
        return KinAmount(this.amount + amount.amount)
    }

    operator fun minus(amount: KinAmount): KinAmount {
        return KinAmount(this.amount - amount.amount)
    }

    fun multiply(amount: KinAmount): KinAmount {
        return KinAmount(this.amount.multiply(amount.value))
    }

    fun divide(amount: KinAmount): KinAmount {
        return KinAmount(this.amount.divide(amount.value))
    }
}

