package org.kin.sdk.design.di

interface ActivityResultSink {
    fun register(requestCode: Int, onResult: (requestCode: Int, intent: Any?) -> Unit)
    fun onNewResult(requestCode: Int, intent: Any?)
}

interface Resolver {
    val activityResultSink: ActivityResultSink?
        get() = null
}
