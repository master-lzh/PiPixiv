package com.mrl.pixiv.login

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.mrl.pixiv.common.router.Graph
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common.util.RString
import com.mrl.pixiv.common.viewmodel.asState
import okio.ByteString.Companion.toByteString
import org.koin.androidx.compose.koinViewModel
import kotlin.io.encoding.Base64
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
    return Base64.UrlSafe.encode(
        getCodeVer().toByteArray(Charsets.UTF_8).toByteString().sha256().toByteArray(),
    ).replace("=", "")
}

fun generateWebViewUrl(create: Boolean) =
    if (create) {
        "https://app-api.pixiv.net/web/v1/provisional-accounts/create?code_challenge=${getCodeChallenge()}&code_challenge_method=S256&client=pixiv-android"
    } else {
        "https://app-api.pixiv.net/web/v1/login?code_challenge=${getCodeChallenge()}&code_challenge_method=S256&client=pixiv-android"
    }

private fun checkUri(dispatch: (LoginAction) -> Unit, uri: Uri): Boolean {
    if (uri.scheme == "pixiv" && uri.host == "account") {
        val code = uri.getQueryParameter("code")
        code?.let { dispatch(LoginAction.Login(code, codeVerifier)) }
        return true
    }
    return false
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = koinViewModel(),
    navHostController: NavHostController = LocalNavigator.currentOrThrow,
) {
    LoginScreen(
        modifier = modifier,
        state = loginViewModel.asState(),
        navToMainGraph = {
            navHostController.popBackStack()
            navHostController.navigate(Graph.Main)
        },
        dispatch = loginViewModel::dispatch,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun LoginScreen(
    modifier: Modifier = Modifier,
    state: LoginState,
    navToMainGraph: () -> Unit = {},
    dispatch: (LoginAction) -> Unit,
) {
    var currUrl by rememberSaveable { mutableStateOf(generateWebViewUrl(true)) }
    LaunchedEffect(state.isLogin) {
        if (state.isLogin) {
            navToMainGraph()
        }
    }
    Scaffold(
        modifier = modifier
            .imePadding(),
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    Button(onClick = {
                        currUrl = generateWebViewUrl(false)
                    }) {
                        Text(text = stringResource(RString.sign_in))
                    }
                    Button(onClick = {
                        currUrl = generateWebViewUrl(true)
                    }) {
                        Text(text = stringResource(RString.sign_up))
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
                    Log.d("LoginScreen", "shouldOverrideUrlLoading: ${request?.url}")
                    if (checkUri(dispatch, request?.url!!)) {
                        return true
                    }
                    return super.shouldOverrideUrlLoading(view, request)
                }
            }
        )
    }
}