package com.mrl.pixiv.util

import android.app.Application
import android.content.Context

object AppUtil {
    lateinit var appContext: Context
    lateinit var application: Application

    fun init(application: Application) {
        appContext = application
        this.application = application
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getSystemService(serviceName: String): T? {
        return appContext.getSystemService(serviceName) as? T
    }
}
