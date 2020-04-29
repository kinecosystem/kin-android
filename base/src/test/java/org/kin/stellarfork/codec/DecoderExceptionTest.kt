/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kin.stellarfork.codec

import org.junit.Assert
import org.junit.Test

/**
 * Tests [DecoderException].
 *
 */
class DecoderExceptionTest {
    @Test
    fun testConstructor0() {
        val e =
            DecoderException()
        Assert.assertNull(e.message)
        Assert.assertNull(e.cause)
    }

    @Test
    fun testConstructorString() {
        val e =
            DecoderException(MSG)
        Assert.assertEquals(MSG, e.message)
        Assert.assertNull(e.cause)
    }

    @Test
    fun testConstructorStringThrowable() {
        val e =
            DecoderException(
                MSG,
                t
            )
        Assert.assertEquals(MSG, e.message)
        Assert.assertEquals(t, e.cause)
    }

    @Test
    fun testConstructorThrowable() {
        val e =
            DecoderException(t)
        Assert.assertEquals(
            t.javaClass.name,
            e.message
        )
        Assert.assertEquals(t, e.cause)
    }

    companion object {
        private const val MSG = "TEST"
        private val t: Throwable = Exception()
    }
}
