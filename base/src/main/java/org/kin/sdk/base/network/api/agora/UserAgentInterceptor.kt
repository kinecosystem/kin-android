package org.kin.sdk.base.network.api.agora

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import org.kin.sdk.base.models.SDKConfig
import org.kin.sdk.base.storage.Storage

internal data class UserAgent(
    val systemUserAgent: String = SDKConfig.systemUserAgent,
    val platform: String = SDKConfig.platform,
    val versionString: String = SDKConfig.versionString,
    val cid: String
) {
    override fun toString(): String {
        return "$systemUserAgent KinSDK/$versionString ($platform; CID/$cid)"
    }
}

class UserAgentInterceptor(
    val storage: Storage
) : ClientInterceptor {
    companion object {
        private val HEADER_KEY_USER_AGENT: Metadata.Key<String> =
            Metadata.Key.of("kin-user-agent", Metadata.ASCII_STRING_MARSHALLER)
    }

    private val userAgentString: String by lazy {
        val cid = storage.getOrCreateCID()
        UserAgent(cid = cid).toString()
    }

    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        method: MethodDescriptor<ReqT, RespT>?,
        callOptions: CallOptions?,
        next: Channel?
    ): ClientCall<ReqT, RespT> {
        return object :
            SimpleForwardingClientCall<ReqT, RespT>(next?.newCall(method, callOptions)) {
            override fun start(
                responseListener: Listener<RespT>,
                headers: Metadata
            ) {
                headers.put(HEADER_KEY_USER_AGENT, userAgentString)
                super.start(responseListener, headers)
            }
        }
    }
}
