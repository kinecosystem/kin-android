package org.kin.sdk.base.network.api.agora

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import org.kin.sdk.base.network.services.AppInfoProvider

class AppUserAuthInterceptor(
    private val appInfoProvider: AppInfoProvider
) : ClientInterceptor {
    companion object {
        val HEADER_KEY_APP_USER_ID: Metadata.Key<String> =
            Metadata.Key.of("app-user-id", Metadata.ASCII_STRING_MARSHALLER)
        val HEADER_KEY_APP_USER_PASSKEY: Metadata.Key<String> =
            Metadata.Key.of("app-user-passkey", Metadata.ASCII_STRING_MARSHALLER)
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
                if (method?.fullMethodName == "kin.agora.transaction.v3.Transaction/SubmitTransaction") {
                    val creds = appInfoProvider.getPassthroughAppUserCredentials()
                    headers.put(HEADER_KEY_APP_USER_ID, creds.appUserId)
                    headers.put(HEADER_KEY_APP_USER_PASSKEY, creds.appUserPasskey)
                }
                super.start(responseListener, headers)
            }
        }
    }
}
