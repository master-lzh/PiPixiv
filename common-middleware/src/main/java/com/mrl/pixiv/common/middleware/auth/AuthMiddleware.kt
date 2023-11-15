package com.mrl.pixiv.common.middleware.auth

import com.mrl.pixiv.common.data.Middleware
import com.mrl.pixiv.data.Constants
import com.mrl.pixiv.data.auth.AuthTokenFieldReq
import com.mrl.pixiv.data.auth.AuthTokenResp
import com.mrl.pixiv.data.auth.GrantType
import com.mrl.pixiv.domain.SetUserAccessTokenUseCase
import com.mrl.pixiv.domain.SetUserInfoUseCase
import com.mrl.pixiv.domain.SetUserRefreshTokenUseCase
import com.mrl.pixiv.domain.auth.RefreshUserAccessTokenUseCase
import com.mrl.pixiv.repository.local.UserLocalRepository
import com.mrl.pixiv.repository.remote.AuthRemoteRepository
import kotlinx.coroutines.flow.first

class AuthMiddleware(
    private val authRemoteRepository: AuthRemoteRepository,
    private val userLocalRepository: UserLocalRepository,
    private val setUserAccessTokenUseCase: SetUserAccessTokenUseCase,
    private val setUserRefreshTokenUseCase: SetUserRefreshTokenUseCase,
    private val refreshUserAccessTokenUseCase: RefreshUserAccessTokenUseCase,
    private val setUserInfoUseCase: SetUserInfoUseCase,
) : Middleware<AuthState, AuthAction>() {
    override suspend fun process(state: AuthState, action: AuthAction) {
        when (action) {
            is AuthAction.Login -> login(action.code, action.codeVerifier)
            is AuthAction.RefreshAccessToken -> refreshAccessToken()

            else -> {}
        }
    }

    private fun refreshAccessToken() {
        launchNetwork {
            refreshUserAccessTokenUseCase()
            val refreshToken = userLocalRepository.userRefreshToken.first()
            val req = AuthTokenFieldReq(
                grantType = GrantType.REFRESH_TOKEN.value,
                refreshToken = refreshToken,
            )
            requestHttpDataWithFlow(request = authRemoteRepository.login(req)) {
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
            requestHttpDataWithFlow(request = authRemoteRepository.login(req)) {
                setUserInfo(it)
                dispatch(AuthAction.LoginSuccess)
            }
        }

    private fun setUserInfo(authTokenResp: AuthTokenResp) = launchIO {
        authTokenResp.apply {
            user?.let { setUserInfoUseCase(it) }
            setUserAccessTokenUseCase(accessToken)
            setUserRefreshTokenUseCase(refreshToken)
        }
    }
}