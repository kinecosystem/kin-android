package org.kin.sdk.base.tools.sha224

interface ExtendedDigest : Digest {
    /**
     * Return the size in bytes of the internal buffer the digest applies it's compression
     * function to.
     *
     * @return byte length of the digests internal buffer.
     */
    val byteLength: Int
}
