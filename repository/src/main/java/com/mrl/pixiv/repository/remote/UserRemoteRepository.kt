package com.mrl.pixiv.repository.remote

import com.mrl.pixiv.data.auth.AuthTokenFieldReq
import com.mrl.pixiv.datasource.remote.UserAuthHttpService
import com.mrl.pixiv.network.Dispatcher
import com.mrl.pixiv.network.DispatcherEnum
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserRemoteRepository @Inject constructor(
    private val userAuthHttpService: UserAuthHttpService,
    @Dispatcher(DispatcherEnum.IO) private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun login(
        authTokenFieldReq: AuthTokenFieldReq
    ) = userAuthHttpService.login(authTokenFieldReq)
        .flowOn(ioDispatcher)

}