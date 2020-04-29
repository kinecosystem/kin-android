package org.kin.sdk.base.models

data class KinBalance @JvmOverloads constructor(
    val amount: KinAmount = KinAmount.ZERO,
    val pendingAmount: KinAmount = amount
)


