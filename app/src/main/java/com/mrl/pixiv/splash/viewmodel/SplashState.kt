package com.mrl.pixiv.splash.viewmodel

import com.mrl.pixiv.common.data.State

data class SplashState(
    val isLoading: Boolean,
    val startDestination: String?,
) : State {
    companion object {
        val INITIAL = SplashState(
            isLoading = true,
            startDestination = null
        )
    }
}
