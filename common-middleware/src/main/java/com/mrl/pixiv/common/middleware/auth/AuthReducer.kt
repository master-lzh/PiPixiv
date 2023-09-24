package com.mrl.pixiv.common.middleware.auth

import com.mrl.pixiv.common.data.Reducer

class AuthReducer : Reducer<AuthState, AuthAction> {
    override fun reduce(state: AuthState, action: AuthAction): AuthState {
        return when (action) {
            is AuthAction.LoginSuccess -> state.copy(isLogin = true)
            else -> state
        }
    }
}