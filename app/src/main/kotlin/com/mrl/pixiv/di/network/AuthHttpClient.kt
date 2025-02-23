package com.mrl.pixiv.di.network

import com.mrl.pixiv.di.network.NetworkUtil.enableBypassSniffing
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.plugin
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
@Named("auth")
fun authHttpClient() = baseHttpClient.apply {
    plugin(HttpSend).intercept { request ->
        addAuthHeader(request)
        request.apply {
            url {
                protocol = URLProtocol.HTTPS
                host = if (enableBypassSniffing) AUTH_HOST else hostMap[AUTH_HOST]!!
            }
            headers.remove("Authorization")
            headers["Host"] = AUTH_HOST
        }
        execute(request)
    }
    config {
        defaultRequest {
            url("https://${if (enableBypassSniffing) AUTH_HOST else hostMap[AUTH_HOST]}")
            accept(ContentType.Application.Json)
        }
    }
}