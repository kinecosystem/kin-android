package org.kin.sdk.base.stellar.models

sealed class NetworkEnvironment(val networkPassphrase: String) {

    private companion object {
        const val Kin3TestNetPassphrase = "Kin Testnet ; December 2018"
        const val Kin3MainNetPassphrase = "Kin Mainnet ; December 2018"
    }

    object TestNet : NetworkEnvironment(Kin3TestNetPassphrase)

    object MainNet : NetworkEnvironment(Kin3MainNetPassphrase)


    override fun toString(): String {
        return "NetworkEnvironment(networkPassphrase='$networkPassphrase')"
    }
}
