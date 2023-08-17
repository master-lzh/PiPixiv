package com.mrl.pixiv.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import retrofit2.HttpException

object NetworkExceptionUtil : CoroutineScope by MainScope() {
    private const val EVENT_HTTP_EXCEPTION = "http_exception"

    fun resolveException(e: Throwable) {
        e.printStackTrace()

        when (e) {
            is HttpException -> {
                val code = e.code()
                logEvent(
                    EVENT_HTTP_EXCEPTION,
                    mapOf(
                        "code" to code,
                        "message" to e.message(),
                        "url" to e.response()?.raw()?.request?.url.toString()
                    )
                )
            }

            else -> {
                logException(e)
            }
        }
    }
}