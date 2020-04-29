package org.kin.stellarfork.responses

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.Memo
import org.kin.stellarfork.codec.Base64
import java.lang.reflect.Type

class TransactionDeserializer : JsonDeserializer<TransactionResponse> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): TransactionResponse { // Create new Gson object with adapters needed in Transaction
        val gson = GsonBuilder()
            .registerTypeAdapter(
                KeyPair::class.java,
                KeyPairTypeAdapter().nullSafe()
            )
            .create()
        val transaction = gson.fromJson(json, TransactionResponse::class.java)
        val memoType = json.asJsonObject["memo_type"].asString
        transaction.setMemo(if (memoType == "none") {
            Memo.none()
        } else { // Because of the way "encoding/json" works on structs in Go, if transaction
// has an empty `memo_text` value, the `memo` field won't be present in a JSON
// representation of a transaction. That's why we need to handle a special case
// here.
            if (memoType == "text") {
                val memoField = json.asJsonObject["memo"]
                if (memoField != null) {
                    Memo.text(memoField.asString)
                } else {
                    Memo.text("")
                }
            } else {
                val memoValue = json.asJsonObject["memo"].asString
                when (memoType) {
                    "id" -> Memo.id(memoValue.toLong())
                    "hash" -> Memo.hash(Base64.decodeBase64(memoValue)!!)
                    "return" -> Memo.returnHash(Base64.decodeBase64(memoValue)!!)
                    else -> throw JsonParseException("Unknown memo type.")
                }
            }
        })
        return transaction
    }
}
