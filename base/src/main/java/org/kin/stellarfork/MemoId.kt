package org.kin.stellarfork

import org.kin.stellarfork.xdr.MemoType
import org.kin.stellarfork.xdr.Uint64

/**
 * Represents MEMO_ID.
 */
class MemoId(val id: Long) : Memo() {
    init {
        require(id >= 0) { "id must be a positive number" }
    }

    override fun toXdr(): org.kin.stellarfork.xdr.Memo? {
        val memo = org.kin.stellarfork.xdr.Memo()
        memo.discriminant = MemoType.MEMO_ID
        val idXdr = Uint64()
        idXdr.uint64 = id
        memo.id = idXdr
        return memo
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val memoId = other as MemoId
        return id == memoId.id
    }

    override fun hashCode(): Int {
        return 31 + id.hashCode()
    }
}
