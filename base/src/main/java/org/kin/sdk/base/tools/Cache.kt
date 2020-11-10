package org.kin.sdk.base.network.services

import org.kin.sdk.base.tools.Promise
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

data class Cache<KEY>(
    private val storage: MutableMap<KEY, Pair<*, Long>> = HashMap(),
    val defaultTimeout: Long = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES),
    val executor: ExecutorService = Executors.newSingleThreadExecutor()
) {
    fun <VALUE> resolve(
        key: KEY,
        timeoutOverride: Long = -1,
        fault: (KEY) -> Promise<VALUE>,
    ): Promise<VALUE> {
        return Promise.create<VALUE> { resolve, reject ->
            val cachedValue = storage[key].let {
                val now = System.currentTimeMillis()
                it?.first?.let { value ->
                    val timeStored = it.second
                    val timeToExpiry =
                        if (timeoutOverride >= 0) timeoutOverride
                        else defaultTimeout
                    val expiryTime = timeStored + timeToExpiry
                    if (expiryTime > now) value
                    else null
                }
            }

            if (cachedValue != null) {
                resolve(cachedValue as VALUE)
            } else {
                fault(key).then(
                    {
                        storage[key] = Pair(it, System.currentTimeMillis())
                        resolve(it)
                    },
                    { reject(it) }
                )
            }
        }.workOn(executor)
    }

    fun <VALUE> warm(key: KEY, fault: (KEY) -> Promise<VALUE>): Promise<VALUE> {
        return fault(key).flatMap {
            storage[key] = Pair(it, System.currentTimeMillis())
            Promise.of(it)
        }
    }

    fun invalidate(key: KEY) {
        storage.remove(key)
    }
}
