package com.mrl.pixiv.common.middleware.auth

import com.mrl.pixiv.common.data.State

data class AuthState(
    val isLogin: Boolean,
) : State {
    companion object {
        val INITIAL = AuthState(isLogin = false)
    }
}

