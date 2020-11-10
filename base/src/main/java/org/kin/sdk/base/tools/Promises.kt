package org.kin.sdk.base.tools

import org.kin.sdk.base.network.api.KinTransactionApiV4
import org.kin.sdk.base.tools.Promise.State
import java.util.Collections
import java.util.Timer
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class PromisedCallback<T>(
    val onSuccess: (value: T) -> Unit,
    val onError: ((error: Throwable) -> Unit)? = { throw it }
) {
    operator fun invoke(value: T) = onSuccess(value)
    operator fun invoke(error: Throwable) = onError?.invoke(error)
}

interface Promise<out T> {
    companion object {
        fun <T> create(work: (resolve: (T) -> Unit, reject: (Throwable) -> Unit) -> Unit): Promise<T> {
            return SimplePromise(work)
        }

        fun <T> defer(promise: () -> Promise<T>): Promise<T> {
            return create { resolve, reject ->
                promise().then(resolve, reject)
            }
        }

        fun <T> of(value: T): Promise<T> =
            create<T> { resolve, _ -> resolve(value) }

        fun <T> error(value: Throwable): Promise<T> =
            create<T> { _, reject -> reject(value) }

        fun <T> allAny(vararg promises: Promise<T>): Promise<List<T>> {
            val values = CopyOnWriteArrayList<T>()
            return create { resolve, reject ->
                for (promise in promises) {
                    promise.then({
                        values += it
                        if (values.size == promises.size) {
                            resolve(Collections.unmodifiableList(values))
                        }
                    }, { reject(it) })
                }
            }
        }

        inline fun <reified A,reified B> all(promise1: Promise<A>, promise2: Promise<B>): Promise<Pair<A,B>> {
            return allAny(promise1, promise2).map {
                val first = it.find { it is A } as A
                val second = it.find { it is B } as B
                Pair(first, second)
            }
        }

        inline fun <reified A,reified B,reified C> all(promise1: Promise<A>, promise2: Promise<B>, promise3: Promise<C>): Promise<Triple<A,B,C>> {
            return allAny(promise1, promise2, promise3).map {
                val first = it.find { it is A } as A
                val second = it.find { it is B } as B
                val third = it.find { it is C } as C
                Triple(first, second, third)
            }
        }
    }

    sealed class State<out T>(val value: Int) {
        class Pending<T> : State<T>(0)
        data class Resolved<T>(val result: T) : State<T>(1)
        data class Rejected<T>(val error: Throwable) : State<T>(2)
    }

    fun workOn(executor: ExecutorService): Promise<T>

    fun resolveOn(executor: ExecutorService): Promise<T>

    fun then(
        onResolved: (T) -> Unit,
        onRejected: (Throwable) -> Unit
    )

    fun then(
        onResolved: (T) -> Unit
    )

    fun resolve()

    fun resolveIn(amount: Long, unit: TimeUnit)

    fun <S> flatMap(
        onResolved: (T) -> Promise<S>,
        onRejected: (Throwable) -> Promise<S>
    ): Promise<S>

    fun <S> flatMap(
        onResolved: (T) -> Promise<S>
    ): Promise<S>

    fun <S> map(
        onResolved: (T) -> S,
        onRejected: (Throwable) -> Throwable
    ): Promise<S>

    fun <S> map(
        onResolved: (T) -> S
    ): Promise<S>

    fun doOnResolved(onResolved: (T) -> Unit): Promise<T>
    fun doOnError(onRejected: (Throwable) -> Unit): Promise<T>
}

class PromiseQueue<T> {
    data class Item<T>(val promise: Promise<T>, val callback: PromisedCallback<T>)

    private val queue = CopyOnWriteArrayList<Item<T>>()
    private val executor = Executors.newSingleThreadExecutor()
    private val isFlushingLock = Any()
    private var isFlushing: Boolean = false
        set(value) {
            synchronized(isFlushingLock) {
                field = value
            }
        }
        get() = synchronized(isFlushingLock) { field }

    fun queue(promise: Promise<T>): Promise<T> {
        return Promise.create<T> { resolve, reject ->
            queue.add(Item(promise, PromisedCallback(resolve, reject)))
            flush()
        }
    }

    private fun flush() {
        if (isFlushing) {
            return
        }
        pop()?.let { queued ->
            isFlushing = true
            try {
                queued.promise.resolveOn(executor).then({
                    queued.callback.onSuccess(it)
                    isFlushing = false
                    flush()
                }, {
                    queued.callback.onError?.invoke(it)
                    isFlushing = false
                    flush()
                })
            } catch (e: Throwable) {
                queued.callback.onError?.invoke(e)
                isFlushing = false
                flush()
            }
        }
    }

    private fun pop() = queue.firstOrNull()?.let { queue.removeAt(0) }
}

