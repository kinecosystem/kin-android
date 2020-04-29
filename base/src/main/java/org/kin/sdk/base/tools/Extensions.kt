package org.kin.sdk.base.tools

fun <T> NetworkOperationsHandler.queueWork(work: (PromisedCallback<T>) -> Unit): Promise<T> {
    return Promise.create { resolve, reject ->
        queueOperation(
            NetworkOperation(
                resolve,
                reject
            ) { respond ->
                work(respond)
            })
    }
}
