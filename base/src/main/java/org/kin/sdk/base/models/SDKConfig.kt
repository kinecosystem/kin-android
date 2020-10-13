package org.kin.sdk.base.models

object SDKConfig {
    const val platform = "JVM"
    const val versionString = "0.3.1"
    val systemUserAgent by lazy { System.getProperty("http.agent") ?: "JVM/unspecified" }
}
