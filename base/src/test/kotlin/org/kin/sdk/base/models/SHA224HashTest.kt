package org.kin.sdk.base.models

import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.kin.sdk.base.tools.printBits
import org.kin.sdk.base.tools.sha224
import org.kin.sdk.base.tools.toByteArray
import java.util.UUID

class SHA224HashTest {

    lateinit var sut: SHA224Hash

    @Before
    fun setUp() {

    }

    @Test
    fun encodeDecode() {
        val bytes = UUID.randomUUID().toByteArray()
        sut = SHA224Hash.of(bytes)

        bytes.sha224().printBits(collapsed = true)
        sut.decode().printBits(collapsed = true)
        assertTrue(bytes.sha224().contentEquals(sut.decode()))
    }
}
