package org.kin.sdk.base.tools

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Timer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class ValueSubjectTest {
    @Test
    fun testObserverMap() {
        val subject = ValueSubject<Int>()

        subject.onNext(1)

        subject.map { 2 }.test {
            kotlin.test.assertEquals(2, value)
        }
    }

    @Test
    fun testFilter() {
        val subject = ValueSubject<Int>()
        val disposeBag = DisposeBag()
        val values = mutableListOf<Int>()
        val latch = CountDownLatch(3)
        subject.filter { it % 2 == 0 }
            .add {
                values.add(it)
                latch.countDown()
            }.disposedBy(disposeBag)

        (1..6).forEach {
            subject.onNext(it)
        }

        latch.await(5, TimeUnit.SECONDS)
        assertEquals(listOf(2, 4, 6), values)

        disposeBag.dispose()

        assertEquals(0, subject.listenerCount())
    }
}
