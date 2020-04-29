package kin.sdk.internal

internal interface Store {
    fun saveString(key: String, value: String)
    fun getString(key: String): String?
    fun clear(key: String)
}
