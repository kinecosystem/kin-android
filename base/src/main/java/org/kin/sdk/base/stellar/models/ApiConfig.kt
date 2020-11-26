package org.kin.sdk.base.stellar.models

sealed class ApiConfig(
    val networkEndpoint: String,
    val networkEnv: NetworkEnvironment,
    val tlsPort: Int
) {
    private companion object {
        private const val TLS_PORT = 443

        const val TestNetHorizonEndpoint = "https://horizon-testnet.kininfrastructure.com/"
        const val MainNetHorizonEndpoint = "https://horizon.kinfederation.com"

        const val TestNetAgoraEndpoint = "api.agorainfra.dev"
        const val MainNetAgoraEndpoint = "api.agorainfra.net"
    }

    object TestNetHorizon :
        ApiConfig(TestNetHorizonEndpoint, NetworkEnvironment.KinStellarTestNetKin3, TLS_PORT)

    object MainNetHorizon :
        ApiConfig(MainNetHorizonEndpoint, NetworkEnvironment.KinStellarMainNetKin3, TLS_PORT)

    object TestNetAgora :
        ApiConfig(TestNetAgoraEndpoint, NetworkEnvironment.KinStellarTestNetKin3, TLS_PORT)

    object MainNetAgora :
        ApiConfig(MainNetAgoraEndpoint, NetworkEnvironment.KinStellarMainNetKin3, TLS_PORT)

    class CustomConfig(
        networkEndpoint: String,
        networkEnv: NetworkEnvironment,
        tlsPort: Int
    ) : ApiConfig(networkEndpoint, networkEnv, tlsPort)
}

