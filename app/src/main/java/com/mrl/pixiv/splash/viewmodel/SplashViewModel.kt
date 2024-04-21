package com.mrl.pixiv.splash.viewmodel

import android.content.Intent
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class SplashViewModel(
    reducer: SplashReducer,
    middleware: SplashMiddleware,
) : BaseViewModel<SplashState, SplashAction>(
    reducer = reducer,
    initialState = SplashState.INITIAL,
    middlewares = listOf(middleware)
) {
    val intent: MutableStateFlow<Intent?> = MutableStateFlow(null)
    override fun onStart() {
        dispatch(SplashAction.IsLoginIntent)
    }
}