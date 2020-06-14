package org.kin.sdk.demo.viewmodel

import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.IllegalStateException
import java.math.BigInteger
import java.util.concurrent.TimeUnit

class WalletOnboarding(private val host: String = "friendbot-testnet.kininfrastructure.com") {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    fun activateAccount(publicAddress: String, fundingAmount: BigInteger, callback: (ex: Exception?) -> Unit) {
        val activateRequest = Request.Builder()
            .url(HttpUrl.Builder()
                .scheme("https")
                .host(host)
                .addQueryParameter("addr", publicAddress)
                .addQueryParameter("amount", fundingAmount.toString())
                .build())
            .build()

        okHttpClient.newCall(activateRequest).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.code() in 200..299) {
                    callback(null)
                } else {
                    callback(IllegalStateException("status=${response.code()}\n${response.body()?.string()}"))
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(e)
            }
        })
    }
}
