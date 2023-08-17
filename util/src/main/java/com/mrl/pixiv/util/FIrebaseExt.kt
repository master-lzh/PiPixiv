package com.mrl.pixiv.util

import android.os.Bundle
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

val firebaseAnalytics
    get() = Firebase.analytics

val firebaseCrashlytics
    get() = Firebase.crashlytics


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