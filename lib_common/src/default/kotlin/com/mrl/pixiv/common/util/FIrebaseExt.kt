package com.mrl.pixiv.common.util

import android.app.Application
import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.initialize

private val firebaseAnalytics
    get() = Firebase.analytics

private val firebaseCrashlytics
    get() = Firebase.crashlytics

fun Application.initializeFirebase() {
    Firebase.initialize(this)
    Firebase.crashlytics.isCrashlyticsCollectionEnabled = !isDebug
}


fun logEvent(event: String, params: Map<String, Any>? = null) {
    firebaseAnalytics.logEvent(event, params?.let {
        Bundle().apply {
            params.forEach { (k, v) ->
                when (v) {
                    is String -> putString(k, v)
                    is Int -> putInt(k, v)
                    is Long -> putLong(k, v)
                    is Float -> putFloat(k, v)
                    is Double -> putDouble(k, v)
                    is Boolean -> putBoolean(k, v)
                    else -> putString(k, v.toString())
                }
            }
        }
    })
}

fun logException(e: Throwable) {
    firebaseCrashlytics.recordException(e)
}