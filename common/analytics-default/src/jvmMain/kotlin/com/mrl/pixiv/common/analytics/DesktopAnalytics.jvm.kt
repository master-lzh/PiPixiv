package com.mrl.pixiv.common.analytics

import io.sentry.SentryEvent
import io.sentry.SentryLevel
import io.sentry.exception.ExceptionMechanismException
import io.sentry.protocol.Mechanism

fun captureDesktopFatalException(error: Throwable) {
    val mechanism = Mechanism().apply {
        type = "TaoFatalError"
        isHandled = false
    }
    val event = SentryEvent(ExceptionMechanismException(mechanism, error, Thread.currentThread()))
        .apply { level = SentryLevel.FATAL }
    // Uses the same Java SDK scopes initialized by the existing KMP Sentry.init.
    io.sentry.Sentry.captureEvent(event)
}

fun flushDesktopAnalytics(timeoutMillis: Long) {
    io.sentry.Sentry.flush(timeoutMillis)
}
