package org.kin.sdk.base.tools

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assume
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.kin.sdk.base.tools.Base58.AddressFormatException
import org.kin.sdk.base.tools.Base58.AddressFormatException.InvalidChecksum
import org.kin.sdk.base.tools.Base58.AddressFormatException.InvalidDataLength
import org.kin.sdk.base.tools.Base58.decode
import org.kin.sdk.base.tools.Base58.decodeChecked
import org.kin.sdk.base.tools.Base58.decodeToBigInteger
import org.kin.sdk.base.tools.Base58.encode
import org.kin.sdk.base.tools.Base58.encodeChecked
import java.math.BigInteger
import java.util.Arrays

class Base58DecodeCheckedInvalidChecksumTest {
    @Test(expected = InvalidChecksum::class)
    fun testDecodeChecked_invalidChecksum() {
        decodeChecked("4stwEBjT6FYyVW")
    }
}

@RunWith(Theories::class)
class Base58DecodeCheckedTest {
    @Rule
    @JvmField
    var expectedException: ExpectedException = ExpectedException.none()

    private fun containsOnlyValidBase58Chars(input: String): Boolean {
        for (s in input.split("").toTypedArray()) {
            if (!BASE58_ALPHABET.contains(s)) {
                return false
            }
        }
        return true
    }

    @Theory
    fun testDecodeChecked(input: String) {
        Assume.assumeTrue(containsOnlyValidBase58Chars(input))
        Assume.assumeTrue(input.length > 4)
        decodeChecked(input)
    }

    @Theory
    fun decode_invalidCharacter_notInAlphabet(input: String) {
        Assume.assumeFalse(containsOnlyValidBase58Chars(input))
        Assume.assumeTrue(input.length > 4)
        expectedException.expect(AddressFormatException.InvalidCharacter::class.java)
        decodeChecked(input)
    }

    @Theory
    fun testDecodeChecked_shortInput(input: String) {
        Assume.assumeTrue(containsOnlyValidBase58Chars(input))
        Assume.assumeTrue(input.length < 4)
        expectedException.expect(InvalidDataLength::class.java)
        decodeChecked(input)
    }

    companion object {
        private const val BASE58_ALPHABET =
            "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ"

        @DataPoints
        @JvmField
        var parameters = arrayOf(
            "4stwEBjT6FYyVV",
            "93VYUMzRG9DdbRP72uQXjaWibbQwygnvaCu9DumcqDjGybD864T",
            "J0F12TrwUP45BMd",
            "4s"
        )
    }
}

@RunWith(Parameterized::class)
class Base58DecodeTest(private val input: String, private val expected: ByteArray) {
    @Test
    fun testDecode() {
        val actualBytes = decode(input)
        assertArrayEquals(input, actualBytes, expected)
    }

    @Test
    fun testDecode_emptyString() {
        assertEquals(0, decode("").size)
    }

    @Test(expected = AddressFormatException::class)
    fun testDecode_invalidBase58() {
        decode("This isn't valid base58")
    }

    companion object {
        @Parameterized.Parameters
        @JvmStatic
        fun parameters(): Collection<Array<Any>> {
            return listOf(
                *arrayOf(
                    arrayOf<Any>(
                        "JxF12TrwUP45BMd",
                        "Hello World".toByteArray()
                    ), arrayOf<Any>("1", ByteArray(1)), arrayOf<Any>("1111", ByteArray(4))
                )
            )
        }
    }
}

class Base58DecodeToBigIntegerTest {
    @Test
    fun testDecodeToBigInteger() {
        val input = decode("129")
        assertEquals(BigInteger(1, input), decodeToBigInteger("129"))
    }
}

@RunWith(Parameterized::class)
class Base58EncodeCheckedTest(
    private val version: Int,
    private val input: ByteArray,
    private val expected: String
) {
    @Test
    fun testEncode() {
        assertEquals(expected, encodeChecked(version, input))
    }

    companion object {

        /**
         * An address is a RIPEMD160 hash of a public key, therefore is always 160 bits or 20 bytes.
         */
        const val LegacyAddress_LENGTH = 20

        @Parameterized.Parameters
        @JvmStatic
        fun parameters(): Collection<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf(
                        111,
                        ByteArray(LegacyAddress_LENGTH),
                        "mfWxJ45yp2SFn7UciZyNpvDKrzbhyfKrY8"
                    ),
                    arrayOf(
                        128,
                        ByteArray(32),
                        "5HpHagT65TZzG1PH3CSu63k8DbpvD8s5ip4nEB3kEsreAbuatmU"
                    )
                )
            )
        }
    }
}

@RunWith(Parameterized::class)
class Base58EncodeTest(private val input: ByteArray, private val expected: String) {
    @Test
    fun testEncode() {
        assertEquals(expected, encode(input))
    }

    companion object {
        @Parameterized.Parameters
        @JvmStatic
        fun parameters(): Collection<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf("Hello World".toByteArray(), "JxF12TrwUP45BMd"),
                    arrayOf(
                        BigInteger.valueOf(3471844090L).toByteArray(), "16Ho7Hs"
                    ),
                    arrayOf(ByteArray(1), "1"),
                    arrayOf(ByteArray(7), "1111111"),
                    arrayOf(ByteArray(0), "")
                )
            )
        }
    }
}
