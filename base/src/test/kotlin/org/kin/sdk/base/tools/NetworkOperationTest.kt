package org.kin.sdk.base.tools

import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class NetworkOperationTest {

    companion object {
        fun testBackupStrategy() = BackoffStrategy.Exponential(
            initial = 10, // 10 millis
            multiplier = 2.0,
            jitter = 0.5,
            maximumWaitTime = 5000, // 5 Seconds
            maxAttempts = 3
        )
    }

    lateinit var sut: NetworkOperationsHandler

    @Before
    fun setUp() {
        sut =
            NetworkOperationsHandlerImpl(
                shouldRetryError = { true },
                logger = KinLoggerFactoryImpl(true)
            )
    }

    @Test
    fun testNoRetryComplete() {
        val values = mutableListOf<Int>()
        latchOperation(3, timeoutSeconds = 10) { latch ->
            val onCompleted: (Int) -> Unit = {
                values.add(it)
                latch.countDown()
            }
            sut.queueOperation(
                NetworkOperation(
                    onCompleted,
                    backoffStrategy = testBackupStrategy()
                ) { callback, _ -> callback(1) })
            sut.queueOperation(
                NetworkOperation(
                    onCompleted,
                    backoffStrategy = testBackupStrategy()
                ) { callback, _ -> callback(2) })
            sut.queueOperation(
                NetworkOperation(
                    onCompleted,
                    backoffStrategy = testBackupStrategy()
                ) { callback, _ -> callback(3) })
        }
        assertEquals(listOf(1, 2, 3), values)
    }

    @Test
    fun testRetry() {
        val values = mutableSetOf<Int>()
        latchOperation(3, timeoutSeconds = 10) { latch ->
            val onCompleted: (Int) -> Unit = {
                synchronized(values) {
                    values.add(it)
                    latch.countDown()
                }
            }
            var i = 1
            sut.queueOperation(
                NetworkOperation(
                    onCompleted,
                    id = "1",
                    backoffStrategy = testBackupStrategy()
                ) { callback, _ ->
                    if (i++ % 3 != 0) throw RuntimeException()
                    else callback(1)
                })
            sut.queueOperation(
                NetworkOperation(
                    onCompleted,
                    id = "2",
                    backoffStrategy = testBackupStrategy()
                ) { callback, _ -> callback(2) })
            sut.queueOperation(
                NetworkOperation(
                    onCompleted,
                    id = "3",
                    backoffStrategy = testBackupStrategy()
                ) { callback, _ -> callback(3) })
        }
        assertEquals(setOf(1, 2, 3), values)
    }

    @Test
    fun testRetry_never() {
        val values = mutableSetOf<Int>()
        latchOperation(2, timeoutSeconds = 10) { latch ->
            val onCompleted: (Int) -> Unit = {
                synchronized(values) {
                    values.add(it)
                    latch.countDown()
                }
            }
            var i = 1
            sut.queueOperation(
                NetworkOperation(
                    onCompleted,
                    id = "1",
                    backoffStrategy = BackoffStrategy.Never()
                ) { callback, _ ->
                    if (i++ % 3 != 0) throw RuntimeException()
                    else callback(1)
                })
            sut.queueOperation(
                NetworkOperation(
                    onCompleted,
                    id = "2",
                    backoffStrategy = BackoffStrategy.Never()
                ) { callback, _ -> callback(2) })
            sut.queueOperation(
                NetworkOperation(
                    onCompleted,
                    id = "3",
                    backoffStrategy = BackoffStrategy.Never()
                ) { callback, _ -> callback(3) })
        }
        assertEquals(setOf(2, 3), values)
    }

    @Test
    fun testRetry_fixed() {
        val values = mutableSetOf<Int>()
        latchOperation(3, timeoutSeconds = 10) { latch ->
            val onCompleted: (Int) -> Unit = {
                synchronized(values) {
                    values.add(it)
                    latch.countDown()
                }
            }
            var i = 1
            sut.queueOperation(
                NetworkOperation(
                    onCompleted,
                    id = "1",
                    backoffStrategy = BackoffStrategy.Fixed(1)
                ) { callback, _ ->
                    if (i++ % 3 != 0) throw RuntimeException()
                    else callback(1)
                })
            sut.queueOperation(
                NetworkOperation(
                    onCompleted,
                    id = "2",
                    backoffStrategy = BackoffStrategy.Fixed(1)
                ) { callback, _ -> callback(2) })
            sut.queueOperation(
                NetworkOperation(
                    onCompleted,
                    id = "3",
                    backoffStrategy = BackoffStrategy.Fixed(1)
                ) { callback, _ -> callback(3) })
        }
        assertEquals(setOf(1, 2, 3), values)
    }

    @Test
    fun testRetry_custom() {
        val values = mutableSetOf<Int>()
        latchOperation(3, timeoutSeconds = 10) { latch ->
            val onCompleted: (Int) -> Unit = {
                synchronized(values) {
                    values.add(it)
                    latch.countDown()
                }
            }
            var i = 1
            sut.queueOperation(
                NetworkOperation(
                    onCompleted,
                    id = "1",
                    backoffStrategy = BackoffStrategy.Custom({ 5 }, {})
                ) { callback, _ ->
                    if (i++ % 3 != 0) throw RuntimeException()
                    else callback(1)
                })
            sut.queueOperation(
                NetworkOperation(
                    onCompleted,
                    id = "2",
                    backoffStrategy = BackoffStrategy.Custom({ 5 }, {})
                ) { callback, _ -> callback(2) })
            sut.queueOperation(
                NetworkOperation(
                    onCompleted,
                    id = "3",
                    backoffStrategy = BackoffStrategy.Custom({ 5 }, {})
                ) { callback, _ -> callback(3) })
        }
        assertEquals(setOf(1, 2, 3), values)
    }

    @Test
    fun testRetry_exponentialIncrease() {
        val values = mutableSetOf<Int>()
        latchOperation(3, timeoutSeconds = 10) { latch ->
            val onCompleted: (Int) -> Unit = {
                synchronized(values) {
                    values.add(it)
                    latch.countDown()
                }
            }
            var i = 1
            sut.queueOperation(
                NetworkOperation(
                    onCompleted,
                    id = "1",
                    backoffStrategy = BackoffStrategy.ExponentialIncrease(
                        initial = 10, // 10 millis
                        multiplier = 2.0,
                        jitter = 0.5,
                        maximumWaitTime = 5000, // 5 Seconds
                        maxAttempts = 3
                    )
                ) { callback, _ ->
                    if (i++ % 3 != 0) throw RuntimeException()
                    else callback(1)
                })
            sut.queueOperation(
                NetworkOperation(
                    onCompleted,
                    id = "2",
                    backoffStrategy = BackoffStrategy.ExponentialIncrease(
                        initial = 10, // 10 millis
                        multiplier = 2.0,
                        jitter = 0.5,
                        maximumWaitTime = 5000, // 5 Seconds
                        maxAttempts = 3
                    )
                ) { callback, _ -> callback(2) })
            sut.queueOperation(
                NetworkOperation(
                    onCompleted,
                    id = "3",
                    backoffStrategy = BackoffStrategy.ExponentialIncrease(
                        initial = 10, // 10 millis
                        multiplier = 2.0,
                        jitter = 0.5,
                        maximumWaitTime = 5000, // 5 Seconds
                        maxAttempts = 3
                    )
                ) { callback, _ -> callback(3) })
        }
        assertEquals(setOf(1, 2, 3), values)
    }

    @Test
    fun testRetry_strategyCombine() {
        val values = mutableSetOf<Int>()
        latchOperation(3, timeoutSeconds = 10) { latch ->
            val onCompleted: (Int) -> Unit = {
                synchronized(values) {
                    values.add(it)
                    latch.countDown()
                }
            }
            var i = 1
            sut.queueOperation(
                NetworkOperation(
                    onCompleted,
                    id = "1",
                    backoffStrategy = BackoffStrategy.combine(BackoffStrategy.Custom({ 5 }, {}), BackoffStrategy.ExponentialIncrease(
                        initial = 10, // 10 millis
                        multiplier = 2.0,
                        jitter = 0.5,
                        maximumWaitTime = 5000, // 5 Seconds
                        maxAttempts = 3
                    ))
                ) { callback, _ ->
                    if (i++ % 3 != 0) throw RuntimeException()
                    else callback(1)
                })
            sut.queueOperation(
                NetworkOperation(
                    onCompleted,
                    id = "2",
                    backoffStrategy = BackoffStrategy.ExponentialIncrease(
                        initial = 10, // 10 millis
                        multiplier = 2.0,
                        jitter = 0.5,
                        maximumWaitTime = 5000, // 5 Seconds
                        maxAttempts = 3
                    )
                ) { callback, _ -> callback(2) })
            sut.queueOperation(
                NetworkOperation(
                    onCompleted,
                    id = "3",
                    backoffStrategy = BackoffStrategy.ExponentialIncrease(
                        initial = 10, // 10 millis
                        multiplier = 2.0,
                        jitter = 0.5,
                        maximumWaitTime = 5000, // 5 Seconds
                        maxAttempts = 3
                    )
                ) { callback, _ -> callback(3) })
        }
        assertEquals(setOf(1, 2, 3), values)
    }

    @Test
    fun testRetryMaxAttemptsFailed() {
        val maxAttempts = 3
        var successAttempts = 0
        var didTimeout = false
        val onCompleted: (Int) -> Unit = {
            successAttempts++
        }
        latchOperation(maxAttempts, timeoutSeconds = 10) { latch ->
            sut.queueOperation(
                NetworkOperation(
                    onCompleted, {
                        if (it is NetworkOperationsHandlerException.OperationTimeoutException) didTimeout =
                            true
                    }, timeout = 20000,
                    backoffStrategy = BackoffStrategy.Exponential(
                        initial = 10, // 10 millis base
                        multiplier = 2.0,
                        jitter = 0.5,
                        maximumWaitTime = 5000, // 5 Seconds
                        maxAttempts = maxAttempts
                    )
                ) { _, _ ->
                    throw RuntimeException()
                        .also { latch.countDown() }
                }
            )
        }
        assertEquals(0, successAttempts)
        assertFalse { didTimeout }
    }

    @Test
    fun testRetryAndTimeout() {
        var successAttempts = 0
        val onCompleted: (Int) -> Unit = {
            successAttempts++
        }
        latchOperation(1, timeoutSeconds = 10) { latch ->
            sut.queueOperation(NetworkOperation(
                onCompleted, {
                    if (it is NetworkOperationsHandlerException.OperationTimeoutException) {
                        latch.countDown()
                    }
                },
                timeout = 2000
            ) { _, _ ->
                throw RuntimeException()
            })
        }
        assertEquals(0, successAttempts)
    }
}
