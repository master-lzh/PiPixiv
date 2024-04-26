package com.mrl.pixiv.domain.auth

import com.mrl.pixiv.common.coroutine.launchIO
import com.mrl.pixiv.common.network.safeHttpCall
import com.mrl.pixiv.data.auth.AuthTokenFieldReq
import com.mrl.pixiv.data.auth.GrantType
import com.mrl.pixiv.data.user.userInfo
import com.mrl.pixiv.domain.SetLocalUserInfoUseCase
import com.mrl.pixiv.domain.SetUserAccessTokenUseCase
import com.mrl.pixiv.domain.SetUserRefreshTokenUseCase
import com.mrl.pixiv.repository.local.UserLocalRepository
import com.mrl.pixiv.repository.remote.AuthRemoteRepository
import kotlinx.coroutines.flow.first

class RefreshUserAccessTokenUseCase(
    private val authRemoteRepository: AuthRemoteRepository,
    private val userLocalRepository: UserLocalRepository,
    private val setUserAccessTokenUseCase: SetUserAccessTokenUseCase,
    private val setUserRefreshTokenUseCase: SetUserRefreshTokenUseCase,
    private val setLocalUserInfoUseCase: SetLocalUserInfoUseCase,
) {
    suspend operator fun invoke(onSuccess: (accessToken: String) -> Unit = {}) {
        val userRefreshToken = userLocalRepository.userRefreshToken.first()
        val req = AuthTokenFieldReq(
            grantType = GrantType.REFRESH_TOKEN.value,
            refreshToken = userRefreshToken
        )
        safeHttpCall(
            request = authRemoteRepository.login(req),
        ) {
            onSuccess(it.accessToken)
            launchIO {
                it.user?.let { authUser ->
                    setLocalUserInfoUseCase {
                        userInfo {
                            uid = authUser.id.toLongOrNull() ?: 0
                            username = authUser.name
                            avatar = authUser.profileImageUrls.px50X50
                        }
                    }
                }
                setUserAccessTokenUseCase(it.accessToken)
                setUserRefreshTokenUseCase(it.refreshToken)
            }
        }
    }
}