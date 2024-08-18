package com.mrl.pixiv.login.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer

class LoginReducer : Reducer<LoginState, LoginAction> {
    override fun LoginState.reduce(action: LoginAction): LoginState {
        return when (action) {
            is LoginAction.LoginSuccess -> copy(isLogin = true)
            else -> this
        }
    }
}