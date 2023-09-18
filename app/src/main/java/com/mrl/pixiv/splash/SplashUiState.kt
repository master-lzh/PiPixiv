package com.mrl.pixiv.splash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mrl.pixiv.common.data.BaseUiState

class SplashUiState : BaseUiState() {
    var isLogin: Boolean by mutableStateOf(false)
    var isTokenExpired: Boolean by mutableStateOf(true)
    var startDestination: String? by mutableStateOf(null)
}
