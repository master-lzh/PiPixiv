package com.mrl.pixiv.datasource.remote

import com.mrl.pixiv.api.UserAuthApi
import com.mrl.pixiv.data.auth.AuthTokenFieldReq

class UserAuthHttpService(
    private val userAuthApi: UserAuthApi
) {
    suspend fun login(
        authTokenFieldReq: AuthTokenFieldReq
    ) = userAuthApi.login(authTokenFieldReq.toMap())
}