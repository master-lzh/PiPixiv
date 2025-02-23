package com.mrl.pixiv.login.viewmodel

import androidx.compose.runtime.Stable
import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State
import org.koin.android.annotation.KoinViewModel

@Stable
data class LoginState(
    val isLogin: Boolean,
) : State {
    companion object {
        val INITIAL = LoginState(isLogin = false)
    }
}

sealed class LoginAction : Action {
    data class Login(val code: String, val codeVerifier: String) : LoginAction()
    data object LoginSuccess : LoginAction()
    data object RefreshAccessToken : LoginAction()
}

@KoinViewModel
class LoginViewModel(
    reducer: LoginReducer,
    loginMiddleware: LoginMiddleware,
) : BaseViewModel<LoginState, LoginAction>(
    initialState = LoginState.INITIAL,
    reducer = reducer,
    middlewares = listOf(loginMiddleware)
) {

}