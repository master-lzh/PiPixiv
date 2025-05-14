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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.util.loginToMainScreen
import com.mrl.pixiv.common.viewmodel.asState
import org.koin.androidx.compose.koinViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoginScreen(
    startUrl: String,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = koinViewModel(),
    navHostController: NavHostController = LocalNavigator.current,
) {
    val state = loginViewModel.asState()
    LaunchedEffect(state.isLogin) {
        if (state.isLogin) {
            navHostController.loginToMainScreen()
        }
    }
    val webViewState = rememberWebViewState(url = startUrl)
    when (webViewState.loadingState) {
        LoadingState.Finished -> {}

        LoadingState.Initializing -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        is LoadingState.Loading -> LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            progress = (webViewState.loadingState as LoadingState.Loading).progress
        )
    }
    Scaffold(
        modifier = modifier
            .imePadding(),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navHostController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
            )
        }
    ) {
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
                    val codePair = checkUri(request?.url!!)
                    if (codePair != null) {
                        loginViewModel.dispatch(LoginAction.Login(codePair.first, codePair.second))
                        return true
                    }
                    return super.shouldOverrideUrlLoading(view, request)
                }
            }
        )
    }
}