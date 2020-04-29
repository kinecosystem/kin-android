package org.kin.stellarfork

import org.kin.stellarfork.xdr.AccountEntry

data class AccountLedgerEntryChange private constructor(
    var account: KeyPair,
    var balance: String
) : LedgerEntryChange() {
    companion object {
        @JvmStatic
        fun fromXdr(xdr: AccountEntry): AccountLedgerEntryChange {
            return AccountLedgerEntryChange(
                KeyPair.fromXdrPublicKey(xdr.accountID!!.accountID!!),
                Operation.fromXdrAmount(xdr.balance!!.int64!!)
            )
        }
    }
}
