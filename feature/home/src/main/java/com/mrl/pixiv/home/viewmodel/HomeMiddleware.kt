package com.mrl.pixiv.home.viewmodel

import com.mrl.pixiv.common.viewmodel.Middleware
import com.mrl.pixiv.domain.auth.RefreshUserAccessTokenUseCase
import com.mrl.pixiv.home.R
import com.mrl.pixiv.util.ToastUtil
import org.koin.core.annotation.Factory

@Factory
class HomeMiddleware(
    private val refreshUserAccessTokenUseCase: RefreshUserAccessTokenUseCase,
) : Middleware<HomeState, HomeAction>() {
    override suspend fun process(state: HomeState, action: HomeAction) {
        when (action) {
            is HomeAction.RefreshTokenIntent -> refreshToken()

            else -> {}
        }
    }

    private fun refreshToken() {
        launchNetwork {
            refreshUserAccessTokenUseCase {
                ToastUtil.safeShortToast(R.string.refresh_token_success)
            }
        }
    }
}