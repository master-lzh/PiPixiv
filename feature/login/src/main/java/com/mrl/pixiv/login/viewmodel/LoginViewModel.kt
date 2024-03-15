package com.mrl.pixiv.login.viewmodel

import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.middleware.auth.AuthAction
import com.mrl.pixiv.common.middleware.auth.AuthMiddleware
import com.mrl.pixiv.common.middleware.auth.AuthReducer
import com.mrl.pixiv.common.middleware.auth.AuthState

class LoginViewModel(
    reducer: AuthReducer,
    authMiddleware: AuthMiddleware,
) : BaseViewModel<AuthState, AuthAction>(
    initialState = AuthState.INITIAL,
    reducer = reducer,
    middlewares = listOf(authMiddleware)
) {

}