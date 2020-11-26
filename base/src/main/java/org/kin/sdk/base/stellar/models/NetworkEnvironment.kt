package org.kin.sdk.base.stellar.models

import org.kin.stellarfork.KeyPair

sealed class NetworkEnvironment(val networkPassphrase: String, val issuer: KeyPair? = null) {

    private companion object {
        const val Kin3TestNetPassphrase = "Kin Testnet ; December 2018"
        const val Kin3MainNetPassphrase = "Kin Mainnet ; December 2018"

        const val Kin2MainNetPassphrase = "Public Global Kin Ecosystem Network ; June 2018"
        const val Kin2TestNetPassphrase = "Kin Playground Network ; June 2018"
        val Kin2IssuerProd = KeyPair.fromAccountId("GDF42M3IPERQCBLWFEZKQRK77JQ65SCKTU3CW36HZVCX7XX5A5QXZIVK")
        val Kin2IssuerTest = KeyPair.fromAccountId("GBC3SG6NGTSZ2OMH3FFGB7UVRQWILW367U4GSOOF4TFSZONV42UJXUH7")
    }

    object KinStellarTestNetKin3 : NetworkEnvironment(Kin3TestNetPassphrase)

    object KinStellarMainNetKin3 : NetworkEnvironment(Kin3MainNetPassphrase)

    object KinStellarTestNetKin2 : NetworkEnvironment(Kin2TestNetPassphrase, Kin2IssuerTest)

    object KinStellarMainNetKin2 : NetworkEnvironment(Kin2MainNetPassphrase, Kin2IssuerProd)


    override fun toString(): String {
        return "NetworkEnvironment(networkPassphrase='$networkPassphrase')"
    }
}
