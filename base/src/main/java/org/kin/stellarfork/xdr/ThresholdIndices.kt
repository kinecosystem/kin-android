// Automatically generated on 2015-11-05T11:21:06-08:00
// DO NOT EDIT or your changes may be overwritten
package org.kin.stellarfork.xdr

import java.io.IOException

// === xdr source ============================================================
//  enum ThresholdIndexes
//  {
//      THRESHOLD_MASTER_WEIGHT = 0,
//      THRESHOLD_LOW = 1,
//      THRESHOLD_MED = 2,
//      THRESHOLD_HIGH = 3
//  };
//  ===========================================================================
enum class ThresholdIndices(val value: Int) {
    THRESHOLD_MASTER_WEIGHT(0),
    THRESHOLD_LOW(1),
    THRESHOLD_MED(2),
    THRESHOLD_HIGH(3);

    companion object {
        @JvmStatic
        @Throws(IOException::class)
        fun decode(stream: XdrDataInputStream): ThresholdIndices {
            return when (val value = stream.readInt()) {
                0 -> THRESHOLD_MASTER_WEIGHT
                1 -> THRESHOLD_LOW
                2 -> THRESHOLD_MED
                3 -> THRESHOLD_HIGH
                else -> throw RuntimeException("Unknown enum value: $value")
            }
        }

        @JvmStatic
        @Throws(IOException::class)
        fun encode(stream: XdrDataOutputStream, value: ThresholdIndices) {
            stream.writeInt(value.value)
        }
    }
}
