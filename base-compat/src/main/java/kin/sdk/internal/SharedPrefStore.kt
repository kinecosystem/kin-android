package kin.sdk.internal

import android.content.SharedPreferences
import androidx.annotation.Nullable

internal class SharedPrefStore(private val sharedPref: SharedPreferences) : Store {
    override fun saveString(key: String, value: String) {
        sharedPref.edit()
            .putString(key, value)
            .apply()
    }

    @Nullable
    override fun getString(key: String): String? {
        return sharedPref.getString(key, null)
    }

    override fun clear(key: String) {
        sharedPref.edit().remove(key).apply()
    }
}
