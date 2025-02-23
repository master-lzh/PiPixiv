package com.mrl.pixiv.login.viewmodel

import com.mrl.pixiv.common.data.Constants
import com.mrl.pixiv.common.data.auth.AuthTokenFieldReq
import com.mrl.pixiv.common.data.auth.AuthTokenResp
import com.mrl.pixiv.common.data.auth.GrantType
import com.mrl.pixiv.common.domain.SetLocalUserInfoUseCase
import com.mrl.pixiv.common.domain.SetUserAccessTokenUseCase
import com.mrl.pixiv.common.domain.SetUserRefreshTokenUseCase
import com.mrl.pixiv.common.domain.auth.RefreshUserAccessTokenUseCase
import com.mrl.pixiv.common.repository.AuthRepository
import com.mrl.pixiv.common.repository.UserRepository
import com.mrl.pixiv.common.viewmodel.Middleware
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory

@Factory
class LoginMiddleware(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val setUserAccessTokenUseCase: SetUserAccessTokenUseCase,
    private val setUserRefreshTokenUseCase: SetUserRefreshTokenUseCase,
    private val refreshUserAccessTokenUseCase: RefreshUserAccessTokenUseCase,
    private val setLocalUserInfoUseCase: SetLocalUserInfoUseCase,
) : Middleware<LoginState, LoginAction>() {
    override suspend fun process(state: LoginState, action: LoginAction) {
        when (action) {
            is LoginAction.Login -> login(action.code, action.codeVerifier)
            is LoginAction.RefreshAccessToken -> refreshAccessToken()

            else -> {}
        }
    }

    private fun refreshAccessToken() {
        launchNetwork {
            refreshUserAccessTokenUseCase()
            val refreshToken = userRepository.userRefreshToken.first()
            val req = AuthTokenFieldReq(
                grantType = GrantType.REFRESH_TOKEN.value,
                refreshToken = refreshToken,
            )
            requestHttpDataWithFlow(request = authRepository.login(req)) {
                setUserInfo(it)
            }
        }
    }

    private fun login(code: String, codeVerifier: String) =
        launchNetwork {
            val req = AuthTokenFieldReq(
                grantType = GrantType.AUTHORIZATION_CODE.value,
                code = code,
                codeVerifier = codeVerifier,
                redirectUri = Constants.PIXIV_LOGIN_REDIRECT_URL,
            )
            requestHttpDataWithFlow(request = authRepository.login(req)) {
                setUserInfo(it)
                dispatch(LoginAction.LoginSuccess)
            }
        }

    private fun setUserInfo(authTokenResp: AuthTokenResp) = launchIO {
        authTokenResp.apply {
            user?.let { authUser ->
                setLocalUserInfoUseCase {
                    it.copy(
                        uid = authUser.id.toLongOrNull() ?: 0,
                        username = authUser.name,
                        avatar = authUser.profileImageUrls.px50X50
                    )
                }
            }
            setUserAccessTokenUseCase(accessToken)
            setUserRefreshTokenUseCase(refreshToken)
        }
    }
}