package org.kin.sdk.base.stellar.models

sealed class ApiConfig(
    val networkEndpoint: String,
    val networkEnv: NetworkEnvironment,
    val tlsPort: Int
) {
    private companion object {
        private const val TLS_PORT = 8085 //443

        const val TestNetHorizonEndpoint = "https://horizon-testnet.kininfrastructure.com/"
        const val MainNetHorizonEndpoint = "https://horizon.kinfederation.com"
    }

    object TestNetHorizon :
        ApiConfig(TestNetHorizonEndpoint, NetworkEnvironment.KinStellarTestNet, TLS_PORT)

    object MainNetHorizon :
        ApiConfig(MainNetHorizonEndpoint, NetworkEnvironment.KinStellarMainNet, TLS_PORT)

    class CustomConfig(
        networkEndpoint: String,
        networkEnv: NetworkEnvironment,
        tlsPort: Int
    ) : ApiConfig(networkEndpoint, networkEnv, tlsPort)
}

