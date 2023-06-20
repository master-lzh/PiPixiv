package com.mrl.pixiv.splash

import com.mrl.pixiv.common.base.BaseViewModel

class SplashViewModel() : BaseViewModel<SplashUiState, SplashUiIntent>() {
    init {

    }
    override fun handleUserIntent(intent: SplashUiIntent) {

    }

    override fun initUiState(): SplashUiState = SplashUiState()
}