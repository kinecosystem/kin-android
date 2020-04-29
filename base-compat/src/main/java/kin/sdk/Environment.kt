package kin.sdk

import kin.sdk.internal.Utils.checkNotEmpty
import org.kin.stellarfork.Network

/**
 * Provides blockchain network details
 */
class Environment(networkUrl: String, networkPassphrase: String?) {
    /**
     * Returns the URL of the blockchain node.
     */
    val networkUrl: String
    val network: Network

    /**
     * Returns the network id.
     */
    val networkPassphrase: String
        get() = network.networkPassphrase

    val isMainNet: Boolean
        get() = PRODUCTION.networkPassphrase == network.networkPassphrase

    companion object {
        @JvmField
        val PRODUCTION = Environment(
            "https://horizon.kinfederation.com",
            "Kin Mainnet ; December 2018"
        )

        @JvmField
        val TEST = Environment(
            "https://horizon-testnet.kininfrastructure.com/",
            "Kin Testnet ; December 2018"
        )
    }

    /**
     * Build an Environment object.
     *
     * @param networkUrl        the URL of the blockchain node.
     * @param networkPassphrase the network id to be used.
     */
    init {
        checkNotEmpty(networkUrl, "networkUrl")
        checkNotEmpty(networkPassphrase, "networkPassphrase")
        this.networkUrl = networkUrl
        network = Network(networkPassphrase!!)
    }
}
