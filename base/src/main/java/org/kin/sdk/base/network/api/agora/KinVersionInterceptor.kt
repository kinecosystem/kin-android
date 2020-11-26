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

class KinVersionInterceptor(
    private val version: Int
) : ClientInterceptor {
    companion object {
        private val HEADER_KEY_KIN_VERSION: Metadata.Key<String> =
            Metadata.Key.of("kin-version", Metadata.ASCII_STRING_MARSHALLER)
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
                headers.put(HEADER_KEY_KIN_VERSION, version.toString())
                super.start(responseListener, headers)
            }
        }
    }
}
