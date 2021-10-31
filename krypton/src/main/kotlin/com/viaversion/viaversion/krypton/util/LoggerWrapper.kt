package com.viaversion.viaversion.krypton.util

import org.apache.logging.log4j.Logger
import java.text.MessageFormat
import java.util.logging.Level
import java.util.logging.LogRecord

class LoggerWrapper(private val base: Logger) : java.util.logging.Logger("logger", null) {

    override fun log(record: LogRecord) {
        log(record.level, record.message)
    }

    override fun log(level: Level, msg: String) = when (level) {
        Level.FINE -> base.debug(msg)
        Level.WARNING -> base.warn(msg)
        Level.SEVERE -> base.error(msg)
        Level.INFO -> base.info(msg)
        else -> base.trace(msg)
    }

    override fun log(level: Level, msg: String, param1: Any?) = when (level) {
        Level.FINE -> base.debug(msg, param1)
        Level.WARNING -> base.warn(msg, param1)
        Level.SEVERE -> base.error(msg, param1)
        Level.INFO -> base.info(msg, param1)
        else -> base.trace(msg, param1)
    }

    override fun log(level: Level, msg: String, params: Array<out Any>?) {
        log(level, MessageFormat.format(msg, params)) // workaround not formatting correctly
    }

    override fun log(level: Level, msg: String, thrown: Throwable?) = when (level) {
        Level.FINE -> base.debug(msg, thrown)
        Level.WARNING -> base.warn(msg, thrown)
        Level.SEVERE -> base.error(msg, thrown)
        Level.INFO -> base.info(msg, thrown)
        else -> base.trace(msg, thrown)
    }
}
