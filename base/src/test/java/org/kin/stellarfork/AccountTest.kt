package org.kin.stellarfork

import org.junit.Assert
import org.junit.Test
import org.kin.stellarfork.KeyPair.Companion.random

class AccountTest {
    @Test
    fun testGetIncrementedSequenceNumber() {
        val (_, sequenceNumber, incrementedSequenceNumber) = Account(random(), 100L)
        var incremented: Long
        incremented = incrementedSequenceNumber
        Assert.assertEquals(100L, sequenceNumber)
        Assert.assertEquals(101L, incremented)
        incremented = incrementedSequenceNumber
        Assert.assertEquals(100L, sequenceNumber)
        Assert.assertEquals(101L, incremented)
    }

    @Test
    fun testIncrementSequenceNumber() {
        val account =
            Account(random(), 100L)
        account.incrementSequenceNumber()
        Assert.assertEquals(account.sequenceNumber, 101L)
    }

    @Test
    fun testGetters() {
        val keypair = random()
        val (keypair1, sequenceNumber) = Account(keypair, 100L)
        Assert.assertEquals(keypair1.accountId, keypair.accountId)
        Assert.assertEquals(sequenceNumber, 100L)
    }
}
