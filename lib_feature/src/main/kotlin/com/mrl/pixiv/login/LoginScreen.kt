package com.mrl.pixiv.login

import android.annotation.SuppressLint
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
import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common.util.RString
import com.mrl.pixiv.common.viewmodel.asState
import org.koin.androidx.compose.koinViewModel


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = koinViewModel(),
    navHostController: NavHostController = LocalNavigator.currentOrThrow,
) {
    LoginScreen(
        modifier = modifier,
        state = loginViewModel.asState(),
        navToHome = {
            navHostController.navigate(Destination.HomeScreen) {
                popUpTo(Destination.LoginScreen) { inclusive = true }
            }
        },
        dispatch = loginViewModel::dispatch,
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun LoginScreen(
    modifier: Modifier = Modifier,
    state: LoginState,
    navToHome: () -> Unit = {},
    dispatch: (LoginAction) -> Unit,
) {
    var currUrl by rememberSaveable { mutableStateOf(generateWebViewUrl(true)) }
    LaunchedEffect(state.isLogin) {
        if (state.isLogin) {
            navToHome()
        }
    }
    Scaffold(
        modifier = modifier
            .imePadding(),
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    Button(
                        onClick = {
                            currUrl = generateWebViewUrl(false)
                        }
                    ) {
                        Text(text = stringResource(RString.sign_in))
                    }
                    Button(
                        onClick = {
                            currUrl = generateWebViewUrl(true)
                        }
                    ) {
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