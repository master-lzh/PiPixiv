package com.mrl.pixiv.login

import com.mrl.pixiv.common.base.BaseScreenViewModel
import com.mrl.pixiv.data.Constants
import com.mrl.pixiv.data.auth.AuthTokenFieldReq
import com.mrl.pixiv.data.auth.AuthTokenResp
import com.mrl.pixiv.data.auth.GrantType
import com.mrl.pixiv.domain.SetUserAccessTokenUseCase
import com.mrl.pixiv.domain.SetUserIdUseCase
import com.mrl.pixiv.domain.SetUserRefreshTokenUseCase
import com.mrl.pixiv.repository.remote.AuthRemoteRepository

class LoginViewModel(
    private val setUserAccessTokenUseCase: SetUserAccessTokenUseCase,
    private val setUserRefreshTokenUseCase: SetUserRefreshTokenUseCase,
    private val setUserIdUseCase: SetUserIdUseCase,
    private val authRemoteRepository: AuthRemoteRepository,
) : BaseScreenViewModel<LoginUiState, LoginUiIntent>() {

    override fun handleUserIntent(intent: LoginUiIntent) {
        when (intent) {
            is LoginUiIntent.LoginIntent -> login(intent.code, intent.codeVerifier)
        }
    }

    private fun login(code: String, codeVerify: String) = launchNetwork {
        val req = AuthTokenFieldReq(
            grantType = GrantType.AUTHORIZATION_CODE.value,
            code = code,
            codeVerifier = codeVerify,
            redirectUri = Constants.PIXIV_LOGIN_REDIRECT_URL,
        )
        requestHttpDataWithFlow(request = authRemoteRepository.login(req)) {
            if (it != null) {
                setUserInfo(it)
                updateUiState {
                    apply { loginResult = true }
                }
            }
        }
    }

    private fun setUserInfo(authTokenResp: AuthTokenResp) = launchIO {
        authTokenResp.apply {
            user?.id?.let { setUserIdUseCase(it.toLong()) }
            accessToken?.let { setUserAccessTokenUseCase(it) }
            refreshToken?.let { setUserRefreshTokenUseCase(it) }
        }
    }

    override fun initUiState(): LoginUiState = LoginUiState()
}