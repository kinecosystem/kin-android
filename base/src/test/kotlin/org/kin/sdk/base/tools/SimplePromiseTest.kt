package org.kin.sdk.base.tools

import org.junit.Test
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.fail

class PromiseTest {
    companion object {
        val error = Exception("error bigly")
        val nextError = Exception("error again")
    }

    @Test
    fun test() {
        Promise.of(1)
            .map {
                2
            }
            .flatMap { Promise.of("asdf") }
            .flatMap { Promise.of("hhhh") }
            .test {
                assertEquals("hhhh", value)
                assertNull(error)
            }
    }

    @Test
    fun testThrashingResolve() {
        val threads = mutableListOf<Thread>()

        for (i in 1..100) {
            val counter = AtomicInteger(0)

            fun checkCounter(param: Any) {
                val count = counter.incrementAndGet()

                assertEquals(1, count, "Promise resolved $count times $param")
            }

            Promise.create<String> { resolve, reject ->
                val resolveThread = Thread {
                    resolve("ok")
                }
                val rejectThread = Thread {
                    reject(IllegalStateException("rejected"))
                }

                threads += resolveThread
                threads += rejectThread

                resolveThread.start()
                rejectThread.start()
            }.then(::checkCounter, ::checkCounter)
        }

        threads.forEach { it.join() }
    }

    @Test
    fun testAll() {
        Promise.allAny(Promise.of("asdf"), Promise.of(1))
            .test {
                value?.forEach(::println)
                assertEquals(listOf("asdf", 1), value)
                assertNull(error)
            }
    }

    @Test
    fun testNoDoubleWork() {
        val workCount = AtomicInteger(0)
        val promise = Promise.create<Int> { resolve, _ ->
            val count = workCount.incrementAndGet()

            if (count > 1) {
                throw IllegalStateException("Work attempted $count times")
            }

            resolve(count)
        }

        promise.then { assertEquals(1, it) }
        promise.then { assertEquals(1, it) }
    }

    @Test
    fun testFailure_throwError() {
        Promise.create<Int> { _, _ ->
            throw error
        }.test {
            assertNotNull(error)
            assertNull(value)
        }
    }

    @Test
    fun testFailure() {
        Promise.error<Exception>(error)
            .test {
                assertNotNull(error)
                assertNull(value)
            }
    }

    @Test
    fun testThrowingDuringFlatMap() {
        Promise.of(1)
            .flatMap<Int> {
                throw error
            }
            .test {
                assertNotNull(error)
                assertNull(value)
            }
    }

    @Test
    fun testMapRejected() {
        Promise.error<Int>(error)
            .map<Int> {
                fail("should never get called in rejection path")
            }
            .test {
                assertNotNull(error)
            }
    }

    @Test
    fun testMapThrows() {
        Promise.of(1)
            .map<Int> {
                throw error
            }
            .test {
                assertNotNull(error)
            }
    }

    @Test
    fun testMapThrowsDuringReject() {
        Promise.error<Int>(error)
            .map({ it }, {
                throw nextError
            })
            .test {
                assertEquals(nextError, error)
            }
    }

    @Test
    fun testFlatMapFail() {
        Promise.error<Int>(error)
            .flatMap<Int>({
                throw nextError
            }, {
                Promise.of(2)
            })
            .test {
                assertEquals(2, value)
            }
    }

    @Test
    fun testThrowingDuringFlatMapFail() {
        Promise.error<Int>(error)
            .flatMap<Int>({
                Promise.of(1)
            }, {
                throw nextError
            })
            .test {
                assertEquals(error, nextError)
                assertNull(value)
            }
    }

    @Test
    fun testFailureInFlatMap() {
        Promise.of(1)
            .flatMap { Promise.error<Exception>(error) }
            .test {
                assertNotNull(error)
                assertNull(value)
            }
    }

    @Test
    fun testResolveWhileResolving() {
        val promise = Promise.create<Int> { resolve, _ ->
            resolve(1)
        }

        promise.then({ value1 ->
            assertEquals(1, value1)

            var value2Update: Int? = null

            // this should be called immediately because the promise should be in the resolved state
            promise.then { value2 ->
                value2Update = value2
            }

            assertEquals(value1, value2Update)
        }, { })
    }

    @Test
    fun testWorkOn() {
        Promise.of(1)
            .workOn(Executors.newSingleThreadExecutor())
            .flatMap {
                Promise.create<Int> { resolve, _ ->
                    Executors.newSingleThreadExecutor().submit { resolve(2) }
                }
            }
            .map { 4 }
            .test {
                assertEquals(4, value)
            }
    }

    @Test
    fun testPromiseQueue_success() {
        val q = PromiseQueue<Int>()
        latchOperationValueCapture<Int>(3) { capture ->
            q.queue(Promise.of(1)).then(capture, capture)
            q.queue(Promise.of(2)).then(capture, capture)
            q.queue(Promise.of(3)).then(capture, capture)
        }.test {
            assertEquals(listOf(1, 2, 3), values)
        }
    }

    @Test
    fun testPromiseQueue_many_queued_success() {
        val q = PromiseQueue<Int>()
        latchOperationValueCapture<Int>(1000) { capture ->
            (0..1000).forEach { i ->
                q.queue(Promise.of(i)).then(capture, capture)
            }
        }.test {
            assertEquals((0..1000).toSet(), values.toSet())
        }
    }

    @Test
    fun testPromiseQueue_propagateErrorFromThen() {
        val q = PromiseQueue<Int>()
        latchOperationValueCapture<Int>(3) { capture ->
            q.queue(Promise.of(1)).then(capture, capture)
            q.queue(Promise.of(2)).then(capture, capture)
            q.queue(Promise.create { _, reject ->
                reject(Exception())
            }).then(capture, capture)
        }.test {
            assertEquals(listOf(1, 2), values)
            assertNotNull(error)
        }
    }

    @Test
    fun testPromiseQueue_propagateErrorFromThrown() {
        val q = PromiseQueue<Int>()
        latchOperationValueCapture<Int>(2) { capture ->
            q.queue(Promise.of(1)).then(capture, capture)
            q.queue(Promise.of(2)).then(capture, capture)
            q.queue(Promise.create { _, _ ->
                throw Exception()
            }).then(capture, capture)
        }.test {
            assertEquals(listOf(1, 2), values)
            assertNotNull(error)
        }
    }
}
