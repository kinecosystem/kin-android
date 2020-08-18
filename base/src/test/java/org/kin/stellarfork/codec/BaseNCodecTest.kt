/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.kin.stellarfork.codec

import org.junit.Assert
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.kin.stellarfork.codec.BaseNCodec.Companion.isWhiteSpace

class BaseNCodecTest {
    var codec: BaseNCodec? = null
    @Before
    fun setUp() {
        codec = object : BaseNCodec(0, 0, 0, 0) {
            override fun decode(pArray: ByteArray, i: Int, length: Int) {}
            override fun encode(pArray: ByteArray, i: Int, length: Int) {}
            override fun isInAlphabet(value: Byte): Boolean {
                return value == 'O'.toByte() || value == 'K'.toByte() // allow OK
            }
        }
    }

    @Test
    fun testBaseNCodec() {
        Assert.assertNotNull(codec)
    }

    @Test
    fun testIsWhiteSpace() {
        Assert.assertTrue(isWhiteSpace(' '.toByte()))
        Assert.assertTrue(isWhiteSpace('\n'.toByte()))
        Assert.assertTrue(isWhiteSpace('\r'.toByte()))
        Assert.assertTrue(isWhiteSpace('\t'.toByte()))
    }

    @Test
    fun testIsInAlphabetByte() {
        Assert.assertFalse(codec!!.isInAlphabet(0.toByte()))
        Assert.assertFalse(codec!!.isInAlphabet('a'.toByte()))
        Assert.assertTrue(codec!!.isInAlphabet('O'.toByte()))
        Assert.assertTrue(codec!!.isInAlphabet('K'.toByte()))
    }

    @Test
    fun testIsInAlphabetByteArrayBoolean() {
        Assert.assertTrue(codec!!.isInAlphabet(byteArrayOf(), false))
        Assert.assertTrue(codec!!.isInAlphabet(byteArrayOf('O'.toByte()), false))
        Assert.assertFalse(
            codec!!.isInAlphabet(
                byteArrayOf('O'.toByte(), ' '.toByte()),
                false
            )
        )
        Assert.assertFalse(codec!!.isInAlphabet(byteArrayOf(' '.toByte()), false))
        Assert.assertTrue(codec!!.isInAlphabet(byteArrayOf(), true))
        Assert.assertTrue(codec!!.isInAlphabet(byteArrayOf('O'.toByte()), true))
        Assert.assertTrue(
            codec!!.isInAlphabet(
                byteArrayOf('O'.toByte(), ' '.toByte()),
                true
            )
        )
        Assert.assertTrue(codec!!.isInAlphabet(byteArrayOf(' '.toByte()), true))
    }

    @Test
    fun testIsInAlphabetString() {
        Assert.assertTrue(codec!!.isInAlphabet("OK"))
        Assert.assertTrue(codec!!.isInAlphabet("O=K= \t\n\r"))
    }

    @Test
    fun testContainsAlphabetOrPad() {
        Assert.assertFalse(codec!!.containsAlphabetOrPad(null))
        Assert.assertFalse(codec!!.containsAlphabetOrPad(byteArrayOf()))
        Assert.assertTrue(codec!!.containsAlphabetOrPad("OK".toByteArray()))
        Assert.assertTrue(codec!!.containsAlphabetOrPad("OK ".toByteArray()))
        Assert.assertFalse(codec!!.containsAlphabetOrPad("ok ".toByteArray()))
        //        assertTrue(codec.containsAlphabetOrPad(new byte[]{codec.pad}));
    }

    companion object {
        /**
         * Verify this VM can allocate the given size byte array. Otherwise skip the test.
         */
        private fun assumeCanAllocateBufferSize(size: Int) {
            var bytes: ByteArray? = null
            try {
                bytes = ByteArray(size)
            } catch (ignore: OutOfMemoryError) { // ignore
            }
            Assume.assumeTrue("Cannot allocate array of size: $size", bytes != null)
        }

        /**
         * Gets the presumable free memory; an estimate of the amount of memory that could be allocated.
         *
         *
         * This performs a garbage clean-up and the obtains the presumed amount of free memory
         * that can be allocated in this VM. This is computed as:
         *
         *
         *
         * <pre>
         * System.gc();
         * long allocatedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
         * long presumableFreeMemory = Runtime.getRuntime().maxMemory() - allocatedMemory;
        </pre> *
         *
         * @return the presumable free memory
         * @see [
         * Christian Fries StackOverflow answer on Java available memory](https://stackoverflow.com/a/18366283)
         */
        val presumableFreeMemory: Long
            get() {
                System.gc()
                val allocatedMemory =
                    Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
                return Runtime.getRuntime().maxMemory() - allocatedMemory
            }
    }
}
