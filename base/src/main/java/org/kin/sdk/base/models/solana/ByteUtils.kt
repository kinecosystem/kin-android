package org.kin.sdk.base.models.solana

import java.io.ByteArrayInputStream

// Writing Bytes


// Reading Bytes

fun ByteArrayInputStream.read(numBytes: Int): ByteArray {
    var i = 0
    return ByteArray(numBytes).also {
        while(i < numBytes) {
            it[i++] = read().toByte()
        }
    }
}

fun ByteArrayInputStream.readRemainingBytes(): ByteArray {
    var i = 0
    return ByteArray(available()).also {
        while(available() !=0) {
            it[i++] = read().toByte()
        }
    }
}

inline fun <reified T> ByteArray.toModel(newInstance: (ByteArray) -> T? = { null }): T =
    newInstance(this)
        ?: T::class.java.getConstructor(ByteArray::class.java).newInstance(this)

// Other

inline fun <T> wrapError(msg: String, wrapped: () -> T): T {
    val value: T
    try {
        value = wrapped()
    } catch (t: Throwable) {
        throw RuntimeException(msg, t)
    }
    return value
}
