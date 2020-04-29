package kin.sdk

import java.math.BigDecimal

interface Balance {
    /**
     * @return BigDecimal the balance value
     */
    fun value(): BigDecimal

    /**
     * @param precision the number of decimals points
     * @return String the balance value as a string with specified precision
     */
    fun value(precision: Int): String
}
