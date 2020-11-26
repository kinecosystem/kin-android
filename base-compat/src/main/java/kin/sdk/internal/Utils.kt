package kin.sdk.internal

import kin.sdk.Balance
import kin.sdk.EventListener
import kin.sdk.ListenerRegistration
import kin.sdk.PaymentInfo
import kin.sdk.Transaction
import kin.sdk.TransactionId
import kin.sdk.WhitelistableTransaction
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.KinPayment
import org.kin.sdk.base.models.TransactionHash
import org.kin.sdk.base.models.toKeyPair
import org.kin.sdk.base.models.toUTF8String
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.tools.DisposeBag
import org.kin.sdk.base.tools.Observer
import org.kin.sdk.base.tools.Promise
import org.kin.stellarfork.Network
import org.kin.stellarfork.xdr.TransactionEnvelope
import org.kin.stellarfork.xdr.XdrDataInputStream
import java.io.ByteArrayInputStream
import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@JvmOverloads
internal fun latchOperation(
    times: Int = 1,
    timeoutSeconds: Long = Long.MAX_VALUE,
    operation: (CountDownLatch) -> Unit
) {
    val latch = CountDownLatch(times)
    operation(latch)

    fun assertTrue(message: String, function: () -> Boolean) {
        if (!function()) {
            println(message)
        }
    }

    assertTrue(message = "Latch Timed Out after $timeoutSeconds seconds!") {
        latch.await(
            timeoutSeconds,
            TimeUnit.SECONDS
        )
    }
}

internal data class LatchedValueCaptor<T>(val value: T?, val values: List<T>, val error: Throwable?)

internal typealias Capture = (Any?) -> Unit

@JvmOverloads
internal inline fun <reified T : Any> latchOperationValueCapture(
    times: Int = 1,
    timeoutSeconds: Long = Long.MAX_VALUE,
    crossinline captor: (Capture) -> Unit
): LatchedValueCaptor<T> {
    var value: T? = null
    val values = mutableListOf<T>()
    var error: Throwable? = null
    latchOperation(times, timeoutSeconds) { latch ->
        captor {
            when (it) {
                is Throwable -> {
                    error = it
                    println("Error: $error")
                    error?.printStackTrace()
                }
                is T -> {
                    value = it
                    values.add(it)
                }
            }
            latch.countDown()
        }
    }
    return LatchedValueCaptor(value, Collections.unmodifiableList(values).filterNotNull(), error)
}

internal inline fun <reified T : Any, reified V : Any> Promise<T>.syncAndMap(mapValue: ((T).() -> V)): V {
    return latchOperationValueCapture<T>(1, Long.MAX_VALUE) { capture ->
        then({ capture(it) }, { capture(it) })
    }.let { valueCaptor ->
        mapValue(valueCaptor.value ?: throw valueCaptor.error!!)
    }
}

internal inline fun <reified T : Any> Promise<T>.sync(): T = syncAndMap { this }

internal object Utils {
    @JvmStatic
    fun checkNotNull(obj: Any?, paramName: String) {
        requireNotNull(obj) { "$paramName == null" }
    }

    @JvmStatic
    fun checkNotEmpty(string: String?, paramName: String) {
        require(!(string == null || string.isEmpty())) { "$paramName cannot be null or empty." }
    }
}

internal fun <T, V> Observer<T>.asListenerRegistration(
    listener: EventListener<V>,
    mapValue: (T) -> V
): ListenerRegistration {
    val disposeBag = DisposeBag()
    this.add { listener.onEvent(mapValue(it)) }
        .disposedBy(disposeBag)
    return ListenerRegistration { disposeBag.dispose() }
}

internal fun <T, V> Observer<T>.asListenerRegistrationToList(
    listener: EventListener<V>,
    mapValue: (T) -> List<V>
): ListenerRegistration {
    val disposeBag = DisposeBag()
    this.add { mapValue(it).forEach { listener.onEvent(it) } }
        .disposedBy(disposeBag)
    return ListenerRegistration { disposeBag.dispose() }
}

// Conversation Utils

internal fun KinTransaction.asTransaction(network: Network): Transaction {

    val txn = try {
        val txnEnvelope = TransactionEnvelope.decode(
            XdrDataInputStream(ByteArrayInputStream(bytesValue))
        )
        org.kin.stellarfork.Transaction.fromEnvelopeXdr(txnEnvelope, network)
    } catch (t: Throwable) {
        null
    }

    val payOp = paymentOperations.first()
    return Transaction(
        payOp.destination.toKeyPair(),
        payOp.source.toKeyPair(),
        payOp.amount.value,
        fee.value.toInt(),
        memo.rawValue.toUTF8String(),
        transactionHash.toTransactionId(),
        txn,
        txn?.let { WhitelistableTransaction(txn.toEnvelopeXdrBase64(), network.networkPassphrase) }
    ).also {
        it.kinTransaction = this
    }
}

internal fun TransactionHash.toTransactionId(): TransactionId {
    return TransactionIdImpl(this)
}

internal fun NetworkEnvironment.toNetwork(): Network {
    return Network(networkPassphrase)
}

internal fun KinBalance.toBalance(): Balance {
    return BalanceImpl(this.amount)
}

internal fun KinPayment.toPaymentInfo(): PaymentInfo {
    return PaymentInfoImpl(this)
}
