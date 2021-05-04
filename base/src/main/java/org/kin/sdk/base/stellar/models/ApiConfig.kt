package org.kin.sdk.base.stellar.models

sealed class ApiConfig(
    val networkEndpoint: String,
    val networkEnv: NetworkEnvironment,
    val tlsPort: Int
) {
    private companion object {
        private const val TLS_PORT = 443

        const val TestNetAgoraEndpoint = "api.agorainfra.dev"
        const val MainNetAgoraEndpoint = "api.agorainfra.net"
    }

    object TestNetAgora :
        ApiConfig(TestNetAgoraEndpoint, NetworkEnvironment.TestNet, TLS_PORT)

    object MainNetAgora :
        ApiConfig(MainNetAgoraEndpoint, NetworkEnvironment.MainNet, TLS_PORT)

    class CustomConfig(
        networkEndpoint: String,
        networkEnv: NetworkEnvironment,
        tlsPort: Int
    ) : ApiConfig(networkEndpoint, networkEnv, tlsPort)
}

