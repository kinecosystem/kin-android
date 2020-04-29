package org.kin.stellarfork.responses

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.kin.stellarfork.KeyPair
import java.io.IOException

internal class KeyPairTypeAdapter :
    TypeAdapter<KeyPair?>() {
    @Throws(IOException::class)
    override fun write(
        out: JsonWriter,
        value: KeyPair?
    ) { // Don't need this.
    }

    @Throws(IOException::class)
    override fun read(reader: JsonReader): KeyPair? {
        return KeyPair.fromAccountId(reader.nextString())
    }
}
