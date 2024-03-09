package com.mrl.pixiv.login

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Base64
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.mrl.pixiv.common.compose.OnLifecycle
import com.mrl.pixiv.common.middleware.auth.AuthAction
import com.mrl.pixiv.common.middleware.auth.AuthState
import com.mrl.pixiv.common.router.Graph
import com.mrl.pixiv.common.ui.Screen
import com.mrl.pixiv.login.viewmodel.LoginViewModel
import org.koin.androidx.compose.koinViewModel
import java.security.MessageDigest
import kotlin.random.Random

private var codeVerifier = getCodeVer()

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

fun generateWebViewUrl(create: Boolean) =
    if (create) {
        "https://app-api.pixiv.net/web/v1/provisional-accounts/create?code_challenge=${getCodeChallenge()}&code_challenge_method=S256&client=pixiv-android"
    } else {
        "https://app-api.pixiv.net/web/v1/login?code_challenge=${getCodeChallenge()}&code_challenge_method=S256&client=pixiv-android"
    }

private fun checkUri(dispatch: (AuthAction) -> Unit, uri: Uri): Boolean {
    if (uri.scheme == "pixiv" && uri.host == "account") {
        val code = uri.getQueryParameter("code")
        code?.let { dispatch(AuthAction.Login(code, codeVerifier)) }
        return true
    }
    return false
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = koinViewModel(),
    navHostController: NavHostController,
) {
    OnLifecycle(onLifecycle = loginViewModel::onStart)
    LoginScreen(
        modifier = modifier,
        state = loginViewModel.state,
        navToMainGraph = {
            navHostController.popBackStack()
            navHostController.navigate(Graph.MAIN)
        },
        dispatch = loginViewModel::dispatch,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun LoginScreen(
    modifier: Modifier = Modifier,
    state: AuthState,
    navToMainGraph: () -> Unit = {},
    dispatch: (AuthAction) -> Unit,
) {
    var currUrl by rememberSaveable { mutableStateOf(generateWebViewUrl(true)) }
    LaunchedEffect(state.isLogin) {
        if (state.isLogin) {
            navToMainGraph()
        }
    }
    Screen(
        modifier = modifier.statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    Button(onClick = {
                        getCodeVer()
                        currUrl = generateWebViewUrl(false)
                    }) {
                        Text(text = "登录")
                    }
                    Button(onClick = {
                        getCodeVer()
                        currUrl = generateWebViewUrl(true)
                    }) {
                        Text(text = "注册")
                    }
                }
            )
        }
    ) {
        val webViewState = rememberWebViewState(url = currUrl)
        when (webViewState.loadingState) {
            LoadingState.Finished -> {}

            LoadingState.Initializing -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            is LoadingState.Loading -> LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = (webViewState.loadingState as LoadingState.Loading).progress
            )
        }
        WebView(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            state = webViewState,
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
                    if (checkUri(dispatch, request?.url!!)) {
                        return true
                    }
                    return super.shouldOverrideUrlLoading(view, request)
                }
            }
        )
    }
}