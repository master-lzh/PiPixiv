package com.mrl.pixiv.common.analytics

@Suppress("UNUSED_PARAMETER")
fun captureDesktopSentrySmokeTest(
    dsn: String,
    release: String,
    environment: String,
    marker: String,
    exception: Throwable,
): String = error("The FOSS build does not support the Sentry mapping smoke test")
