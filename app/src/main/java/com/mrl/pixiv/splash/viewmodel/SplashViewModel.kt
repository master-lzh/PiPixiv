package com.mrl.pixiv.splash.viewmodel

import com.mrl.pixiv.common.viewmodel.BaseViewModel

class SplashViewModel(
    reducer: SplashReducer,
    middleware: SplashMiddleware,
) : BaseViewModel<SplashState, SplashAction>(
    reducer = reducer,
    initialState = SplashState.INITIAL,
    middlewares = listOf(middleware)
) {
    override fun onStart() {
        dispatch(SplashAction.IsLoginIntent)
    }
}