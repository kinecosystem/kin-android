package org.kin.sdk.base.tools.sha224

import org.kin.sdk.base.tools.sha224.Pack.bigEndianToInt
import org.kin.sdk.base.tools.sha224.Pack.intToBigEndian

/**
 * SHA-224 as described in RFC 3874
 * <pre>
 * block  word  digest
 * SHA-1   512    32    160
 * SHA-224 512    32    224
 * SHA-256 512    32    256
 * SHA-384 1024   64    384
 * SHA-512 1024   64    512
</pre> *
 */
class SHA224Digest : GeneralDigest, EncodableDigest {
    private var H1 = 0
    private var H2 = 0
    private var H3 = 0
    private var H4 = 0
    private var H5 = 0
    private var H6 = 0
    private var H7 = 0
    private var H8 = 0
    private val X = IntArray(64)
    private var xOff = 0

    /**
     * Standard constructor
     */
    constructor() {
        reset()
    }

    /**
     * Copy constructor.  This will copy the state of the provided
     * message digest.
     */
    constructor(t: SHA224Digest) : super(t) {
        doCopy(t)
    }

    private fun doCopy(t: SHA224Digest) {
        super.copyIn(t)
        H1 = t.H1
        H2 = t.H2
        H3 = t.H3
        H4 = t.H4
        H5 = t.H5
        H6 = t.H6
        H7 = t.H7
        H8 = t.H8
        System.arraycopy(t.X, 0, X, 0, t.X.size)
        xOff = t.xOff
    }

    /**
     * State constructor - create a digest initialised with the state of a previous one.
     *
     * @param encodedState the encoded state from the originating digest.
     */
    constructor(encodedState: ByteArray) : super(encodedState) {
        H1 = bigEndianToInt(encodedState, 16)
        H2 = bigEndianToInt(encodedState, 20)
        H3 = bigEndianToInt(encodedState, 24)
        H4 = bigEndianToInt(encodedState, 28)
        H5 = bigEndianToInt(encodedState, 32)
        H6 = bigEndianToInt(encodedState, 36)
        H7 = bigEndianToInt(encodedState, 40)
        H8 = bigEndianToInt(encodedState, 44)
        xOff = bigEndianToInt(encodedState, 48)
        for (i in 0 until xOff) {
            X[i] = bigEndianToInt(encodedState, 52 + i * 4)
        }
    }

    override val algorithmName: String = "SHA-224"

    override val digestSize: Int = DIGEST_LENGTH

    override fun processWord(inBytes: ByteArray, inOff: Int) {
        // Note: Inlined for performance
//        X[xOff] = Pack.bigEndianToInt(in, inOff);
        var inOffLocal = inOff
        var n: Int = inBytes[inOffLocal].toInt() shl 24
        n = n or (inBytes[++inOffLocal].toInt() and 0xff shl 16)
        n = n or (inBytes[++inOffLocal].toInt() and 0xff shl 8)
        n = n or (inBytes[++inOffLocal].toInt() and 0xff)
        X[xOff] = n
        if (++xOff == 16) {
            processBlock()
        }
    }

    override fun processLength(bitLength: Long) {
        if (xOff > 14) {
            processBlock()
        }
        X[14] = (bitLength ushr 32).toInt()
        X[15] = (bitLength and -0x1).toInt()
    }

    override fun doFinal(out: ByteArray, outOff: Int): Int {
        finish()
        intToBigEndian(H1, out, outOff)
        intToBigEndian(H2, out, outOff + 4)
        intToBigEndian(H3, out, outOff + 8)
        intToBigEndian(H4, out, outOff + 12)
        intToBigEndian(H5, out, outOff + 16)
        intToBigEndian(H6, out, outOff + 20)
        intToBigEndian(H7, out, outOff + 24)
        reset()
        return this.digestSize
    }

    /**
     * reset the chaining variables
     */
    override fun reset() {
        super.reset()

        /* SHA-224 initial hash value
         */H1 = -0x3efa6128
        H2 = 0x367cd507
        H3 = 0x3070dd17
        H4 = -0x8f1a6c7
        H5 = -0x3ff4cf
        H6 = 0x68581511
        H7 = 0x64f98fa7
        H8 = -0x4105b05c
        xOff = 0
        for (i in X.indices) {
            X[i] = 0
        }
    }

