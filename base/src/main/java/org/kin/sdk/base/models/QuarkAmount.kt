package org.kin.sdk.base.models

import java.math.BigDecimal

data class QuarkAmount(val value: Long)


internal const val QUARK_CONVERSION_RATE = 100000

fun KinAmount.toQuarks(): QuarkAmount =
    QuarkAmount(value.multiply(BigDecimal(QUARK_CONVERSION_RATE)).toLong())

fun QuarkAmount.toKin(): KinAmount =
    KinAmount(BigDecimal(value).divide(BigDecimal(QUARK_CONVERSION_RATE)))
