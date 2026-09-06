package com.mrl.pixiv.common.analytics

@Suppress("UNUSED_PARAMETER")
fun captureDesktopFatalException(error: Throwable) = Unit

@Suppress("UNUSED_PARAMETER")
fun flushDesktopAnalytics(timeoutMillis: Long) = Unit
