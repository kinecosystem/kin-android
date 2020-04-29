package org.kin.stellarfork

import org.kin.stellarfork.codec.DecoderException
import org.kin.stellarfork.xdr.MemoType

/**
 *
 * The memo contains optional extra information. It is the responsibility of the client to interpret this value. Memos can be one of the following types:
 *
 *  * `MEMO_NONE`: Empty memo.
 *  * `MEMO_TEXT`: A string up to 28-bytes long.
 *  * `MEMO_ID`: A 64 bit unsigned integer.
 *  * `MEMO_HASH`: A 32 byte hash.
 *  * `MEMO_RETURN`: A 32 byte hash intended to be interpreted as the hash of the transaction the sender is refunding.
 *
 *
 * Use static methods to generate any of above types.
 *
 * @see Transaction
 */
abstract class Memo {
    abstract fun toXdr(): org.kin.stellarfork.xdr.Memo?
    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int

    companion object {
        /**
         * Creates new MemoNone instance.
         */
        @JvmStatic
        fun none(): MemoNone = MemoNone()

        /**
         * Creates new [MemoText] instance.
         *
         * @param text
         */
        @JvmStatic
        fun text(text: String): MemoText = MemoText(text)

        /**
         * Creates new [MemoId] instance.
         *
         * @param id
         */
        @JvmStatic
        fun id(id: Long): MemoId = MemoId(id)

        /**
         * Creates new [MemoHash] instance from byte array.
         *
         * @param bytes
         */
        @JvmStatic
        fun hash(bytes: ByteArray): MemoHash = MemoHash(bytes)

        /**
         * Creates new [MemoHash] instance from hex-encoded string
         *
         * @param hexString
         * @throws DecoderException
         */
        @JvmStatic
        @Throws(DecoderException::class)
        fun hash(hexString: String): MemoHash = MemoHash(hexString)

        /**
         * Creates new [MemoReturnHash] instance from byte array.
         *
         * @param bytes
         */
        @JvmStatic
        fun returnHash(bytes: ByteArray): MemoReturnHash = MemoReturnHash(bytes)

        /**
         * Creates new [MemoReturnHash] instance from hex-encoded string.
         *
         * @param hexString
         */
        @JvmStatic
        @Throws(DecoderException::class)
        fun returnHash(hexString: String): MemoReturnHash = MemoReturnHash(hexString)


        @JvmStatic
        fun fromXdr(memo: org.kin.stellarfork.xdr.Memo): Memo {
            return when (memo.discriminant) {
                MemoType.MEMO_NONE -> none()
                MemoType.MEMO_ID -> id(memo.id!!.uint64!!)
                MemoType.MEMO_TEXT -> text(memo.text!!)
                MemoType.MEMO_HASH -> hash(memo.hash!!.hash!!)
                MemoType.MEMO_RETURN -> returnHash(memo.retHash!!.hash!!)
                else -> throw RuntimeException("Unknown memo type")
            }
        }
    }
}
