package com.mrl.pixiv.repository.remote

import com.mrl.pixiv.data.auth.AuthTokenFieldReq
import com.mrl.pixiv.datasource.remote.UserAuthHttpService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn

class AuthRemoteRepository constructor(
    private val userAuthHttpService: UserAuthHttpService,
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun login(
        authTokenFieldReq: AuthTokenFieldReq
    ) = userAuthHttpService.login(authTokenFieldReq)
        .flowOn(ioDispatcher)

}