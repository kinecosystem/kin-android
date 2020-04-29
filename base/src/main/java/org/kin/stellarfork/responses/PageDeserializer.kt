package org.kin.stellarfork.responses

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import org.kin.stellarfork.Asset
import org.kin.stellarfork.KeyPair
import java.lang.reflect.Type

internal class PageDeserializer<E>
/**
 * "Generics on a type are typically erased at runtime, except when the type is compiled with the
 * generic parameter bound. In that case, the compiler inserts the generic type information into
 * the compiled class. In other cases, that is not possible."
 * More info: http://stackoverflow.com/a/14506181
 *
 * @param pageType
 */(private val pageType: TypeToken<Page<E>>) :
    JsonDeserializer<Page<E>> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Page<E> { // Flatten the object so it has two fields `records` and `links`
        val newJson = JsonObject()
            .apply {
                add(
                    "records",
                    json.asJsonObject["_embedded"].asJsonObject["records"]
                )
                add("links", json.asJsonObject["_links"])
            }
        // Create new Gson object with adapters needed in Page
        val gson = GsonBuilder()
            .registerTypeAdapter(Asset::class.java, AssetDeserializer())
            .registerTypeAdapter(
                KeyPair::class.java,
                KeyPairTypeAdapter().nullSafe()
            )
            .registerTypeAdapter(TransactionResponse::class.java, TransactionDeserializer())
            .create()
        return gson.fromJson(newJson, pageType.type)
    }

}
