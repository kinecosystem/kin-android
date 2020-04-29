package org.kin.stellarfork

import org.kin.stellarfork.Util.CHARSET_UTF8
import org.kin.stellarfork.Util.hash
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

/**
 * Network class is used to specify which Stellar network you want to use.
 * Each network has a `networkPassphrase` which is hashed to
 * every transaction id.
 */
data class Network(
    /**
     * Returns network passphrase
     */
    val networkPassphrase: String
) {
    /**
     * Returns network id (SHA-256 hashed `networkPassphrase`).
     */
    val networkId: ByteArray?
        get() = try {
            hash(networkPassphrase.toByteArray(Charset.forName(CHARSET_UTF8)))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            null
        }

    companion object {
        private const val PUBLIC = "Kin Mainnet ; December 2018"
        private const val TESTNET = "Kin Testnet ; December 2018"
        @JvmStatic
        val publicNetwork: Network
            get() = Network(PUBLIC)
        @JvmStatic
        val testNetwork: Network
            get() = Network(TESTNET)
    }
}
