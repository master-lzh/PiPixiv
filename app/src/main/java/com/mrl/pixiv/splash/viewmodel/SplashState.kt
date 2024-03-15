package com.mrl.pixiv.splash.viewmodel

import androidx.compose.runtime.Stable
import com.mrl.pixiv.common.viewmodel.State

@Stable
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
