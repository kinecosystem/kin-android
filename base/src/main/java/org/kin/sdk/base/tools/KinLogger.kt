package org.kin.sdk.base.tools

import org.slf4j.ILoggerFactory
import org.slf4j.Logger

interface KinLogger {
    fun log(msg: () -> String)
    fun log(msg: String)
    fun warning(msg: String)
    fun error(msg: String, throwable: Throwable? = null)

    interface Delegate {
        val isLoggingEnabled: Boolean
    }
}

interface KinLoggerFactory {
    var isLoggingEnabled: Boolean

    fun getLogger(name: String): KinLogger
}

class KinLoggerImpl(
    private val log: Logger,
    private val delegate: KinLogger.Delegate,
) : KinLogger {

    override fun log(msg: () -> String) {
        logCheck()?.info("[KinLogger.i]:${msg()}")
    }

    override fun log(msg: String) {
        logCheck()?.info("[KinLogger.i]:$msg")
    }

    override fun warning(msg: String) {
        logCheck()?.warn("[KinLogger.w]:$msg")
    }

    override fun error(msg: String, throwable: Throwable?) {
        if (throwable != null) logCheck()?.error("[KinLogger]:$msg")
        else logCheck()?.error("[KinLogger.e]:$msg::${throwable?.message}::${throwable?.cause}")
    }

    private fun logCheck(): Logger? {
        return if (delegate.isLoggingEnabled) this.log
        else null
    }
}

class KinTestLoggerImpl(
    private val log: Logger,
    private val delegate: KinLogger.Delegate,
) : KinLogger {

    override fun log(msg: () -> String) {
        println("[KinLogger.i]:${msg()}")
    }

    override fun log(msg: String) {
        println("[KinLogger.i]:${msg}")
    }

    override fun warning(msg: String) {
        println("[KinLogger.w]:${msg}")
    }

    override fun error(msg: String, throwable: Throwable?) {
        println("[KinLogger.e]:$msg::${throwable?.message}::${throwable?.cause}")
    }
}

class KinLoggerFactoryImpl(
    override var isLoggingEnabled: Boolean,
    private val logger: ILoggerFactory = org.slf4j.LoggerFactory.getILoggerFactory(),
) : KinLoggerFactory, KinLogger.Delegate {
    override fun getLogger(name: String): KinLogger {
        return KinLoggerImpl(logger.getLogger(name), this)
    }
}

class KinTestLoggerFactoryImpl(
    override var isLoggingEnabled: Boolean,
    private val logger: ILoggerFactory = org.slf4j.LoggerFactory.getILoggerFactory(),
) : KinLoggerFactory, KinLogger.Delegate {
    override fun getLogger(name: String): KinLogger {
        return KinTestLoggerImpl(logger.getLogger(name), this)
    }
}
