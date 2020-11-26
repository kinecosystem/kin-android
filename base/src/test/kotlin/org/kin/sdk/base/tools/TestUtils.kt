package org.kin.sdk.base.tools

import org.kin.sdk.base.models.InvoiceList
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.asPrivateKey
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.stellar.models.SolanaKinTransaction
import org.kin.sdk.base.stellar.models.StellarKinTransaction
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.codec.Base64
import java.util.Collections
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

fun latchOperation(
    times: Int = 1,
    timeoutSeconds: Long = 5,
    operation: (CountDownLatch) -> Unit
) {
    val latch = CountDownLatch(times)
    operation(latch)
    assertTrue(message = "Latch Timed Out after $timeoutSeconds seconds!") {
        latch.await(
            timeoutSeconds,
            TimeUnit.SECONDS
        )
    }
}

data class LatchedValueCaptor<T>(val value: T?, val values: List<T>, val error: Throwable?)

typealias Capture = (Any?) -> Unit

inline fun <reified T : Any> latchOperationValueCapture(
    times: Int = 1,
    timeoutSeconds: Long = 5,
    crossinline captor: (Capture) -> Unit
): LatchedValueCaptor<T> {
    var value: T? = null
    val values = CopyOnWriteArrayList<T>()
    var error: Throwable? = null
    latchOperation(times, timeoutSeconds) { latch ->
        try {
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
        } catch (e: Throwable) {
            error = e
            println("Error: $error")
            error?.printStackTrace()
            latch.countDown()
        }
    }
    return LatchedValueCaptor(value, Collections.unmodifiableList(values).filterNotNull(), error)
}

fun <T : Any> LatchedValueCaptor<T>.test(
    onTest: (LatchedValueCaptor<T>).() -> Unit
) {
    onTest(this)
}

fun <T : Any> LatchedValueCaptor<T>.testValue(
    onTest: (T) -> Unit
) {
    this.value?.let { onTest(it) } ?: (throw (error ?: Exception("Missing Error")))
}

inline fun <reified T : Any> Promise<T>.test(
    timeout: Long = 15,
    onTest: (LatchedValueCaptor<T>).() -> Unit
): Promise<T> {
    latchOperationValueCapture<T>(timeoutSeconds = timeout) { capture ->
        then({
            capture(it)
        }, {
            capture(it)
        })
    }.let {
        onTest(it)
    }
    return this
}

inline fun <reified T : Any> Observer<T>.test(
    times: Int = 1,
    timeout: Long = 5,
    onTest: (LatchedValueCaptor<T>).() -> Unit
): Observer<T> {
    latchOperationValueCapture<T>(times = times, timeoutSeconds = timeout) { capture ->
        add { capture(it) }
    }.let {
        onTest(it)
    }
    return this
}

fun <T> Capture.byAutoCapturePromisedCallback() =
    PromisedCallback<T>({ this(it as Any) }, { this(it) })

class TestUtils {
    companion object {
        fun newPublicKey() = Key.PublicKey(KeyPair.random().publicKey)
        fun newPrivateKey() = KeyPair.random().asPrivateKey()

        fun newKinAccount() = KinAccount(newPublicKey())
        fun newSigningKinAccount() = KinAccount(newPrivateKey())
        fun fromSecretSeed(seed: String) = KinAccount(KeyPair.fromSecretSeed(seed).asPrivateKey())
        fun fromAccountId(accountId: String) =
            KinAccount(KeyPair.fromAccountId(accountId).asPublicKey())

        fun kinTransactionFromXdr(
            base64StringXdr: String,
            recordType: KinTransaction.RecordType,
            networkEnvironment: NetworkEnvironment = NetworkEnvironment.KinStellarTestNetKin3,
            invoiceList: InvoiceList? = null
        ) = StellarKinTransaction(Base64.decodeBase64(base64StringXdr)!!, recordType, networkEnvironment, invoiceList)

        fun kinTransactionFromSolanaTransaction(
            base64StringBytes: String,
            recordType: KinTransaction.RecordType,
            networkEnvironment: NetworkEnvironment = NetworkEnvironment.KinStellarTestNetKin3,
            invoiceList: InvoiceList? = null
        ) = SolanaKinTransaction(Base64.decodeBase64(base64StringBytes)!!, recordType, networkEnvironment, invoiceList)
    }
}

fun KinAccount.updateStatus(status: KinAccount.Status) = KinAccount(key, id, tokenAccounts, balance, status)

fun String.chunkForStream(id: String = "data", chunkSize: Int = Int.MAX_VALUE): String =
    "id: $id\n\n" + chunked(chunkSize) { "data: ${it}\n" }
        .reduce { acc, s -> acc + s }
        .also { println(it) }
