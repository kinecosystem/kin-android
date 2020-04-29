package org.kin.sdk.base.stellar.models

import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount

sealed class KinOperation {
    data class Payment internal constructor(
        val amount: KinAmount,
        val source: KinAccount.Id,
        val destination: KinAccount.Id
    ) : KinOperation()
}
