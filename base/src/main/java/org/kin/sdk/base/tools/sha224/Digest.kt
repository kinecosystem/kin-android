package org.kin.sdk.base.tools.sha224

/**
 * interface that a message digest conforms to.
 */
interface Digest {
    /**
     * return the algorithm name
     *
     * @return the algorithm name
     */
    val algorithmName: String?

    /**
     * return the size, in bytes, of the digest produced by this message digest.
     *
     * @return the size, in bytes, of the digest produced by this message digest.
     */
    val digestSize: Int

    /**
     * update the message digest with a single byte.
     *
     * @param in the input byte to be entered.
     */
    fun update(inBytes: Byte)

    /**
     * update the message digest with a block of bytes.
     *
     * @param in the byte array containing the data.
     * @param inOff the offset into the byte array where the data starts.
     * @param len the length of the data.
     */
    fun update(inBytes: ByteArray, inOff: Int, len: Int)

    /**
     * close the digest, producing the final digest value. The doFinal
     * call leaves the digest reset.
     *
     * @param out the array the digest is to be copied into.
     * @param outOff the offset into the out array the digest is to start at.
     */
    fun doFinal(out: ByteArray, outOff: Int): Int

    /**
     * reset the digest back to it's initial state.
     */
    fun reset()
}
