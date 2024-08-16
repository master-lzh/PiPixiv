package com.mrl.pixiv.common.middleware.auth

import com.mrl.pixiv.common.viewmodel.Reducer

class AuthReducer : Reducer<AuthState, AuthAction> {
    override fun AuthState.reduce(action: AuthAction): AuthState {
        return when (action) {
            is AuthAction.LoginSuccess -> copy(isLogin = true)
            else -> this
        }
    }
}