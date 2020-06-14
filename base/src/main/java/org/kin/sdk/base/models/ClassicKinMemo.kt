package org.kin.sdk.base.models

import kotlin.math.max
import kotlin.math.min

/**
 * This is used to format a text based [KinMemo] with the indended format of
 *      "appIdVersion-appId-memoSuffix"
 *  e.g. "1-aef2-someAppLandMemoInfo"
 */
data class ClassicKinMemo(val appIdVersion: Int = 1, val appId: AppId, val memoSuffix: MemoSuffix) {
    override fun toString() = "$appIdVersion-$appId-$memoSuffix"
}

fun ClassicKinMemo.asKinMemo(): KinMemo = KinMemo(toString())

/**
 * @param value - limited to 3 or 4 characters
 */

data class AppId @Throws(IllegalArgumentException::class) constructor(val value: String) {
    init {
        if (!value.isBlank()) {
            require(value.matches(Regex("[a-zA-Z0-9]{3,4}"))) {
                "appId must contain only upper and/or lower case letters and/or digits and that " +
                        "the total string length is between 3 to 4. for example 1234 or 2ab3 or " +
                        "cd2 or fqa, etc."
            }
        }
    }

    override fun toString() = value.subSequence(0, max(0, min(value.length, 4))).toString()
}

/**
 * @param value - will be truncated to 26 characters
 */
data class MemoSuffix(val value: String) {
    init {
        if (!value.isBlank()) {
            require(value.length <= 26) { "MemSuffix must be less than 26 characters" }
        }
    }

    override fun toString(): String = value.subSequence(0, max(0, min(value.length, 26))).toString()
}

