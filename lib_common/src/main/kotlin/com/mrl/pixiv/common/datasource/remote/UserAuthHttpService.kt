package com.mrl.pixiv.common.datasource.remote

import com.mrl.pixiv.common.data.auth.AuthTokenFieldReq
import com.mrl.pixiv.common.data.auth.AuthTokenResp
import io.ktor.client.HttpClient
import io.ktor.http.parameters
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class UserAuthHttpService(
    @Named("auth") private val httpClient: HttpClient
) {
    suspend fun login(
        authTokenFieldReq: AuthTokenFieldReq
    ) = httpClient.safePostForm<AuthTokenResp>(
        urlString = "/auth/token",
        formParameters = parameters {
            authTokenFieldReq.toMap().map {
                append(it.key, it.value.toString())
            }
        }
    ) {
        headers.remove("Authorization")
    }
}