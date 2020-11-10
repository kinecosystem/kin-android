package org.kin.sdk.base.models

import org.kin.sdk.base.tools.byteArrayToInt
import org.kin.sdk.base.tools.intToByteArray
import org.kin.sdk.base.tools.shl
import org.kin.sdk.base.tools.subByteArray
import org.kin.sdk.base.tools.ushr
import org.kin.stellarfork.codec.Base64
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.pow

/**
 * A binary Kin memo format.
 *
 * @see the spec for format details https://github.com/kinecosystem/agora-api-internal/blob/master/spec/memo.md
 *
 * @param magicByteIndicator    2 bits   | < 4
 * @param version               3 bits   | < 8
 * @param typeId                5 bits   | < 32
 * @param appIdx                16 bits  | < 65,536
 * @param foreignKey            230 bits | Base64 Encoded String of [230 bits + (2 zeros padding)]
 */
data class KinBinaryMemo internal constructor(
    val magicByteIndicator: Int,
    val version: Int,
    val typeId: TransferType,
    val appIdx: Int,
    val foreignKey: String
) {
    sealed class TransferType(val value: Int) {
        /**
         * An unclassified transfer of Kin.
         */
        object Unknown : TransferType(-1)

        /**
         * When none of the other types are appropriate for the use case.
         */
        object None : TransferType(0)

        /**
         * Use when transferring Kin to a user for some performed action.
         */
        object Earn : TransferType(1)

        /**
         * Use when transferring Kin due to purchasing something.
         */
        object Spend : TransferType(2)

        /**
         * Use when transferring Kin where it does not constitute an [Earn] or [Spend]
         */
        object P2P : TransferType(3)

        /* Shouldn't be used. */
        internal data class ANY internal constructor(val anyValue: Int) : TransferType(anyValue)

        companion object {
            fun fromValue(value: Int): TransferType =
                when (value) {
                    0 -> None
                    1 -> Earn
                    2 -> Spend
                    3 -> P2P
                    else -> Unknown
                }
        }
    }

    data class Builder @JvmOverloads constructor(
        val appIdx: Int,
        val magicByteIndicator: Int = 0x1,
        val version: Int = 0
    ) {
        data class KinBinaryMemoFormatException(override val message: String) :
            RuntimeException(message)

        private var typeId: TransferType? = null
        private var foreignKeyBytes: ByteArray? = null

        fun setTranferType(typeId: TransferType): Builder =
            also { this.typeId = typeId }

        fun setForeignKey(foreignKeyBytes: ByteArray): Builder =
            also { this.foreignKeyBytes = foreignKeyBytes }

        fun build(): KinBinaryMemo {
            if (typeId == null) {
                throw KinBinaryMemoFormatException("typeId must not be null!")
            }

            if (foreignKeyBytes == null) {
                foreignKeyBytes = ByteArray(0)
            }

            val typeIdMaxSize = (2f.pow(5)).toInt()
            if (typeId!!.value < 0 || typeId!!.value >= typeIdMaxSize) {
                throw KinBinaryMemoFormatException(
                    "typeId of ${typeId!!.value} invalid! must be " +
                            "larger than zero and less than $typeIdMaxSize"
                )
            }

            val appIdxMaxSize = (2f.pow(16)).toInt()
            if (appIdx < 0 || appIdx >= appIdxMaxSize) {
                throw KinBinaryMemoFormatException(
                    "appIdx of $appIdx invalid! must be " +
                            "larger than zero and less than $appIdxMaxSize"
                )
            }

            val magicByteIndicatorMaxSize = (2f.pow(2)).toInt()
            if (magicByteIndicator < 0 || magicByteIndicator >= magicByteIndicatorMaxSize) {
                throw KinBinaryMemoFormatException(
                    "magicByteIndicator of $magicByteIndicator invalid! must be" +
                            "larger than zero and less than $magicByteIndicatorMaxSize"
                )
            }

            val versionMaxSize = (2f.pow(3)).toInt()
            if (version < 0 || version >= versionMaxSize) {
                throw KinBinaryMemoFormatException(
                    "version of $version invalid! must be " +
                            "larger than zero and less than $versionMaxSize"
                )
            }

            // Pad with zeros or truncate foreignKeyBytes
            val foreignKeyBytesPadded = ByteArray(BYTE_COUNT_FOREIGN_KEY)
            System.arraycopy(
                foreignKeyBytes!!, 0, foreignKeyBytesPadded, 0,
                min(foreignKeyBytesPadded.size, foreignKeyBytes!!.size)
            )
            // trim last two bits, they don't fit
            foreignKeyBytesPadded[28] = foreignKeyBytesPadded[28] and 0x3F.toByte()

            return KinBinaryMemo(
                magicByteIndicator,
                version,
                typeId!!,
                appIdx,
                Base64().encodeAsString(foreignKeyBytesPadded)
            )
        }
    }

    companion object {
        const val MASK_MAGIC_BYTE_INDICATOR: Int = 0x3
        const val MASK_VERSION: Int = 0x1C
        const val MASK_TYPE_ID: Int = 0x3E0
        const val MASK_APP_IDX: Int = 0x3FFFC00

        const val BIT_LENGTH_MAGIC_BYTE_INDICATOR = 2
        const val BIT_LENGTH_VERSION = 3
        const val BIT_LENGTH_TYPE_ID = 5
        const val BIT_LENGTH_APP_IDX = 16
        const val BIT_LENGTH_FOREIGN_KEY = 230

        const val BIT_OFFSET_MAGIC_BYTE_INDICATOR = 0
        const val BIT_OFFSET_VERSION = BIT_LENGTH_MAGIC_BYTE_INDICATOR
        const val BIT_OFFSET_TYPE_ID = BIT_OFFSET_VERSION + BIT_LENGTH_VERSION
        const val BIT_OFFSET_APP_IDX = BIT_OFFSET_TYPE_ID + BIT_LENGTH_TYPE_ID
        const val BIT_OFFSET_FOREIGN_KEY = BIT_OFFSET_APP_IDX + BIT_LENGTH_APP_IDX

        val BYTE_COUNT_LOWER_BYTES: Int by lazy {
            (ceil((BIT_LENGTH_MAGIC_BYTE_INDICATOR + BIT_LENGTH_VERSION + BIT_LENGTH_TYPE_ID + BIT_LENGTH_APP_IDX) / 8f)).toInt()
        }
        val BYTE_COUNT_FOREIGN_KEY: Int by lazy {
            ceil(BIT_LENGTH_FOREIGN_KEY / 8f).toInt()
        }
        val BYTE_COUNT_TOTAL: Int by lazy {
            (ceil((BIT_LENGTH_MAGIC_BYTE_INDICATOR + BIT_LENGTH_VERSION + BIT_LENGTH_TYPE_ID + BIT_LENGTH_APP_IDX + BIT_LENGTH_FOREIGN_KEY) / 8f)).toInt()
        }
        val BYTE_OF_FK_START = floor(BIT_OFFSET_FOREIGN_KEY / 8f).toInt()

        fun decode(bytes: ByteArray): KinBinaryMemo = with(bytes.subByteArray(0, 4).byteArrayToInt()) {
            val magicByteIndicator =
                this and MASK_MAGIC_BYTE_INDICATOR ushr BIT_OFFSET_MAGIC_BYTE_INDICATOR
            val version = this and MASK_VERSION ushr BIT_OFFSET_VERSION
            val typeId = TransferType.fromValue(this and MASK_TYPE_ID ushr BIT_OFFSET_TYPE_ID)
            val appIdx = this and MASK_APP_IDX ushr BIT_OFFSET_APP_IDX

            val foreignKey = ByteArray(29)
            for (i in 0..27) {
                foreignKey[i] = foreignKey[i] or (bytes[i + 3] ushr 2) and 0x3F
                foreignKey[i] = foreignKey[i] or ((bytes[i + 4] and 0x3) shl 6)
            }
            foreignKey[28] = foreignKey[28] or ((bytes[31] ushr 2) and 0x3F)

            KinBinaryMemo(
                magicByteIndicator,
                version,
                typeId,
                appIdx,
                Base64().encodeAsString(foreignKey)
            )
        }
    }

    val foreignKeyBytes: ByteArray by lazy {
        Base64().decode(foreignKey)!!
            .subByteArray(0, 29)
            .apply { set(28, get(28) and 0x3F.toByte()) }
    }

    /**
     * Fields below are packed from LSB to MSB order:
     *
     * magicByteIndicator    2 bits   | < 4
     * version               3 bits   | < 8
     * typeId                5 bits   | < 32
     * appIdx                16 bits  | < 65,536
     * foreignKey            230 bits | Often a SHA-224 of an [InvoiceList] but could be anything
     */
    fun encode(): ByteArray = ByteArray(BYTE_COUNT_TOTAL).apply {
        val lowerBytes =
            ((magicByteIndicator shl BIT_OFFSET_MAGIC_BYTE_INDICATOR and MASK_MAGIC_BYTE_INDICATOR)
                    or (version shl BIT_OFFSET_VERSION and MASK_VERSION)
                    or (typeId.value shl BIT_OFFSET_TYPE_ID and MASK_TYPE_ID)
                    or (appIdx shl BIT_OFFSET_APP_IDX and MASK_APP_IDX))
                .intToByteArray()
        System.arraycopy(lowerBytes, 0, this, 0, BYTE_COUNT_LOWER_BYTES)

        this[BYTE_OF_FK_START] = this[BYTE_OF_FK_START] or ((foreignKeyBytes[0] and 0x3F) shl 2)
        // insert the rest of the fk. since each loop references fk[n] and fk[n+1],
        // the upper bound is offset by 3 instead of 4.
        for (i in (BYTE_OF_FK_START + 1)..(2 + foreignKeyBytes.size)) {
            this[i] = this[i] or ((foreignKeyBytes[i - 4] ushr 6) and 0x3)
            this[i] = this[i] or (foreignKeyBytes[i - 3] and 0x3F shl 2)
        }
        // if the foreign key is less than 29 bytes,
        // the last 2 bits of the FK can be included in the memo
        if (foreignKeyBytes.size < 29) {
            this[BYTE_OF_FK_START + foreignKeyBytes.size] =
                ((foreignKeyBytes[foreignKeyBytes.size - 1] ushr 6)) and 0x3
        }
    }

    fun toKinMemo(): KinMemo = KinMemo(encode())

    override fun toString(): String {
        return "AgoraMemo(magicByteIndicator=$magicByteIndicator, " +
                "version=$version, " +
                "typeId=${typeId.value}, " +
                "appIdx=$appIdx, " +
                "foreignKey='$foreignKey')"
    }
}
