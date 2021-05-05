package kin.sdk.internal

import org.kin.sdk.base.tools.Promise
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
