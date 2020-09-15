package org.kin.sdk.base.tools

import org.slf4j.ILoggerFactory
import org.slf4j.Logger

interface KinLogger {
    fun log(msg: () ->String)
    fun log(msg: String)
    fun warning(msg: String)
    fun error(msg: String, throwable: Throwable? = null)
}

interface KinLoggerFactory {
    var isLoggingEnabled: Boolean

    fun getLogger(name: String): KinLogger
}

class KinLoggerImpl(
    private val log: Logger,
    private val delegate: Delegate,
) : KinLogger {

    interface Delegate {
        val isLoggingEnabled: Boolean
    }

    override fun log(msg: () -> String) {
        logCheck()?.info(msg())
    }

    override fun log(msg: String) {
        logCheck()?.info(msg)
    }

    override fun warning(msg: String) {
        logCheck()?.warn(msg)
    }

    override fun error(msg: String, throwable: Throwable?) {
        if (throwable != null) logCheck()?.error(msg)
        else logCheck()?.error("$msg::${throwable?.message}::${throwable?.cause}")
    }

    private fun logCheck(): Logger? {
        return if (delegate.isLoggingEnabled) this.log
        else null
    }
}


class KinLoggerFactoryImpl(
    override var isLoggingEnabled: Boolean,
    private val logger: ILoggerFactory = org.slf4j.LoggerFactory.getILoggerFactory(),
) : KinLoggerFactory, KinLoggerImpl.Delegate {
    override fun getLogger(name: String): KinLogger {
        return KinLoggerImpl(logger.getLogger(name), this)
    }
}
