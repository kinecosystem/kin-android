package kin.sdk.internal

import kin.sdk.Balance
import org.kin.sdk.base.models.KinAmount
import java.math.BigDecimal

internal data class BalanceImpl(private val kinAmount: KinAmount) : Balance {
    override fun value(): BigDecimal = kinAmount.value
    override fun value(precision: Int): String = kinAmount.toString(precision)
}
