package com.mrl.pixiv.splash.viewmodel

import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.data.auth.GrantType

sealed class SplashAction : Action {
    data object RouteToHomeScreenIntent : SplashAction()
    data object RouteToLoginScreenIntent : SplashAction()
    data object IsLoginIntent : SplashAction()
    data object IsNeedRefreshTokenIntent : SplashAction()
    data class RefreshAccessTokenAndRouteIntent(val grantType: GrantType) : SplashAction()
    data object RefreshAccessTokenIntent : SplashAction()
}
