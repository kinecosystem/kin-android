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

import java.io.IOException
import java.io.InputStream
import java.util.Random

/**
 * This random data was encoded by OpenSSL. Java had nothing to do with it. This data helps us test interop between
 * Commons-Codec and OpenSSL. Notice that OpenSSL creates 64 character lines instead of the 76 of Commons-Codec.
 *
 * @see [RFC 2045](http://www.ietf.org/rfc/rfc2045.txt)
 *
 * @since 1.4
 */
object Base32TestData {
    const val STRING_FIXTURE = "Hello World"
    const val BASE32_FIXTURE = "JBSWY3DPEBLW64TMMQ======\r\n"
    //  static final String BASE32HEX_FIXTURE = "91IMOR3F41BMUSJCCG======";
// Some utility code to help test chunked reads of the InputStream.
    private const val SIZE_KEY = 0
    private const val LAST_READ_KEY = 1
    @JvmOverloads
    @Throws(IOException::class)
    fun streamToBytes(`in`: InputStream, buf: ByteArray = ByteArray(7)): ByteArray {
        var buf1 = buf
        try {
            var status = fill(buf1, 0, `in`)
            var size = status[SIZE_KEY]
            var lastRead = status[LAST_READ_KEY]
            while (lastRead != -1) {
                buf1 = resizeArray(buf1)
                status = fill(buf1, size, `in`)
                size = status[SIZE_KEY]
                lastRead = status[LAST_READ_KEY]
            }
            if (buf1.size != size) {
                val smallerBuf = ByteArray(size)
                System.arraycopy(buf1, 0, smallerBuf, 0, size)
                buf1 = smallerBuf
            }
        } finally {
            `in`.close()
        }
        return buf1
    }

    @Throws(IOException::class)
    private fun fill(buf: ByteArray, offset: Int, `in`: InputStream): IntArray {
        var read = `in`.read(buf, offset, buf.size - offset)
        var lastRead = read
        if (read == -1) {
            read = 0
        }
        while (lastRead != -1 && read + offset < buf.size) {
            lastRead = `in`.read(buf, offset + read, buf.size - read - offset)
            if (lastRead != -1) {
                read += lastRead
            }
        }
        return intArrayOf(offset + read, lastRead)
    }

    private fun resizeArray(bytes: ByteArray): ByteArray {
        val biggerBytes = ByteArray(bytes.size * 2)
        System.arraycopy(bytes, 0, biggerBytes, 0, bytes.size)
        return biggerBytes
    }

    /**
     * Returns an encoded and decoded copy of the same random data.
     *
     * @param codec the codec to use
     * @param size amount of random data to generate and encode
     * @return two byte[] arrays:  [0] = decoded, [1] = encoded
     */
    fun randomData(codec: BaseNCodec, size: Int): Array<ByteArray?> {
        val r = Random()
        val decoded = ByteArray(size)
        r.nextBytes(decoded)
        val encoded = codec.encode(decoded)
        return arrayOf(decoded, encoded)
    }

    /**
     * Tests the supplied byte[] array to see if it contains the specified byte c.
     *
     * @param bytes byte[] array to test
     * @param c byte to look for
     * @return true if bytes contains c, false otherwise
     */
    fun bytesContain(bytes: ByteArray, c: Byte): Boolean {
        for (b in bytes) {
            if (b == c) {
                return true
            }
        }
        return false
    }
}
