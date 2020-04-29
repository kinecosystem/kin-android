package org.kin.stellarfork

import org.kin.stellarfork.xdr.TrustLineEntry
import org.kin.stellarfork.xdr.Uint32

class TrustLineLedgerEntryChange internal constructor() : LedgerEntryChange() {
    var account: KeyPair? = null
        private set
    var asset: Asset? = null
        private set
    var balance: String? = null
        private set
    var limit: String? = null
        private set
    private val flags: Uint32? = null

    companion object {
        fun fromXdr(xdr: TrustLineEntry): TrustLineLedgerEntryChange {
            val entry = TrustLineLedgerEntryChange()
            entry.account =
                KeyPair.fromXdrPublicKey(xdr.accountID!!.accountID!!)
            entry.asset = Asset.fromXdr(xdr.asset!!)
            entry.balance = Operation.fromXdrAmount(xdr.balance!!.int64!!)
            entry.limit = Operation.fromXdrAmount(xdr.limit!!.int64!!)
            return entry
        }
    }
}
