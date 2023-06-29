package com.mrl.pixiv.splash

import com.mrl.pixiv.common.data.UiIntent
import com.mrl.pixiv.data.auth.GrantType

sealed class SplashUiIntent : UiIntent() {
    object RouteToHomeScreenIntent : SplashUiIntent()
    object RouteToLoginScreenIntent : SplashUiIntent()

    object IsLoginIntent : SplashUiIntent()
    object IsNeedRefreshTokenIntent : SplashUiIntent()
    data class RefreshAccessTokenIntent(val grantType: GrantType) : SplashUiIntent()
}
