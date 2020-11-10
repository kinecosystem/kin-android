package org.kin.sdk.base.network.api

import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.tools.Observer

interface KinStreamingApiV4 {
    fun streamAccount(kinAccountId: KinAccount.Id): Observer<KinAccount>
    fun streamNewTransactions(kinAccountId: KinAccount.Id): Observer<KinTransaction>
}
