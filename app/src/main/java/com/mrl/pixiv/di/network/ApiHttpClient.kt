package com.mrl.pixiv.di.network

import com.mrl.pixiv.datasource.TokenManager
import com.mrl.pixiv.di.network.NetworkUtil.enableBypassSniffing
import com.mrl.pixiv.di.network.NetworkUtil.refreshUserAccessTokenUseCase
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.plugin
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
@Named("api")
fun apiHttpClient() =
    baseHttpClient.apply {
        plugin(HttpSend).intercept { request ->
            addAuthHeader(request)
            request.apply {
                url {
                    protocol = URLProtocol.HTTPS
                    host = API_HOST
                }
            }
            execute(request)
        }
        config {
            defaultRequest {
                url("https://${if (enableBypassSniffing) API_HOST else hostMap[API_HOST]}")
                accept(ContentType.Application.Json)
            }
            HttpResponseValidator {
                handleResponseExceptionWithRequest { cause, request ->
                    val tokenExpiredException = cause as? ClientRequestException
                        ?: return@handleResponseExceptionWithRequest
                    if (tokenExpiredException.response.status == HttpStatusCode.Unauthorized) {
                        runBlocking(Dispatchers.IO) {
                            refreshUserAccessTokenUseCase {
                                TokenManager.setToken(it)
                            }
                        }
                    }
                }
            }
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 3)
                exponentialDelay()
            }
        }
    }