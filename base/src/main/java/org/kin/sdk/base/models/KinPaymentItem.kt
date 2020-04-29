package org.kin.sdk.base.models

data class KinPaymentItem(val amount: KinAmount, val destinationAccount: KinAccount.Id)
