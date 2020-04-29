package org.kin.stellarfork.responses

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.kin.stellarfork.Asset
import org.kin.stellarfork.KeyPair

object GsonSingleton {
    var instance: Gson? = null
        get() {
            if (field == null) {
                field = GsonBuilder()
                    .registerTypeAdapter(Asset::class.java, AssetDeserializer())
                    .registerTypeAdapter(
                        KeyPair::class.java,
                        KeyPairTypeAdapter().nullSafe()
                    )
                    .registerTypeAdapter(TransactionResponse::class.java, TransactionDeserializer())
                    .registerType(object : TypeToken<Page<AccountResponse>>() {})
                    .registerType(object : TypeToken<Page<LedgerResponse>>() {})
                    .registerType(object : TypeToken<Page<TransactionResponse>>() {})
                    .create()
            }
            return field
        }
        private set

    private fun <T> GsonBuilder.registerType(typeToken: TypeToken<Page<T>>): GsonBuilder {
        return registerTypeAdapter(typeToken.type, PageDeserializer<T>(typeToken))
    }
}
