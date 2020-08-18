package org.kin.sdk.base.network.api.horizon

import okhttp3.OkHttpClient
import org.kin.sdk.base.stellar.models.ApiConfig
import org.kin.stellarfork.KinServer
import org.kin.stellarfork.Server

abstract class KinJsonApi(
    val environment: ApiConfig,
    val okHttpClient: OkHttpClient,
    val server: KinServer = Server(environment.networkEndpoint, okHttpClient)
) {
    object MalformedBodyException : Exception("Malformed Body")
    object TimeoutException : Exception("Timed Out")
}
