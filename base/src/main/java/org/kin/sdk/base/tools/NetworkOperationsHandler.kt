package org.kin.sdk.base.tools

import java.util.Random
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class RetriesExceededException : Throwable()

sealed class NetworkOperationsHandlerException(message: String) : Throwable(message) {
    class OperationTimeoutException : NetworkOperationsHandlerException("Op timed out")
}

sealed class BackoffStrategy(
    val maxAttempts: Int = DEFAULT_MAX_ATTEMPTS
) {
    private var currentAttempt = 0

    companion object {
        private const val DEFAULT_MAX_ATTEMPTS = 5
        private const val DEFAULT_MAX_ATTEMPT_WAIT_TIME: Long = 15000
        private val randomSource = Random(System.currentTimeMillis())
        private const val InfiniteRetries: Int = -1

        fun combine(vararg strategies: BackoffStrategy): BackoffStrategy {
            val totalMaxAttempts =
                strategies.map { it.maxAttempts }.reduce { acc, maxAttempts -> acc + maxAttempts }

            return BackoffStrategy.Custom({ currentAttempt ->
                var previousAttempts = 0
                for (it in strategies) {
                    if ((currentAttempt - previousAttempts) < it.maxAttempts) {
                        return@Custom it.nextDelay()
                    } else {
                        previousAttempts += it.maxAttempts
                    }
                }
                previousAttempts = 0
                -1
            }, { strategies.forEach { it.reset() } }, totalMaxAttempts)
        }
    }

    class Never(
        maxAttempts: Int = DEFAULT_MAX_ATTEMPTS
    ) : BackoffStrategy(maxAttempts = maxAttempts)

    class Fixed(
        val after: Long,
        maxAttempts: Int = DEFAULT_MAX_ATTEMPTS
    ) : BackoffStrategy(maxAttempts = maxAttempts)

    class Exponential(
        val initial: Long = 1000,
        val multiplier: Double = 2.0,
        val jitter: Double = 0.5,
        val maximumWaitTime: Long = DEFAULT_MAX_ATTEMPT_WAIT_TIME,
        maxAttempts: Int = DEFAULT_MAX_ATTEMPTS
    ) : BackoffStrategy(maxAttempts = maxAttempts)

    class ExponentialIncrease(
        val initial: Long = 1000,
        val multiplier: Double = 2.0,
        val jitter: Double = 0.5,
        val maximumWaitTime: Long = DEFAULT_MAX_ATTEMPT_WAIT_TIME,
        maxAttempts: Int = DEFAULT_MAX_ATTEMPTS
    ) : BackoffStrategy(maxAttempts = maxAttempts)

    class Custom(
        val afterClosure: (Int) -> Long,
        val reset: () -> Unit,
        maxAttempts: Int = DEFAULT_MAX_ATTEMPTS
    ) : BackoffStrategy(maxAttempts = maxAttempts)

    fun nextDelay(): Long = delayForAttempt(currentAttempt++)

    fun reset() {
        currentAttempt = 0
    }

    @Throws(RetriesExceededException::class)
    fun delayForAttempt(attempt: Int): Long {
        if (attempt >= maxAttempts && maxAttempts != InfiniteRetries) {
            throw RetriesExceededException()
        }

        // The first backoff attempt is attempt=1.
        if (attempt <= 0 && this !is Custom && this !is ExponentialIncrease) {
            return 0
        }

        return when (this) {
            is Never -> throw RetriesExceededException()
            is Fixed -> after
            is Exponential -> {
                val delay = initial * multiplier.pow((attempt - 1).toDouble())
                val jitterAmount = delay * jitter * randomSource.nextDouble()

                min(maximumWaitTime, max(0, (delay + jitterAmount).toLong()))
            }
            is ExponentialIncrease -> {
                val delay = initial * multiplier.pow((maxAttempts - attempt).toDouble())
                val jitterAmount = delay * jitter * randomSource.nextDouble()

                min(maximumWaitTime, max(0, (delay + jitterAmount).toLong()))
            }
            is Custom -> afterClosure(attempt)
        }
    }
}

data class NetworkOperation<ResponseType>
/**
 * @param onCompleted - will be called when the operation has completed, successfully or with an error (including if it timed out, or failed fatally)
 * @param id - a unique identifier for the operation
 * @param timeout - task will timeout in milliseconds, if not completed within the timeout period, with [NetworkOperationsHandlerException.OperationTimeoutException]
 * @param backoffStrategy - the strategy used to retry a task that fails
 * @param callback - the work performed by the operation
 */
