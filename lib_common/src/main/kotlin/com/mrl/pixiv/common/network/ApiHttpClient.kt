package com.mrl.pixiv.common.network

import com.mrl.pixiv.common.datasource.local.mmkv.AuthManager
import com.mrl.pixiv.common.util.API_HOST
import com.mrl.pixiv.common.util.addAuthHeader
import io.ktor.client.plugins.*
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import org.koin.core.annotation.Single

@Single
@ApiClient
fun apiHttpClient() = baseHttpClient.apply {
    plugin(HttpSend).intercept { request ->
        addAuthHeader(request)
        request.apply {
            headers["Host"] = API_HOST
        }
        execute(request)
    }
    config {
        defaultRequest {
            accept(ContentType.Application.Json)
        }
        HttpResponseValidator {
            handleResponseExceptionWithRequest { cause, request ->
                val tokenExpiredException = cause as? ClientRequestException
                    ?: return@handleResponseExceptionWithRequest
                if (tokenExpiredException.response.status == HttpStatusCode.Unauthorized) {
                    AuthManager.requireUserAccessToken()
                }
            }
        }
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 3)
            exponentialDelay()
        }
    }
}