    override fun processBlock() {
        //
        // expand 16 word block into 64 word blocks.
        //
        for (t in 16..63) {
            X[t] = Theta1(X[t - 2]) + X[t - 7] + Theta0(X[t - 15]) + X[t - 16]
        }

        //
        // set up working variables.
        //
        var a = H1
        var b = H2
        var c = H3
        var d = H4
        var e = H5
        var f = H6
        var g = H7
        var h = H8
        var t = 0
        for (i in 0..7) {
            // t = 8 * i
            h += Sum1(e) + Ch(e, f, g) + K[t] + X[t]
            d += h
            h += Sum0(a) + Maj(a, b, c)
            ++t

            // t = 8 * i + 1
            g += Sum1(d) + Ch(d, e, f) + K[t] + X[t]
            c += g
            g += Sum0(h) + Maj(h, a, b)
            ++t

            // t = 8 * i + 2
            f += Sum1(c) + Ch(c, d, e) + K[t] + X[t]
            b += f
            f += Sum0(g) + Maj(g, h, a)
            ++t

            // t = 8 * i + 3
            e += Sum1(b) + Ch(b, c, d) + K[t] + X[t]
            a += e
            e += Sum0(f) + Maj(f, g, h)
            ++t

            // t = 8 * i + 4
            d += Sum1(a) + Ch(a, b, c) + K[t] + X[t]
            h += d
            d += Sum0(e) + Maj(e, f, g)
            ++t

            // t = 8 * i + 5
            c += Sum1(h) + Ch(h, a, b) + K[t] + X[t]
            g += c
            c += Sum0(d) + Maj(d, e, f)
            ++t

            // t = 8 * i + 6
            b += Sum1(g) + Ch(g, h, a) + K[t] + X[t]
            f += b
            b += Sum0(c) + Maj(c, d, e)
            ++t

            // t = 8 * i + 7
            a += Sum1(f) + Ch(f, g, h) + K[t] + X[t]
            e += a
            a += Sum0(b) + Maj(b, c, d)
            ++t
        }
        H1 += a
        H2 += b
        H3 += c
        H4 += d
        H5 += e
        H6 += f
        H7 += g
        H8 += h

        //
        // reset the offset and clean out the word buffer.
        //
        xOff = 0
        for (i in 0..15) {
            X[i] = 0
        }
    }

    override fun copy(): Memoable {
        return SHA224Digest(this)
    }

    override fun reset(other: Memoable) {
        val d = other as SHA224Digest
        doCopy(d)
    }

    override val encodedState: ByteArray
        get() {
            val state = ByteArray(52 + xOff * 4)
            super.populateState(state)
            intToBigEndian(H1, state, 16)
            intToBigEndian(H2, state, 20)
            intToBigEndian(H3, state, 24)
            intToBigEndian(H4, state, 28)
            intToBigEndian(H5, state, 32)
            intToBigEndian(H6, state, 36)
            intToBigEndian(H7, state, 40)
            intToBigEndian(H8, state, 44)
            intToBigEndian(xOff, state, 48)
            for (i in 0 until xOff) {
                intToBigEndian(X[i], state, 52 + i * 4)
            }
            return state
        }

    companion object {
        const val DIGEST_LENGTH = 28

        /**
         * SHA-224 Constants
         * (represent the first 32 bits of the fractional parts of the
         * cube roots of the first sixty-four prime numbers)
         */
        val K = intArrayOf(
            0x428a2f98,
            0x71374491,
            -0x4a3f0431,
            -0x164a245b,
            0x3956c25b,
            0x59f111f1,
            -0x6dc07d5c,
            -0x54e3a12b,
            -0x27f85568,
            0x12835b01,
            0x243185be,
            0x550c7dc3,
            0x72be5d74,
            -0x7f214e02,
            -0x6423f959,
            -0x3e640e8c,
            -0x1b64963f,
            -0x1041b87a,
            0x0fc19dc6,
            0x240ca1cc,
            0x2de92c6f,
            0x4a7484aa,
            0x5cb0a9dc,
            0x76f988da,
            -0x67c1aeae,
            -0x57ce3993,
            -0x4ffcd838,
            -0x40a68039,
            -0x391ff40d,
            -0x2a586eb9,
            0x06ca6351,
            0x14292967,
            0x27b70a85,
            0x2e1b2138,
            0x4d2c6dfc,
            0x53380d13,
            0x650a7354,
            0x766a0abb,
            -0x7e3d36d2,
            -0x6d8dd37b,
            -0x5d40175f,
            -0x57e599b5,
            -0x3db47490,
            -0x3893ae5d,
            -0x2e6d17e7,
            -0x2966f9dc,
            -0xbf1ca7b,
            0x106aa070,
            0x19a4c116,
            0x1e376c08,
            0x2748774c,
            0x34b0bcb5,
            0x391c0cb3,
            0x4ed8aa4a,
            0x5b9cca4f,
            0x682e6ff3,
            0x748f82ee,
            0x78a5636f,
            -0x7b3787ec,
            -0x7338fdf8,
            -0x6f410006,
            -0x5baf9315,
            -0x41065c09,
            -0x398e870e
        )

        /* SHA-224 functions */
        private fun Ch(x: Int, y: Int, z: Int): Int = x and y xor (x.inv() and z)
        
        private fun Maj(x: Int, y: Int, z: Int): Int = x and y xor (x and z) xor (y and z)
        
        private fun Sum0(x: Int): Int =
            x ushr 2 or (x shl 30) xor (x ushr 13 or (x shl 19)) xor (x ushr 22 or (x shl 10))

        private fun Sum1(x: Int): Int =
            x ushr 6 or (x shl 26) xor (x ushr 11 or (x shl 21)) xor (x ushr 25 or (x shl 7))

        private fun Theta0(x: Int): Int =
            x ushr 7 or (x shl 25) xor (x ushr 18 or (x shl 14)) xor (x ushr 3)

        private fun Theta1(x: Int): Int =
            x ushr 17 or (x shl 15) xor (x ushr 19 or (x shl 13)) xor (x ushr 10)
    }
}
