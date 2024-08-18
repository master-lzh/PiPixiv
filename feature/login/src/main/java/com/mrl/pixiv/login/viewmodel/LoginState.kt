package com.mrl.pixiv.login.viewmodel

import androidx.compose.runtime.Stable
import com.mrl.pixiv.common.viewmodel.State

@Stable
data class LoginState(
    val isLogin: Boolean,
) : State {
    companion object {
        val INITIAL = LoginState(isLogin = false)
    }
}

