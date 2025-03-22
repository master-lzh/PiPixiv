package com.mrl.pixiv.login

import androidx.compose.runtime.Stable
import com.mrl.pixiv.common.datasource.local.mmkv.AuthManager
import com.mrl.pixiv.common.viewmodel.BaseMviViewModel
import com.mrl.pixiv.common.viewmodel.ViewIntent
import org.koin.android.annotation.KoinViewModel

@Stable
data class LoginState(
    val isLogin: Boolean = false,
)

sealed class LoginAction : ViewIntent {
    data class Login(val code: String, val codeVerifier: String) : LoginAction()
}

@KoinViewModel
class LoginViewModel : BaseMviViewModel<LoginState, LoginAction>(
    initialState = LoginState(),
) {
    override suspend fun handleIntent(intent: LoginAction) {
        when (intent) {
            is LoginAction.Login -> login(intent.code, intent.codeVerifier)
        }
    }

    private fun login(code: String, codeVerifier: String) =
        launchIO {
            AuthManager.login(code, codeVerifier)
            updateState { copy(isLogin = true) }
        }
}