package com.mrl.pixiv.login

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.compose.LocalNavigator
import com.mrl.pixiv.common.util.loginToMainScreen
import com.multiplatform.webview.web.*
import org.koin.androidx.compose.koinViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoginScreen(
    startUrl: String,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel(),
    navHostController: NavHostController = LocalNavigator.current,
) {
    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect {
            when (it) {
                is LoginEvent.NavigateToMain -> {
                    navHostController.loginToMainScreen()
                }
            }
        }
    }
    val webViewState = rememberWebViewState(url = startUrl)
    val loadingState = webViewState.loadingState

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
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            when (loadingState) {
                LoadingState.Finished -> {}

                LoadingState.Initializing -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                is LoadingState.Loading -> LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = { loadingState.progress }
                )
            }
            WebView(
                modifier = Modifier.weight(1f),
                state = webViewState,
                onCreated = { webview ->
                    webview.settings.apply {
                        javaScriptEnabled = true
                        cacheMode = WebSettings.LOAD_DEFAULT
                        userAgentString = userAgentString.replace("; wv", "")
                    }
                },
                platformWebViewParams = PlatformWebViewParams(
                    client = object : AccompanistWebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            Log.d("LoginScreen", "shouldOverrideUrlLoading: ${request?.url}")
                            val codePair = checkUri(request?.url!!)
                            if (codePair != null) {
                                viewModel.dispatch(
                                    LoginAction.Login(codePair.first, codePair.second)
                                )
                                return true
                            }
                            return super.shouldOverrideUrlLoading(view, request)
                        }
                    },
                )
            )
        }

    }
}