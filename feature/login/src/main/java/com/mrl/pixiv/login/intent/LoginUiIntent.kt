package com.mrl.pixiv.login.intent

import com.mrl.pixiv.common.data.UiIntent

sealed class LoginUiIntent : UiIntent() {
    data class LoginIntent(val code: String, val codeVerifier: String) : LoginUiIntent()

}
