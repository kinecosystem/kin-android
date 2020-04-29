package org.kin.stellarfork

import org.kin.stellarfork.xdr.Hash
import org.kin.stellarfork.xdr.Memo
import org.kin.stellarfork.xdr.MemoType

/**
 * Represents MEMO_HASH.
 */
class MemoHash : MemoHashAbstract {
    constructor(bytes: ByteArray) : super(bytes)
    constructor(hexString: String) : super(hexString)

    override fun toXdr(): Memo {
        return Memo().apply {
            discriminant = MemoType.MEMO_HASH
            hash = Hash().apply { hash = bytes }
        }
    }
}
