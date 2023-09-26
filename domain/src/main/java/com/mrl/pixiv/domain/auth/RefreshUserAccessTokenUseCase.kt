package com.mrl.pixiv.domain.auth

import com.mrl.pixiv.common.coroutine.launchIO
import com.mrl.pixiv.common.network.safeHttpCall
import com.mrl.pixiv.data.auth.AuthTokenFieldReq
import com.mrl.pixiv.data.auth.GrantType
import com.mrl.pixiv.domain.SetUserAccessTokenUseCase
import com.mrl.pixiv.domain.SetUserIdUseCase
import com.mrl.pixiv.domain.SetUserRefreshTokenUseCase
import com.mrl.pixiv.repository.local.UserLocalRepository
import com.mrl.pixiv.repository.remote.AuthRemoteRepository
import kotlinx.coroutines.flow.first

class RefreshUserAccessTokenUseCase(
    private val authRemoteRepository: AuthRemoteRepository,
    private val userLocalRepository: UserLocalRepository,
    private val setUserAccessTokenUseCase: SetUserAccessTokenUseCase,
    private val setUserRefreshTokenUseCase: SetUserRefreshTokenUseCase,
    private val setUserIdUseCase: SetUserIdUseCase,
) {
    suspend operator fun invoke(onSuccess: () -> Unit = {}) {
        val userRefreshToken = userLocalRepository.userRefreshToken.first()
        val req = AuthTokenFieldReq(
            grantType = GrantType.REFRESH_TOKEN.value,
            refreshToken = userRefreshToken
        )
        safeHttpCall(
            request = authRemoteRepository.login(req),
        ) {
            if (it != null) {
                onSuccess()
                launchIO {
                    it.user?.id?.let { setUserIdUseCase(it.toLong()) }
                    setUserAccessTokenUseCase(it.accessToken)
                    setUserRefreshTokenUseCase(it.refreshToken)
                }
            }
        }
    }
}