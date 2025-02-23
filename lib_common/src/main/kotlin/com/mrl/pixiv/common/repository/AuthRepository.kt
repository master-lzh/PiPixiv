package com.mrl.pixiv.common.repository

import com.mrl.pixiv.common.data.auth.AuthTokenFieldReq
import com.mrl.pixiv.common.datasource.remote.UserAuthHttpService
import org.koin.core.annotation.Single

@Single
class AuthRepository(
    private val userAuthHttpService: UserAuthHttpService,
) {
    suspend fun login(
        authTokenFieldReq: AuthTokenFieldReq
    ) = userAuthHttpService.login(authTokenFieldReq)
}