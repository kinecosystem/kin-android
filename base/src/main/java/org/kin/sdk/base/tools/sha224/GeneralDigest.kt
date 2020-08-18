package org.kin.sdk.base.tools.sha224

import org.kin.sdk.base.tools.sha224.Pack.bigEndianToInt
import org.kin.sdk.base.tools.sha224.Pack.bigEndianToLong
import org.kin.sdk.base.tools.sha224.Pack.intToBigEndian
import org.kin.sdk.base.tools.sha224.Pack.longToBigEndian

/**
 * base implementation of MD4 family style digest as outlined in
 * "Handbook of Applied Cryptography", pages 344 - 347.
 */
abstract class GeneralDigest : ExtendedDigest, Memoable {
    private val xBuf = ByteArray(4)
    private var xBufOff = 0
    private var byteCount: Long = 0

    /**
     * Standard constructor
     */
    protected constructor() {
        xBufOff = 0
    }

    /**
     * Copy constructor.  We are using copy constructors in place
     * of the Object.clone() interface as this interface is not
     * supported by J2ME.
     */
    protected constructor(t: GeneralDigest) {
        copyIn(t)
    }

    protected constructor(encodedState: ByteArray) {
        System.arraycopy(encodedState, 0, xBuf, 0, xBuf.size)
        xBufOff = bigEndianToInt(encodedState, 4)
        byteCount = bigEndianToLong(encodedState, 8)
    }

    protected fun copyIn(t: GeneralDigest) {
        System.arraycopy(t.xBuf, 0, xBuf, 0, t.xBuf.size)
        xBufOff = t.xBufOff
        byteCount = t.byteCount
    }

    override fun update(inBytes: Byte) {
        xBuf[xBufOff++] = inBytes
        if (xBufOff == xBuf.size) {
            processWord(xBuf, 0)
            xBufOff = 0
        }
        byteCount++
    }

    override fun update(
        inBytes: ByteArray,
        inOff: Int,
        len: Int
    ) {
        var lenLocal = len
        lenLocal = Math.max(0, lenLocal)

        //
        // fill the current word
        //
        var i = 0
        if (xBufOff != 0) {
            while (i < lenLocal) {
                xBuf[xBufOff++] = inBytes[inOff + i++]
                if (xBufOff == 4) {
                    processWord(xBuf, 0)
                    xBufOff = 0
                    break
                }
            }
        }

        //
        // process whole words.
        //
        val limit = (lenLocal - i and 3.inv()) + i
        while (i < limit) {
            processWord(inBytes, inOff + i)
            i += 4
        }

        //
        // load in the remainder.
        //
        while (i < lenLocal) {
            xBuf[xBufOff++] = inBytes[inOff + i++]
        }
        byteCount += lenLocal.toLong()
    }

    fun finish() {
        val bitLength = byteCount shl 3

        //
        // add the pad bytes.
        //
        update(128.toByte())
        while (xBufOff != 0) {
            update(0.toByte())
        }
        processLength(bitLength)
        processBlock()
    }

    override fun reset() {
        byteCount = 0
        xBufOff = 0
        for (i in xBuf.indices) {
            xBuf[i] = 0
        }
    }

    protected fun populateState(state: ByteArray) {
        System.arraycopy(xBuf, 0, state, 0, xBufOff)
        intToBigEndian(xBufOff, state, 4)
        longToBigEndian(byteCount, state, 8)
    }

    override val byteLength = 64
    protected abstract fun processWord(inBytes: ByteArray, inOff: Int)
    protected abstract fun processLength(bitLength: Long)
    protected abstract fun processBlock()
}
