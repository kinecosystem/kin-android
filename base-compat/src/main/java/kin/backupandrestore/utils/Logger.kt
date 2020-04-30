package kin.backupandrestore.utils

import android.util.Log
import org.kin.base.compat.BuildConfig

object Logger {
    private const val TAG = "kin.backup"
    private val shouldLog =
        BuildConfig.DEBUG || Log.isLoggable(
            TAG,
            Log.DEBUG
        )

    fun d(msg: String?) {
        if (shouldLog) {
            println("$TAG $msg")
        }
    }

    @JvmStatic
    fun e(msg: String?, throwable: Throwable?) {
        if (shouldLog) {
            println("$TAG $msg $throwable")
            throwable?.printStackTrace()
        }
    }
}
