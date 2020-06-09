package org.kin.sdk.base.models

import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test


class ClassicKinMemoTest {

    val validAppIds = listOf("", "123", "1234", "1f3", "1f3e")
    val validSuffixes = listOf("", "12345678901234567890123456")

    val invalidAppIds = listOf("1", "12", "12!", "!!!", "#$@!", "12345")
    val invalidSuffixes = listOf("123456789012345678901234567")

    @Test
    fun testCases() {

        validSuffixes.forEach { suffix ->
            validAppIds.forEach { appId ->
                println("suffix: $suffix appId: $appId")
                val memo = ClassicKinMemo(appId = AppId(appId), memoSuffix = MemoSuffix(suffix))
                val expectedMemoString = "1-$appId-$suffix"
                assertEquals(expectedMemoString, memo.toString())
                val kinMemo = memo.asKinMemo()
                assertEquals(KinMemo(expectedMemoString), kinMemo)
                assertEquals(expectedMemoString, kinMemo.toString())
            }
        }


        validSuffixes.forEach { suffix ->
            invalidAppIds.forEach { appId ->
                println("suffix: $suffix appId: $appId")
                var exception: Exception? = null
                try {
                    ClassicKinMemo(appId = AppId(appId), memoSuffix = MemoSuffix(suffix))
                } catch (e: Exception) {
                    exception = e
                }

                assertNotNull(exception)
                assertTrue(exception is IllegalArgumentException)
            }
        }

        invalidSuffixes.forEach { suffix ->
            validAppIds.forEach { appId ->
                println("suffix: $suffix appId: $appId")
                var exception: Exception? = null
                try {
                    ClassicKinMemo(appId = AppId(appId), memoSuffix = MemoSuffix(suffix))
                } catch (e: Exception) {
                    exception = e
                }

                assertNotNull(exception)
                assertTrue(exception is IllegalArgumentException)
            }
        }
    }
}
