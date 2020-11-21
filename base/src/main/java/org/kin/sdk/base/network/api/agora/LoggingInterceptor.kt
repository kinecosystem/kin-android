package org.kin.sdk.base.network.api.agora

//import com.google.protobuf.Message
//import com.google.protobuf.TextFormat
import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.Status
import org.kin.sdk.base.tools.KinLoggerFactory

class LoggingInterceptor(private val logger: KinLoggerFactory) : ClientInterceptor {

    private val log = logger.getLogger(javaClass.simpleName)

    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        method: MethodDescriptor<ReqT, RespT>?,
        callOptions: CallOptions?,
        next: Channel?,
    ): ClientCall<ReqT, RespT> {
        return object :
            SimpleForwardingClientCall<ReqT, RespT>(next?.newCall(method, callOptions)) {
            override fun sendMessage(message: ReqT) {
                log.log{"request --- [gRPCLogInterceptor]::${method?.fullMethodName}" +
                        "\n${message.toString()}" +
                        "--- request"}
                super.sendMessage(message)
            }

            override fun start(responseListener: Listener<RespT>?, headers: Metadata?) {
                val listener: Listener<RespT> = object : Listener<RespT>() {
                    override fun onMessage(message: RespT) {
                        log.log{"response --- [gRPCLogInterceptor]::${method?.fullMethodName}" +
                                "\n${message.toString()}" +
                                "--- response"}
                        responseListener?.onMessage(message)
                        super.onMessage(message)
                    }

                    override fun onClose(status: Status?, trailers: Metadata?) {
                        responseListener?.onClose(status, trailers)
                        super.onClose(status, trailers)
                    }

                    override fun onHeaders(headers: Metadata?) {
                        responseListener?.onHeaders(headers)
                        super.onHeaders(headers)
                    }

                    override fun onReady() {
                        responseListener?.onReady()
                        super.onReady()
                    }
                }
                super.start(listener, headers)
            }
        }
    }
}
