package com.mrl.pixiv.login.viewmodel

import com.mrl.pixiv.common.viewmodel.Action

sealed class LoginAction : Action {
    data class Login(val code: String, val codeVerifier: String) : LoginAction()
    data object LoginSuccess : LoginAction()
    data object RefreshAccessToken : LoginAction()
}