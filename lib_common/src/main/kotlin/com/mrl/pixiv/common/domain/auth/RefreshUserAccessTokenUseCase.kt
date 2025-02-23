package com.mrl.pixiv.common.domain.auth

import com.mrl.pixiv.common.coroutine.launchIO
import com.mrl.pixiv.common.data.auth.AuthTokenFieldReq
import com.mrl.pixiv.common.data.auth.GrantType
import com.mrl.pixiv.common.domain.SetLocalUserInfoUseCase
import com.mrl.pixiv.common.domain.SetUserAccessTokenUseCase
import com.mrl.pixiv.common.domain.SetUserRefreshTokenUseCase
import com.mrl.pixiv.common.network.safeHttpCall
import com.mrl.pixiv.common.repository.AuthRepository
import com.mrl.pixiv.common.repository.UserRepository
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Single

@Single
class RefreshUserAccessTokenUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val setUserAccessTokenUseCase: SetUserAccessTokenUseCase,
    private val setUserRefreshTokenUseCase: SetUserRefreshTokenUseCase,
    private val setLocalUserInfoUseCase: SetLocalUserInfoUseCase,
) {
    suspend operator fun invoke(onSuccess: (accessToken: String) -> Unit = {}) {
        val userRefreshToken = userRepository.userRefreshToken.first()
        val req = AuthTokenFieldReq(
            grantType = GrantType.REFRESH_TOKEN.value,
            refreshToken = userRefreshToken
        )
        safeHttpCall(
            request = authRepository.login(req),
        ) {
            onSuccess(it.accessToken)
            launchIO {
                it.user?.let { authUser ->
                    setLocalUserInfoUseCase {
                        it.copy(
                            uid = authUser.id.toLongOrNull() ?: 0,
                            username = authUser.name,
                            avatar = authUser.profileImageUrls.px50X50,
                        )
                    }
                }
                setUserAccessTokenUseCase(it.accessToken)
                setUserRefreshTokenUseCase(it.refreshToken)
            }
        }
    }
}