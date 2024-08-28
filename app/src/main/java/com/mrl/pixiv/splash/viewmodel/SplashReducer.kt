package com.mrl.pixiv.splash.viewmodel

import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.router.Graph
import com.mrl.pixiv.common.viewmodel.Reducer
import org.koin.core.annotation.Single

@Single
class SplashReducer : Reducer<SplashState, SplashAction> {
    override fun SplashState.reduce(action: SplashAction): SplashState {
        return when (action) {
            is SplashAction.RouteToHomeScreenIntent -> routeToHome(this)
            is SplashAction.RouteToLoginScreenIntent -> routeToLogin(this)
            else -> this
        }
    }

    private fun routeToLogin(state: SplashState) =
        state.copy(
            isLoading = false,
            startDestination = Destination.LoginScreen.route
        )

    private fun routeToHome(state: SplashState): SplashState =
        state.copy(
            isLoading = false,
            startDestination = Graph.MAIN
        )

}