constructor(
    val onCompleted: PromisedCallback<ResponseType>,
    val id: String = generateRandomId(),
    val timeout: Long = DEFAULT_TIMEOUT,
    val backoffStrategy: BackoffStrategy = BackoffStrategy.Exponential(
        maximumWaitTime = timeout
    ),
    val callback: (PromisedCallback<ResponseType>, error: Throwable?) -> Unit,
    val shouldRetryError: ((Throwable) -> Boolean)? = null
) {
    constructor(
        onSuccess: (ResponseType) -> Unit,
        onError: ((Throwable) -> Unit)? = null,
        id: String = generateRandomId(),
        timeout: Long = DEFAULT_TIMEOUT,
        backoffStrategy: BackoffStrategy = BackoffStrategy.Exponential(
            maximumWaitTime = timeout
        ),
        callback: (PromisedCallback<ResponseType>, error: Throwable?) -> Unit
    ) : this(PromisedCallback(onSuccess, onError), id, timeout, backoffStrategy, callback)

    companion object {
        private const val DEFAULT_TIMEOUT: Long = 50000
        private val randomSource = Random(System.currentTimeMillis())
        fun generateRandomId(): String {
            return randomSource.nextLong().toString()
        }
    }

    var state: State =
        State.INIT
        set(value) {
            (field as? State.SCHEDULED)?.cancellable?.cancel(true)
            field = value
        }

    sealed class State {

        /**
         * Recently created, not yet queued or scheduled
         */
        object INIT : State()

        /**
         * Operation has been added to the `activeOperations` list and is awaiting scheduling.
         */
        object QUEUED : State()

        /**
         * Operation has been scheduled to execute at a certain [executionTimestamp].
         * There should exist a [ScheduledFuture] associated with this operation's scheduling
         */
        data class SCHEDULED(
            val executionTimestamp: Long,
            val cancellable: ScheduledFuture<*>
        ) : State()

        /**
         * Operation has been run and is in-flight
         */
        object RUNNING : State()

        /**
         * Operation has completed
         */
        object COMPLETED : State()

        /**
         * Operation has been run and has run into an [error]
         */
        data class ERRORED(val error: Throwable) : State()
    }

    var expiryFuture: ScheduledFuture<*>? = null
}

interface NetworkOperationsHandler {
    fun <ResponseType> queueOperation(op: NetworkOperation<ResponseType>): NetworkOperation<ResponseType>
}

class NetworkOperationsHandlerImpl(
    private val ioScheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    private val ioExecutor: ExecutorService = Executors.newScheduledThreadPool(
        Runtime.getRuntime().availableProcessors()
    ),
    private val logger: KinLoggerFactory,
    private val shouldRetryError: (Throwable) -> Boolean = { false }
) : NetworkOperationsHandler {
    private val operations = hashMapOf<String, NetworkOperation<*>>()
    private val log = logger.getLogger(javaClass.simpleName)

    override fun <ResponseType> queueOperation(op: NetworkOperation<ResponseType>): NetworkOperation<ResponseType> {
        return op.apply {
            state = NetworkOperation.State.QUEUED
            expiryFuture = ioScheduler.schedule(
                { expire() },
                timeout,
                TimeUnit.MILLISECONDS
            )
            synchronized(operations) { operations[id] = op }
            schedule()
        }
    }

    private fun <ResponseType> NetworkOperation<ResponseType>.expire() {
        val error = NetworkOperationsHandlerException.OperationTimeoutException()
        state = NetworkOperation.State.ERRORED(error)
        onCompleted.onError?.invoke(error)
        cleanup()
    }

    private fun NetworkOperation<*>.schedule(error: Throwable? = null) {
        log.log("schedule[id=$id]")
        val delayMillis = try {
            backoffStrategy.nextDelay()
        } catch (e: Throwable) {
            (state as? NetworkOperation.State.ERRORED)?.let {
                fatalError(it.error.apply { addSuppressed(e) })
            } ?: fatalError(e)
            return
        }
        log.log("delayMillis[id=$id]: + $delayMillis")

        state = NetworkOperation.State.SCHEDULED(
            System.currentTimeMillis() + delayMillis,
            ioScheduler.schedule(
                { ioExecutor.submit { runOperation(error) } },
                delayMillis,
                TimeUnit.MILLISECONDS
            )
        )
    }

    private fun <ResponseType> NetworkOperation<ResponseType>.runOperation(error: Throwable? = null) = apply {
        log.log("runOperation[id=$id]")
        state = NetworkOperation.State.RUNNING

        try {
            callback(PromisedCallback({
                complete()
                onCompleted.onSuccess(it)
            }, { handleError(it) }), error)
        } catch (error: Throwable) {
            handleError(error)
        }
    }

    private fun NetworkOperation<*>.complete() = apply {
        log.log("complete[id=$id]")
        state = NetworkOperation.State.COMPLETED
        cleanup()

    }

    private fun NetworkOperation<*>.handleError(error: Throwable) = apply {
        log.log("handleError[id=$id]: $error")
        if (this.shouldRetryError?.let { it(error) } == true || shouldRetryError(error)) {
            state = NetworkOperation.State.ERRORED(error)
            schedule()
        } else fatalError(error)
    }

    private fun NetworkOperation<*>.fatalError(e: Throwable) = apply {
        log.log("fatalError[id=$id]: $e")
        state = NetworkOperation.State.ERRORED(e)
        onCompleted.onError?.invoke(e)
        cleanup()
    }

    private fun NetworkOperation<*>.cleanup() = apply {
        expiryFuture?.cancel(true)
            ?.let {
                if (it) expiryFuture = null
            }
        synchronized(operations) { operations.remove(id) }
    }
}
