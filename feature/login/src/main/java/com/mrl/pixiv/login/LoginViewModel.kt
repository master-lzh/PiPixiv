package com.mrl.pixiv.login

import com.mrl.pixiv.common.base.BaseViewModel
import com.mrl.pixiv.login.intent.LoginUiIntent
import com.mrl.pixiv.login.state.LoginUiState

class LoginViewModel : BaseViewModel<LoginUiState, LoginUiIntent>() {
    override fun handleUserIntent(intent: LoginUiIntent) {

    }

    override fun initUiState(): LoginUiState = LoginUiState()
}