private class SimplePromise<out T>(
    private var work: ((resolve: (T) -> Unit, reject: (Throwable) -> Unit) -> Unit)?
) : Promise<T> {
    private val stateLock = Any()
    private var state: State<T> = State.Pending<T>()

    private var workExecutor: ExecutorService? = null
    private var resolveExecutor: ExecutorService? = null

    private val resolutions = CopyOnWriteArrayList<Pair<((T) -> Unit)?, ((Throwable) -> Unit)?>>()

    private fun maybeDoWork() {
        val pendingWork = synchronized(stateLock) {
            val retainedWork = work

            work = null

            retainedWork
        }

        if (pendingWork != null) {
            submitOrRunOn(workExecutor) { pendingWork(::resolve, ::reject) }
        }
    }

    private fun resolve(value: T) = updateState(State.Resolved<T>(value))

    private fun reject(error: Throwable) = updateState(State.Rejected(error))

    private fun queueOrDisptach(onResolved: ((T) -> Unit)?, onRejected: ((Throwable) -> Unit)?) {
        val currentState = synchronized(stateLock) {
            if (state is State.Pending) {
                resolutions.add(Pair(onResolved, onRejected))

                return
            }

            state
        }

        if (currentState is State.Resolved) {
            onResolved?.invoke(currentState.result)
        } else if (currentState is State.Rejected) {
            onRejected?.invoke(currentState.error)
        }
    }

    private fun updateState(newState: State<T>) {
        if (state is State.Pending) {
            val queuedResolutions = synchronized(stateLock) {
                if (state !is State.Pending) {
                    return
                }

                state = newState

                val retainedResolutions = resolutions.toList()

                resolutions.clear()

                retainedResolutions
            }

            if (newState is State.Resolved) {
                queuedResolutions.forEach { (resolved, _) ->
                    submitOrRunOn(resolveExecutor) { resolved?.let { it(newState.result) } }
                }
            } else if (newState is State.Rejected) {
                queuedResolutions.forEach { (_, rejected) ->
                    submitOrRunOn(resolveExecutor) { rejected?.let { it(newState.error) } }
                }
            }
        }
    }

    override fun workOn(executor: ExecutorService): Promise<T> {
        if (workExecutor == null) workExecutor = executor
        return this
    }

    override fun resolveOn(executor: ExecutorService): Promise<T> {
        resolveExecutor = executor
        return this
    }

    override fun then(
        onResolved: (T) -> Unit,
        onRejected: ((Throwable) -> Unit)
    ) {
        queueOrDisptach(onResolved, onRejected)

        maybeDoWork()
    }

    override fun then(onResolved: (T) -> Unit) {
        return then(onResolved, { throw it })
    }

    override fun <S> flatMap(
        onResolved: (T) -> Promise<S>,
        onRejected: ((Throwable) -> Promise<S>)
    ): Promise<S> {
        return SimplePromise<S> { resolve, reject ->
            then({ value ->
                try {
                    onResolved(value).then(resolve, reject)
                } catch (e: Throwable) {
                    reject(e)
                }
            }, { error ->
                try {
                    onRejected(error).then(resolve, reject)
                } catch (e: Throwable) {
                    reject(e)
                }
            })
        }
    }

    override fun <S> flatMap(onResolved: (T) -> Promise<S>): Promise<S> {
        return flatMap(onResolved, { Promise.error(it) })
    }

    override fun <S> map(
        onResolved: (T) -> S,
        onRejected: ((Throwable) -> Throwable)
    ): Promise<S> {
        return flatMap({ value ->
            SimplePromise<S> { resolve, reject ->
                try {
                    resolve(onResolved(value))
                } catch (e: Throwable) {
                    reject(e)
                }
            }
        }, { error ->
            SimplePromise<S> { _, reject ->
                try {
                    reject(onRejected(error))
                } catch (e: Throwable) {
                    reject(e)
                }
            }
        })
    }

    override fun <S> map(onResolved: (T) -> S): Promise<S> {
        return map(onResolved, { it })
    }

    override fun doOnResolved(onResolved: (T) -> Unit): Promise<T> {
        queueOrDisptach(onResolved, null)
        return this
    }

    override fun doOnError(onRejected: (Throwable) -> Unit): Promise<T> {
        queueOrDisptach(null, onRejected)
        return this
    }

    override fun resolve() {
        maybeDoWork()
    }

    override fun resolveIn(amount: Long, unit: TimeUnit) {
        Timer().schedule(unit.toMillis(amount)) {
            resolve()
        }
    }
}

fun <T> Promise<T>.onErrorResumeNext(
    resumeNext: (Throwable) -> Promise<T>
): Promise<T> =
    flatMap(
        { Promise.of(it) },
        { resumeNext(it) })

fun <T> Promise<T>.onErrorResumeNextValue(
    resumeNext: (Throwable) -> T
): Promise<T> = onErrorResumeNext { Promise.of(resumeNext(it)) }

fun <T> Promise<T>.onErrorResumeNextError(
    resumeNext: (Throwable) -> Throwable
): Promise<T> = onErrorResumeNext { Promise.error(resumeNext(it)) }

fun <T, ErrorType : Throwable> Promise<T>.onErrorResumeNext(
    error: Class<ErrorType>,
    resumeNext: (Throwable) -> Promise<T>
): Promise<T> =
    onErrorResumeNext {
        if (it.javaClass == error) resumeNext(it)
        else Promise.error(it)
    }
