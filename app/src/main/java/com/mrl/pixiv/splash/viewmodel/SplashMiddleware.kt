package com.mrl.pixiv.splash.viewmodel

import com.mrl.pixiv.common.viewmodel.Middleware
import com.mrl.pixiv.data.auth.AuthTokenFieldReq
import com.mrl.pixiv.data.auth.GrantType
import com.mrl.pixiv.domain.auth.RefreshUserAccessTokenUseCase
import com.mrl.pixiv.repository.AuthRepository
import com.mrl.pixiv.repository.UserRepository
import kotlinx.coroutines.flow.first

class SplashMiddleware(
    private val refreshUserAccessTokenUseCase: RefreshUserAccessTokenUseCase,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
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
        launchNetwork(
            onError = {
                dispatch(SplashAction.RouteToLoginScreenIntent)
            }
        ) {
            if (userRepository.isNeedRefreshToken.first()) {
                val userRefreshToken = userRepository.userRefreshToken.first()
                val req = AuthTokenFieldReq(
                    grantType = grantType.value,
                    refreshToken = userRefreshToken
                )
                requestHttpDataWithFlow(
                    request = authRepository.login(req),
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
            if (userRepository.isNeedRefreshToken.first()) {
                dispatch(SplashAction.RefreshAccessTokenAndRouteIntent(GrantType.REFRESH_TOKEN))
            } else {
                dispatch(SplashAction.RouteToHomeScreenIntent)
            }
        }
    }

    private fun isLogin() {
        launchIO {
            if (userRepository.isLogin.first()) {
                dispatch(SplashAction.IsNeedRefreshTokenIntent)
            } else {
                dispatch(SplashAction.RouteToLoginScreenIntent)
            }
        }
    }

    private fun setUserRefreshToken(refreshToken: String) = userRepository.setUserRefreshToken(refreshToken)
    private fun setUserAccessToken(accessToken: String) = userRepository.setUserAccessToken(accessToken)
}