package com.mrl.pixiv.login

import android.net.Uri
import android.util.Base64
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebContent
import com.google.accompanist.web.WebView
import com.google.accompanist.web.WebViewState
import com.mrl.pixiv.common.ui.BaseScreen
import com.mrl.pixiv.login.intent.LoginUiIntent
import java.security.MessageDigest
import kotlin.random.Random

private lateinit var codeVerifier: String

private fun getCodeVer(): String {
    val randomKeySet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~"
    val result = (0..128).map {
        randomKeySet[Random.nextInt(randomKeySet.length)]
    }.joinToString("")
    codeVerifier = result
    return codeVerifier
}

private fun getCodeChallenge(): String {
    return Base64.encodeToString(
        MessageDigest.getInstance("SHA-256").digest(codeVerifier.toByteArray(Charsets.US_ASCII)),
        Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
    ).replace("=", "")
}

fun generateWebviewUrl(create: Boolean) =
    if (create) {
        "https://app-api.pixiv.net/web/v1/provisional-accounts/create?code_challenge=${getCodeChallenge()}&code_challenge_method=S256&client=pixiv-android"
    } else {
        "https://app-api.pixiv.net/web/v1/login?code_challenge=${getCodeChallenge()}&code_challenge_method=S256&client=pixiv-android"
    }

private fun checkUri(uri: Uri): Boolean {
    if (uri.scheme == "pixiv" && uri.host == "account") {
        val code = uri.getQueryParameter("code")
        code?.let { viewModel.dispatch(LoginUiIntent.LoginIntent(code, codeVerifier)) }
        return true
    }
    return false
}

@Composable
fun LoginScreen() {
    var currUrl by rememberSaveable { mutableStateOf(generateWebviewUrl(true)) }
    BaseScreen(actions = {
        Button(onClick = {
            getCodeVer()
            currUrl = generateWebviewUrl(false)
        }) {
            Text(text = "登录")
        }
        Button(onClick = {
            getCodeVer()
            currUrl = generateWebviewUrl(true)
        }) {
            Text(text = "注册")
        }
    }) {
        WebView(
            state = WebViewState(WebContent.Url(currUrl)),
            onCreated = {
                it.settings.apply {
                    javaScriptEnabled = true
                    cacheMode = WebSettings.LOAD_DEFAULT
                    userAgentString = userAgentString.replace("; wv", "")
                }
            },
            client = object : AccompanistWebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    if (checkUri(request?.url!!)) {
                        return true
                    }
                    return super.shouldOverrideUrlLoading(view, request)
                }


            }
        )
    }
}