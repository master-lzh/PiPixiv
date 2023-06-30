package com.mrl.pixiv.splash

import androidx.lifecycle.viewModelScope
import com.mrl.pixiv.common.base.BaseViewModel
import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.router.Graph
import com.mrl.pixiv.data.auth.AuthTokenFieldReq
import com.mrl.pixiv.data.auth.GrantType
import com.mrl.pixiv.domain.SetUserAccessTokenUseCase
import com.mrl.pixiv.domain.SetUserRefreshTokenUseCase
import com.mrl.pixiv.repository.local.UserLocalRepository
import com.mrl.pixiv.repository.remote.AuthRemoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(
    private val setUserAccessTokenUseCase: SetUserAccessTokenUseCase,
    private val setUserRefreshTokenUseCase: SetUserRefreshTokenUseCase,
    private val userLocalRepository: UserLocalRepository,
    private val authRemoteRepository: AuthRemoteRepository,
) : BaseViewModel<SplashUiState, SplashUiIntent>() {



    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        dispatch(SplashUiIntent.IsLoginIntent)
    }

    private fun routeToHome() {
        updateUiState { apply { startDestination = Graph.MAIN } }
    }

    private fun routeToLogin() {
        updateUiState { apply { startDestination = Destination.LoginScreen.route } }
    }

    private fun isLogin() {
        launchIO {
            if (userLocalRepository.isLogin.first()) {
                updateUiState { apply { isLogin = true } }
                dispatch(SplashUiIntent.IsNeedRefreshTokenIntent)
            } else {
                _isLoading.emit(false)
                updateUiState { apply { isLogin = false } }
                dispatch(SplashUiIntent.RouteToLoginScreenIntent)
            }
        }
    }

    private fun isNeedRefreshToken() {
        launchIO {
            if (userLocalRepository.isNeedRefreshToken.first()) {
                updateUiState { apply { isTokenExpired = true } }
                dispatch(SplashUiIntent.RefreshAccessTokenIntent(GrantType.REFRESH_TOKEN))
            } else {
                _isLoading.emit(false)
                updateUiState { apply { isTokenExpired = false } }
                dispatch(SplashUiIntent.RouteToHomeScreenIntent)
            }
        }
    }

    override fun handleUserIntent(intent: SplashUiIntent) {
        when (intent) {
            is SplashUiIntent.RouteToHomeScreenIntent -> routeToHome()
            is SplashUiIntent.RouteToLoginScreenIntent -> routeToLogin()
            is SplashUiIntent.IsLoginIntent -> isLogin()
            is SplashUiIntent.IsNeedRefreshTokenIntent -> isNeedRefreshToken()
            is SplashUiIntent.RefreshAccessTokenIntent -> refreshAccessToken(intent.grantType)
        }
    }

    private fun refreshAccessToken(grantType: GrantType) {
        launchIO {
            if (userLocalRepository.isNeedRefreshToken.first()) {
                val userRefreshToken = userLocalRepository.userRefreshToken.first()
                val req = AuthTokenFieldReq(
                    grantType = grantType.value,
                    refreshToken = userRefreshToken
                )
                requestHttpDataWithFlow(request = authRemoteRepository.login(req)) {
                    if (it != null) {
                        setUserRefreshToken(it.refreshToken!!)
                        setUserAccessToken(it.accessToken!!)
                        updateUiState {
                            apply {
                                isTokenExpired = false
                                startDestination = Graph.MAIN
                            }
                        }
                    } else {
                        updateUiState {
                            apply {
                                isTokenExpired = true
                                startDestination = Destination.LoginScreen.route
                            }
                        }
                    }
                }
            } else {
                updateUiState { apply { isTokenExpired = false } }
            }
            _isLoading.emit(false)
        }
    }

    private fun setUserRefreshToken(refreshToken: String) = viewModelScope.launch(ioDispatcher) {
        setUserRefreshTokenUseCase(refreshToken)
    }

    private fun setUserAccessToken(accessToken: String) = viewModelScope.launch(ioDispatcher) {
        setUserAccessTokenUseCase(accessToken)
    }

    override fun initUiState(): SplashUiState = SplashUiState()
}