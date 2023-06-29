package com.mrl.pixiv.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mrl.pixiv.common.data.BaseUiState

class LoginUiState : BaseUiState() {
    var loginResult: Boolean by mutableStateOf(false)
    val isNeedLogin: Boolean by mutableStateOf(true)
    val refreshTokenResult: Boolean by mutableStateOf(false)
    val isNeedRefreshToken: Boolean by mutableStateOf(true)
}