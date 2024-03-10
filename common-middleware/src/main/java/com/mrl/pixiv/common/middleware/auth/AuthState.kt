package com.mrl.pixiv.common.middleware.auth

import androidx.compose.runtime.Stable
import com.mrl.pixiv.common.data.State

@Stable
data class AuthState(
    val isLogin: Boolean,
) : State {
    companion object {
        val INITIAL = AuthState(isLogin = false)
    }
}

