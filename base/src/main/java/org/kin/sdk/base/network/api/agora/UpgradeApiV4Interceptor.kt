package org.kin.sdk.base.network.api.agora

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor

class UpgradeApiV4Interceptor() : ClientInterceptor {
    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        method: MethodDescriptor<ReqT, RespT>?,
        callOptions: CallOptions?,
        next: Channel?
    ): ClientCall<ReqT, RespT> {
        return object :
            ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
                next?.newCall(
                    method,
                    callOptions
                )
            ) {
            override fun start(
                responseListener: Listener<RespT>,
                headers: Metadata
            ) {
                headers.put(
                    Metadata.Key.of(
                        "desired-kin-version",
                        Metadata.ASCII_STRING_MARSHALLER
                    ), "4"
                )
                super.start(responseListener, headers)
            }
        }
    }
}
