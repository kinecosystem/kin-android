package org.kin.sdk.base.models

import org.junit.Test
import org.kin.sdk.base.tools.toByteArray
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KinBinaryMemoTest {

    @Test
    fun testAgoraEncoding_specicFK() {
        val agoraMemo = KinBinaryMemo.Builder(10, 1, 2)
            .setTranferType(KinBinaryMemo.TransferType.P2P)
            .setForeignKey(byteArrayOf(0xAE.toByte(), 0xFD.toByte()))
            .build()

        val bytes: ByteArray = agoraMemo.encode()

        with(KinBinaryMemo.decode(bytes)) {
            assertTrue { agoraMemo.foreignKeyBytes.contentEquals(this.foreignKeyBytes) }
        }
    }

    @Test
    fun testAgoraEncoding_validFK_lessThanMax() {

        // FK Less than max
        (0..500).forEach {
            val agoraMemo = KinBinaryMemo.Builder(10, 1, 2)
                .setTranferType(KinBinaryMemo.TransferType.P2P)
                .setForeignKey(UUID.randomUUID().toByteArray())
                .build()

            val bytes: ByteArray = agoraMemo.encode()

            with(KinBinaryMemo.decode(bytes)) {
                assertTrue { agoraMemo.foreignKeyBytes.contentEquals(this.foreignKeyBytes) }
            }
        }
    }

    @Test
    fun testAgoraEncoding_validFK_atOrLargerThanMax() {
        // FK at/larger than max (which gets truncated)
        (0..500).forEach {
            val agoraMemo = KinBinaryMemo.Builder(10, 1, 2)
                .setTranferType(KinBinaryMemo.TransferType.P2P)
                .setForeignKey(UUID.randomUUID().toByteArray() + UUID.randomUUID().toByteArray())
                .build()

            val bytes: ByteArray = agoraMemo.encode()

            with(KinBinaryMemo.decode(bytes)) {
                assertTrue { agoraMemo.foreignKeyBytes.contentEquals(this.foreignKeyBytes) }
            }
        }
    }

    @Test
    fun testAgoraEncoding_appIdx_validRange() {
        (0..65535).forEach { index ->
            val agoraMemo = KinBinaryMemo.Builder(index, 3, 7)
                .setTranferType(KinBinaryMemo.TransferType.P2P)
                .setForeignKey(UUID.randomUUID().toByteArray() + UUID.randomUUID().toByteArray())
                .build()

            val bytes: ByteArray = agoraMemo.encode()
            with(KinBinaryMemo.decode(bytes)) {

                assertEquals(index, appIdx)
                assertEquals(3, magicByteIndicator)
                assertEquals(7, version)
                assertEquals(KinBinaryMemo.TransferType.P2P, typeId)
                assertTrue { agoraMemo.foreignKeyBytes.contentEquals(this.foreignKeyBytes) }
            }
        }
    }

    @Test(expected = KinBinaryMemo.Builder.KinBinaryMemoFormatException::class)
    fun testAgoraEncoding_appIdx_invalid_tooSmall() {
        KinBinaryMemo.Builder(appIdx = -1, magicByteIndicator = 1, version = 7)
            .setTranferType(KinBinaryMemo.TransferType.P2P)
            .setForeignKey(UUID.randomUUID().toByteArray() + UUID.randomUUID().toByteArray())
            .build()
    }

    @Test(expected = KinBinaryMemo.Builder.KinBinaryMemoFormatException::class)
    fun testAgoraEncoding_appIdx_invalid_tooLarge() {
        KinBinaryMemo.Builder(65536, 1, 2)
            .setTranferType(KinBinaryMemo.TransferType.P2P)
            .setForeignKey(UUID.randomUUID().toByteArray() + UUID.randomUUID().toByteArray())
            .build()
    }

    @Test(expected = KinBinaryMemo.Builder.KinBinaryMemoFormatException::class)
    fun testAgoraEncoding_typeId_null() {
        KinBinaryMemo.Builder(65536, 1, 2)
            .setForeignKey(UUID.randomUUID().toByteArray() + UUID.randomUUID().toByteArray())
            .build()
    }

    @Test
    fun testAgoraEncoding_no_fk_default_0s() {
        val memo = KinBinaryMemo.Builder(123, 1, 2)
            .setTranferType(KinBinaryMemo.TransferType.P2P)
            .build()

        assertTrue {
            byteArrayOf(
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0
            ).contentEquals(memo.foreignKeyBytes)
        }
    }

    @Test
    fun testAgoraEncoding_magicByteIndicator_validRange() {
        (0..3).forEach { index ->
            val agoraMemo = KinBinaryMemo.Builder(65535, index, 7)
                .setTranferType(KinBinaryMemo.TransferType.P2P)
                .setForeignKey(UUID.randomUUID().toByteArray() + UUID.randomUUID().toByteArray())
                .build()

            val bytes: ByteArray = agoraMemo.encode()
            with(KinBinaryMemo.decode(bytes)) {

                assertEquals(65535, appIdx)
                assertEquals(index, magicByteIndicator)
                assertEquals(7, version)
                assertEquals(KinBinaryMemo.TransferType.P2P, typeId)
                assertTrue { agoraMemo.foreignKeyBytes.contentEquals(this.foreignKeyBytes) }
            }
        }
    }

    @Test(expected = KinBinaryMemo.Builder.KinBinaryMemoFormatException::class)
    fun testAgoraEncoding_magicByteIndicator_invalid_tooSmall() {
        KinBinaryMemo.Builder(65535, -1, 7)
            .setTranferType(KinBinaryMemo.TransferType.P2P)
            .setForeignKey(UUID.randomUUID().toByteArray() + UUID.randomUUID().toByteArray())
            .build()
    }

    @Test(expected = KinBinaryMemo.Builder.KinBinaryMemoFormatException::class)
    fun testAgoraEncoding_magicByteIndicator_invalid_tooLarge() {
        KinBinaryMemo.Builder(65535, 3, 8)
            .setTranferType(KinBinaryMemo.TransferType.P2P)
            .setForeignKey(UUID.randomUUID().toByteArray() + UUID.randomUUID().toByteArray())
            .build()
    }

    @Test
    fun testAgoraEncoding_version_validRange() {
        (0..7).forEach { index ->
            val agoraMemo = KinBinaryMemo.Builder(65535, 3, index)
                .setTranferType(KinBinaryMemo.TransferType.P2P)
                .setForeignKey(UUID.randomUUID().toByteArray() + UUID.randomUUID().toByteArray())
                .build()

            val bytes: ByteArray = agoraMemo.encode()
            with(KinBinaryMemo.decode(bytes)) {

                assertEquals(65535, appIdx)
                assertEquals(3, magicByteIndicator)
                assertEquals(index, version)
                assertEquals(KinBinaryMemo.TransferType.P2P, typeId)
                assertTrue { agoraMemo.foreignKeyBytes.contentEquals(this.foreignKeyBytes) }
            }
        }
    }

    @Test(expected = KinBinaryMemo.Builder.KinBinaryMemoFormatException::class)
    fun testAgoraEncoding_version_invalid_tooSmall() {
        KinBinaryMemo.Builder(65535, 3, -1)
            .setTranferType(KinBinaryMemo.TransferType.P2P)
            .setForeignKey(UUID.randomUUID().toByteArray() + UUID.randomUUID().toByteArray())
            .build()
    }

    @Test(expected = KinBinaryMemo.Builder.KinBinaryMemoFormatException::class)
    fun testAgoraEncoding_version_invalid_tooLarge() {
        KinBinaryMemo.Builder(65535, 3, 8)
            .setTranferType(KinBinaryMemo.TransferType.P2P)
            .setForeignKey(UUID.randomUUID().toByteArray() + UUID.randomUUID().toByteArray())
            .build()
    }

    @Test
    fun testAgoraEncoding_typeId_validRange() {

        fun verifyTypeId(inputTypeId: KinBinaryMemo.TransferType) {
            val agoraMemo = KinBinaryMemo.Builder(65535, 3, 7)
                .setTranferType(inputTypeId)
                .setForeignKey(UUID.randomUUID().toByteArray() + UUID.randomUUID().toByteArray())
                .build()

            val bytes: ByteArray = agoraMemo.encode()
            with(KinBinaryMemo.decode(bytes)) {

                assertEquals(65535, appIdx)
                assertEquals(3, magicByteIndicator)
                assertEquals(7, version)
                assertEquals(inputTypeId, typeId)
                assertTrue { agoraMemo.foreignKeyBytes.contentEquals(this.foreignKeyBytes) }
            }
        }

        verifyTypeId(KinBinaryMemo.TransferType.None)
        verifyTypeId(KinBinaryMemo.TransferType.Earn)
        verifyTypeId(KinBinaryMemo.TransferType.Spend)
        verifyTypeId(KinBinaryMemo.TransferType.P2P)

    }

    @Test(expected = KinBinaryMemo.Builder.KinBinaryMemoFormatException::class)
    fun testAgoraEncoding_typeId_invalid_tooSmall() {
        KinBinaryMemo.Builder(65535, 3, -1)
            .setTranferType(KinBinaryMemo.TransferType.ANY(-1))
            .setForeignKey(UUID.randomUUID().toByteArray() + UUID.randomUUID().toByteArray())
            .build()
    }

    @Test(expected = KinBinaryMemo.Builder.KinBinaryMemoFormatException::class)
    fun testAgoraEncoding_typeId_invalid_tooLarge() {
        KinBinaryMemo.Builder(65535, 3, 8)
            .setTranferType(KinBinaryMemo.TransferType.ANY(32))
            .setForeignKey(UUID.randomUUID().toByteArray() + UUID.randomUUID().toByteArray())
            .build()
    }
}
