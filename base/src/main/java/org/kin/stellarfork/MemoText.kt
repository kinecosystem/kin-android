package org.kin.stellarfork

import org.kin.stellarfork.Util.CHARSET_UTF8
import org.kin.stellarfork.xdr.MemoType
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

/**
 * Represents MEMO_TEXT.
 */
data class MemoText(val text: String) : Memo() {

    init {
        var length = 0
        try {
            length = text.toByteArray(Charset.forName(CHARSET_UTF8)).size
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        if (length > 28) {
            throw MemoTooLongException("text must be <= 28 bytes. length=$length")
        }
    }

    override fun toXdr(): org.kin.stellarfork.xdr.Memo? {
        val memo = org.kin.stellarfork.xdr.Memo()
        memo.discriminant = MemoType.MEMO_TEXT
        memo.text = text
        return memo
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val memoText = other as MemoText?
        return if (memoText != null) {
            text == memoText.text
        } else false
    }

    override fun hashCode(): Int {
        return 31 + text.hashCode()
    }
}
