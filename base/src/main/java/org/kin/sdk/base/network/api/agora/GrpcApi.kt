package org.kin.sdk.base.network.api.agora

import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.ClientCallStreamObserver
import io.grpc.stub.ClientResponseObserver
import io.grpc.stub.StreamObserver
import org.kin.sdk.base.tools.NetworkOperationsHandlerException.OperationTimeoutException
import org.kin.sdk.base.tools.ObservableCallback
import org.kin.sdk.base.tools.PromisedCallback
import kotlin.reflect.KFunction2

abstract class GrpcApi(protected val managedChannel: ManagedChannel) {

    companion object {
        private val RETRYABLE_STATUS = setOf(
            Status.Code.UNKNOWN,
            Status.Code.CANCELLED,
            Status.Code.DEADLINE_EXCEEDED,
            Status.Code.ABORTED,
            Status.Code.INTERNAL,
            Status.Code.UNAVAILABLE
        )

        fun Throwable.canRetry(): Boolean =
            this is StatusRuntimeException && RETRYABLE_STATUS.contains(this.status.code)
                    || this is OperationTimeoutException

        fun Throwable.isForcedUpgrade(): Boolean =
            this is StatusRuntimeException && Status.Code.FAILED_PRECONDITION == this.status.code

    }

    object UnrecognizedProtoResponse :
        Exception("Unregonized Response format...possible breaking proto changes")

    data class UnrecognizedResultException(override val cause: Throwable) :
        Exception("Received an unknown result type", cause)

    fun <Request, Response> KFunction2<Request, StreamObserver<Response>, Unit>.callAsPromisedCallback(
        request: Request,
        callback: PromisedCallback<Response>
    ) {
        val observer = object : ClientResponseObserver<Request, Response> {
            override fun onNext(value: Response) = callback.onSuccess(value)
            override fun onError(error: Throwable) {
                callback.onError?.invoke(error)
            }

            override fun onCompleted() {}
            override fun beforeStart(requestStream: ClientCallStreamObserver<Request>?) {}
        }
        try {
            this(request, observer)
        } catch (error: Throwable) {
            observer.onError(error)
        }
    }

    interface StreamHandler<Request> {
        fun cancel()
    }

    fun <Request, Response> KFunction2<Request, StreamObserver<Response>, Unit>.callAsObservableCallback(
        request: Request,
        callback: ObservableCallback<Response>
    ): StreamHandler<Request> {
        var stream: ClientCallStreamObserver<Request>? = null

        val observer = object : ClientResponseObserver<Request, Response> {
            override fun onNext(value: Response) = callback.onNext(value)
            override fun onError(error: Throwable) {
                callback.onError?.invoke(error)
            }

            override fun onCompleted() = callback.onCompleted()
            override fun beforeStart(requestStream: ClientCallStreamObserver<Request>?) {
                stream = requestStream
            }
        }
        try {
            this(request, observer)
        } catch (error: Throwable) {
            observer.onError(error)
        }

        return object : StreamHandler<Request> {
            override fun cancel() {
                stream?.cancel("Cancelling Stream", null)
            }
        }
    }
}
