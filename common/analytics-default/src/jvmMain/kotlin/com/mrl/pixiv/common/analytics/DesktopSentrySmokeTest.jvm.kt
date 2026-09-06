package com.mrl.pixiv.common.analytics

import io.sentry.kotlin.multiplatform.Sentry

fun captureDesktopSentrySmokeTest(
    dsn: String,
    release: String,
    environment: String,
    marker: String,
    exception: Throwable,
): String {
    require(dsn.isNotBlank()) { "Sentry DSN is not configured for the desktop mapping smoke test" }
    Sentry.init { options ->
        options.dsn = dsn
        options.release = release
        options.environment = environment
        options.enableAutoSessionTracking = false
        options.sendDefaultPii = false
        options.attachThreads = false
        options.maxBreadcrumbs = 0
        options.sampleRate = 1.0
        options.tracesSampleRate = 0.0
        options.debug = false
    }
    try {
        check(Sentry.isEnabled()) { "Sentry is disabled for the desktop mapping smoke test" }
        val eventId = Sentry.captureException(exception) { scope ->
            scope.setTag("diagnostic", "desktop-sentry-mapping")
            scope.setTag("test_marker", marker)
        }.toString()
        check(eventId != "00000000000000000000000000000000") {
            "Sentry did not accept the desktop mapping smoke-test event"
        }
        // The KMP facade has no flush API; use only the JVM transport flush here.
        io.sentry.Sentry.flush(10_000L)
        return eventId
    } finally {
        Sentry.close()
    }
}
