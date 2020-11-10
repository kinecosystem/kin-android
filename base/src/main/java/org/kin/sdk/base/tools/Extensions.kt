package org.kin.sdk.base.tools

fun <T> NetworkOperationsHandler.queueWork(work: (PromisedCallback<T>, Throwable?) -> Unit): Promise<T> {
    return Promise.create { resolve, reject ->
        queueOperation(
            NetworkOperation(
                resolve,
                reject
            ) { respond, error ->
                work(respond, error)
            })
    }
}

fun <T> NetworkOperationsHandler.queueWork(work: (PromisedCallback<T>) -> Unit): Promise<T> {
    return queueWork { respond, _ -> work(respond) }
}

//fun <T> NetworkOperationsHandler.queueWorkWithPromise(backoffStrategy: BackoffStrategy = BackoffStrategy.Exponential(maximumWaitTime = 50000), workBuilder: () -> Promise<T>): Promise<T> {
//    return Promise.create { resolve, reject ->
//        queueOperation(
//            NetworkOperation(
//                resolve,
//                reject,
//                backoffStrategy = backoffStrategy
//            ) { respond, error ->
//                workBuilder().then({ respond(it) }, { respond(it) })
//            })
//    }
//}
