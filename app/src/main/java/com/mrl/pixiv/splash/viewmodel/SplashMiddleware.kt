package com.mrl.pixiv.splash.viewmodel

import com.mrl.pixiv.common.data.Middleware
import com.mrl.pixiv.data.auth.AuthTokenFieldReq
import com.mrl.pixiv.data.auth.GrantType
import com.mrl.pixiv.domain.SetUserAccessTokenUseCase
import com.mrl.pixiv.domain.SetUserRefreshTokenUseCase
import com.mrl.pixiv.domain.auth.RefreshUserAccessTokenUseCase
import com.mrl.pixiv.repository.local.UserLocalRepository
import com.mrl.pixiv.repository.remote.AuthRemoteRepository
import kotlinx.coroutines.flow.first

class SplashMiddleware(
    private val setUserAccessTokenUseCase: SetUserAccessTokenUseCase,
    private val setUserRefreshTokenUseCase: SetUserRefreshTokenUseCase,
    private val refreshUserAccessTokenUseCase: RefreshUserAccessTokenUseCase,
    private val userLocalRepository: UserLocalRepository,
    private val authRemoteRepository: AuthRemoteRepository,
) : Middleware<SplashState, SplashAction>() {
    override suspend fun process(state: SplashState, action: SplashAction) {
        when (action) {
            is SplashAction.IsLoginIntent -> isLogin()
            is SplashAction.IsNeedRefreshTokenIntent -> isNeedRefreshToken()
            is SplashAction.RefreshAccessTokenAndRouteIntent -> refreshAccessToken(action.grantType)
            is SplashAction.RefreshAccessTokenIntent -> refreshAccessToken()

            else -> {}
        }
    }

    private fun refreshAccessToken() =
        launchNetwork {
            refreshUserAccessTokenUseCase()
        }


    private fun refreshAccessToken(grantType: GrantType) {
        launchNetwork {
            if (userLocalRepository.isNeedRefreshToken.first()) {
                val userRefreshToken = userLocalRepository.userRefreshToken.first()
                val req = AuthTokenFieldReq(
                    grantType = grantType.value,
                    refreshToken = userRefreshToken
                )
                requestHttpDataWithFlow(
                    request = authRemoteRepository.login(req),
                    failedCallback = {
                        dispatch(SplashAction.RouteToLoginScreenIntent)
                    }
                ) {
                    setUserRefreshToken(it.refreshToken)
                    setUserAccessToken(it.accessToken)
                    dispatch(SplashAction.RouteToHomeScreenIntent)
                }
            } else {
                dispatch(SplashAction.RouteToHomeScreenIntent)
            }
        }
    }

    private fun isNeedRefreshToken() {
        launchIO {
            if (userLocalRepository.isNeedRefreshToken.first()) {
                dispatch(SplashAction.RefreshAccessTokenAndRouteIntent(GrantType.REFRESH_TOKEN))
            } else {
                dispatch(SplashAction.RouteToHomeScreenIntent)
            }
        }
    }

    private fun isLogin() {
        launchIO {
            if (userLocalRepository.isLogin.first()) {
                dispatch(SplashAction.IsNeedRefreshTokenIntent)
            } else {
                dispatch(SplashAction.RouteToLoginScreenIntent)
            }
        }
    }

    private fun setUserRefreshToken(refreshToken: String) = setUserRefreshTokenUseCase(refreshToken)
    private fun setUserAccessToken(accessToken: String) = setUserAccessTokenUseCase(accessToken)
}