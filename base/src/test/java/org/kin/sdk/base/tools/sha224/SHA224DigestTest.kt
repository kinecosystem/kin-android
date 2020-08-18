package org.kin.sdk.base.tools.sha224


/**
 * standard vector test for SHA-224 from RFC 3874 - only the last three are in
 * the RFC.
 */
class SHA224DigestTest : DigestTest(
    SHA224Digest(),
    messages,
    digests
) {
    override fun performTest() {
        super.performTest()
        millionATest(million_a_digest)
    }

    override fun cloneDigest(digest: Digest?): Digest {
        return SHA224Digest((digest as SHA224Digest?)!!)
    }

    override fun cloneDigest(encodedState: ByteArray?): Digest {
        return SHA224Digest(encodedState!!)
    }

    companion object {
        private val messages = arrayOf(
            "",
            "a",
            "abc",
            "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"
        )
        private val digests = arrayOf(
            "d14a028c2a3a2bc9476102bb288234c415a2b01f828ea62ac5b3e42f",
            "abd37534c7d9a2efb9465de931cd7055ffdb8879563ae98078d6d6d5",
            "23097d223405d8228642a477bda255b32aadbce4bda0b3f7e36c9da7",
            "75388b16512776cc5dba5da1fd890150b0c6455cb4f58b1952522525"
        )

        // 1 million 'a'
        private const val million_a_digest =
            "20794655980c91d8bbb4c1ea97618a4bf03f42581948b2ee4ee7ad67"
    }

    @org.junit.Test
    fun test() {
        runTest(SHA224DigestTest())
    }
}
