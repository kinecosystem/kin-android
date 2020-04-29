package org.kin.stellarfork

import org.kin.stellarfork.xdr.LedgerEntry
import org.kin.stellarfork.xdr.LedgerEntryType

open class LedgerEntryChange constructor() {
    var lastModifiedLedgerSequence: Long? = null
        private set

    companion object {
        @JvmStatic
        fun fromXdr(xdr: LedgerEntry): LedgerEntryChange? {
            var entryChange: LedgerEntryChange? = null
            when (xdr.data!!.discriminant) {
                LedgerEntryType.ACCOUNT -> {
                    entryChange = AccountLedgerEntryChange.fromXdr(xdr.data!!.account!!)
                    entryChange.lastModifiedLedgerSequence =
                        xdr.lastModifiedLedgerSeq!!.uint32!!.toLong()
                }
                LedgerEntryType.TRUSTLINE -> {
                    entryChange = TrustLineLedgerEntryChange.fromXdr(xdr.data!!.trustLine!!)
                    entryChange.lastModifiedLedgerSequence =
                        xdr.lastModifiedLedgerSeq!!.uint32!!.toLong()
                }
                LedgerEntryType.OFFER -> {
                }
                LedgerEntryType.DATA -> {
                }
                else -> {
                }
            }
            return entryChange
        }
    }
}
