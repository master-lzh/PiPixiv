package com.mrl.pixiv.common.network

import com.mrl.pixiv.common.util.NetworkUtil.imageHost
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.http.URLProtocol
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
@Named("image")
fun imageHttpClient() = baseImageHttpClient.apply {
    plugin(HttpSend).intercept { request ->
        request.apply {
            url {
                host = imageHost
                protocol = URLProtocol.HTTPS
            }
            headers["Referer"] = "https://app-api.pixiv.net/"
        }
        execute(request)
    }
}