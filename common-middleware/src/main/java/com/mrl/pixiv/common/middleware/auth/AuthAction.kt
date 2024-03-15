package com.mrl.pixiv.common.middleware.auth

import com.mrl.pixiv.common.viewmodel.Action

sealed class AuthAction : Action {
    data class Login(val code: String, val codeVerifier: String) : AuthAction()
    data object LoginSuccess : AuthAction()
    data object RefreshAccessToken : AuthAction()
}