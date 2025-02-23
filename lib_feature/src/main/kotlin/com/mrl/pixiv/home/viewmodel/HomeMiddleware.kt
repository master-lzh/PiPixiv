package com.mrl.pixiv.home.viewmodel

import com.mrl.pixiv.common.domain.auth.RefreshUserAccessTokenUseCase
import com.mrl.pixiv.common.util.RString
import com.mrl.pixiv.common.util.ToastUtil
import com.mrl.pixiv.common.viewmodel.Middleware
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
                ToastUtil.safeShortToast(RString.refresh_token_success)
            }
        }
    }
}