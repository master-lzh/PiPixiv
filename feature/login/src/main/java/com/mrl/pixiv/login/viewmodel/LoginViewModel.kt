package com.mrl.pixiv.login.viewmodel

import com.mrl.pixiv.common.viewmodel.BaseViewModel

class LoginViewModel(
    reducer: LoginReducer,
    loginMiddleware: LoginMiddleware,
) : BaseViewModel<LoginState, LoginAction>(
    initialState = LoginState.INITIAL,
    reducer = reducer,
    middlewares = listOf(loginMiddleware)
) {

}