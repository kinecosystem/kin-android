package org.kin.sdk.base.models

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class KinMemoTest {

    @Test
    fun testEquality() {
        assertEquals(KinMemo("aMemo"), KinMemo("aMemo"))
        assertEquals(KinMemo("aMemo").hashCode(), KinMemo("aMemo").hashCode())
        val memo = KinMemo("aMemo")
        assertEquals(memo, memo)
        assertNotEquals(KinMemo("aMemo"), KinMemo("different"))
        assertNotEquals(KinMemo("aMemo").hashCode(), KinMemo("different").hashCode())
    }
}
