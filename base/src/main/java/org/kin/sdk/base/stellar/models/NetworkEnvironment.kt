package org.kin.sdk.base.stellar.models

sealed class NetworkEnvironment(val networkPassphrase: String) {
    private companion object {
        const val TestNetPassphrase = "Kin Testnet ; December 2018"
        const val MainNetPassphrase = "Kin Mainnet ; December 2018"
    }

    object KinStellarTestNet : NetworkEnvironment(TestNetPassphrase)

    object KinStellarMainNet : NetworkEnvironment(MainNetPassphrase)

    override fun toString(): String {
        return "NetworkEnvironment(networkPassphrase='$networkPassphrase')"
    }
}
