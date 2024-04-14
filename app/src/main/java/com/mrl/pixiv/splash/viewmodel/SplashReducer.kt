package com.mrl.pixiv.splash.viewmodel

import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.router.Graph
import com.mrl.pixiv.common.viewmodel.Reducer

class SplashReducer : Reducer<SplashState, SplashAction> {
    override fun reduce(state: SplashState, action: SplashAction): SplashState {
        return when (action) {
            is SplashAction.RouteToHomeScreenIntent -> routeToHome(state)
            is SplashAction.RouteToLoginScreenIntent -> routeToLogin(state)
            else -> state
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