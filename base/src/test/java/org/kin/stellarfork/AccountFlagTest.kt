package org.kin.stellarfork

import org.junit.Assert
import org.junit.Test

class AccountFlagTest {
    @Test
    fun testValues() {
        Assert.assertEquals(1, AccountFlag.AUTH_REQUIRED_FLAG.value.toLong())
        Assert.assertEquals(2, AccountFlag.AUTH_REVOCABLE_FLAG.value.toLong())
        Assert.assertEquals(4, AccountFlag.AUTH_IMMUTABLE_FLAG.value.toLong())
    }
}
