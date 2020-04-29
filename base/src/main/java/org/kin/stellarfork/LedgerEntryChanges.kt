package org.kin.stellarfork

import org.kin.stellarfork.LedgerEntryChange.Companion.fromXdr
import org.kin.stellarfork.xdr.LedgerEntryChangeType
import java.util.ArrayList

class LedgerEntryChanges {
    lateinit var ledgerEntryUpdates: Array<LedgerEntryChange>
        private set
    lateinit var ledgerEntryStates: Array<LedgerEntryChange>
        private set

    companion object {
        fun fromXdr(xdr: org.kin.stellarfork.xdr.LedgerEntryChanges): LedgerEntryChanges {
            val ledgerEntryChanges = LedgerEntryChanges()
            val updates: MutableList<LedgerEntryChange> = ArrayList()
            val states: MutableList<LedgerEntryChange> = ArrayList()
            for (ledgerEntryChange in xdr.ledgerEntryChanges) {
                when (ledgerEntryChange!!.discriminant) {
                    LedgerEntryChangeType.LEDGER_ENTRY_CREATED -> {
                    }
                    LedgerEntryChangeType.LEDGER_ENTRY_UPDATED -> {
                        val entryChange = fromXdr(ledgerEntryChange.updated!!)
                        if (entryChange != null) {
                            updates.add(entryChange)
                        }
                    }
                    LedgerEntryChangeType.LEDGER_ENTRY_REMOVED -> {
                    }
                    LedgerEntryChangeType.LEDGER_ENTRY_STATE -> {
                        val stateChange = fromXdr(ledgerEntryChange.state!!)
                        if (stateChange != null) {
                            states.add(stateChange)
                        }
                    }
                    else -> {
                    }
                }
            }
            ledgerEntryChanges.ledgerEntryUpdates = updates.toTypedArray()
            ledgerEntryChanges.ledgerEntryStates = states.toTypedArray()
            return ledgerEntryChanges
        }
    }
}
