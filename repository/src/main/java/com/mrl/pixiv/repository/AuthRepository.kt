package com.mrl.pixiv.repository

import com.mrl.pixiv.data.auth.AuthTokenFieldReq
import com.mrl.pixiv.datasource.remote.UserAuthHttpService
import org.koin.core.annotation.Single

@Single
class AuthRepository(
    private val userAuthHttpService: UserAuthHttpService,
) {
    suspend fun login(
        authTokenFieldReq: AuthTokenFieldReq
    ) = userAuthHttpService.login(authTokenFieldReq)
}