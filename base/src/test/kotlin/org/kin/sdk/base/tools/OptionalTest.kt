package org.kin.sdk.base.tools

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue


class OptionalTest {

    @Test
    fun testPresentValue() {
        assertFalse(Optional.empty<String>().isPresent)
        assertFalse(Optional.ofNullable(null).isPresent)
        assertTrue(Optional.of("test").isPresent)
        assertTrue(Optional.ofNullable("test2").isPresent)
    }

    @Test
    fun testOrElse() {
        assertEquals("foo", Optional.ofNullable<String>(null).orElse("foo"))
        assertEquals("test", Optional.of("test").orElse("foo"))
    }

    @Test
    fun testMap() {
        assertEquals("6", Optional.of(2).map { "${it * 3}" }.get())
    }

    @Test(expected = RuntimeException::class)
    fun testMapException() {
        Optional.of(2)
            .map { throw IllegalStateException("Well that was expected") }.get()
    }

    @Test
    fun testOptionalEquality() {
        assertEquals(
            Optional.empty(),
            Optional.ofNullable(null).mapNullable { 1 }
        )
        assertEquals(
            Optional.empty<Int>().hashCode(),
            Optional.ofNullable(null).mapNullable { 1 }.hashCode()
        )
        assertEquals(
            Optional.empty<Int>().toString(),
            Optional.ofNullable(null).mapNullable { 1 }.toString()
        )
        assertEquals(
            Optional.ofNullable(null),
            Optional.ofNullable(null)
        )
        assertEquals(
            Optional.ofNullable(null).toString(),
            Optional.ofNullable(null).toString()
        )
        assertEquals(
            Optional.of(1),
            Optional.of(1)
        )
        assertEquals(
            Optional.of(1).toString(),
            Optional.of(1).toString()
        )
        assertEquals(
            Optional.of(1).hashCode(),
            Optional.of(1).hashCode()
        )
        assertNotEquals<Optional<*>>(
            Optional.of(1),
            Optional.of("hello")
        )
        assertNotEquals(
            Optional.of(1).hashCode(),
            Optional.of("hello").hashCode()
        )
    }

    @Test(expected = Exception::class)
    fun testMapNullable_notNull_map_throws() {
        Optional.of(1)
            .mapNullable { throw Exception() }
    }
}
