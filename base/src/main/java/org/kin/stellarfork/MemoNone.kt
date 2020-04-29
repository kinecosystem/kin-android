package org.kin.stellarfork

import org.kin.stellarfork.xdr.MemoType

/**
 * Represents MEMO_NONE.
 */
class MemoNone : Memo() {
    override fun toXdr(): org.kin.stellarfork.xdr.Memo? {
        val memo = org.kin.stellarfork.xdr.Memo()
        memo.discriminant = MemoType.MEMO_NONE
        return memo
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return !(other == null || javaClass != other.javaClass)
    }

    override fun hashCode(): Int {
        return 0
    }
}
