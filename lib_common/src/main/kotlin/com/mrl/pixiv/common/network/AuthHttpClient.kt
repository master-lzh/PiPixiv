package com.mrl.pixiv.common.network

import com.mrl.pixiv.common.util.AUTH_HOST
import com.mrl.pixiv.common.util.addAuthHeader
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.plugin
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import org.koin.core.annotation.Single

@Single
@AuthClient
fun authHttpClient() = baseHttpClient.apply {
    plugin(HttpSend).intercept { request ->
        addAuthHeader(request)
        request.apply {
            headers.remove("Authorization")
            headers["Host"] = AUTH_HOST
        }
        execute(request)
    }
    config {
        defaultRequest {
            accept(ContentType.Application.Json)
        }
    }
}