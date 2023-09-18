package com.mrl.pixiv

import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.mrl.pixiv.common.base.BaseActivity
import com.mrl.pixiv.navigation.root.RootNavigationGraph
import com.mrl.pixiv.splash.SplashUiIntent
import com.mrl.pixiv.splash.SplashViewModel
import com.mrl.pixiv.theme.PiPixivTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {
    private val splashViewModel: SplashViewModel by viewModel()
    @Composable
    override fun BuildContent() {
        PiPixivTheme {
            val state by splashViewModel.uiStateFlow.collectAsStateWithLifecycle()
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                state.startDestination?.let {
                    RootNavigationGraph(
                        navHostController = rememberNavController(),
                        startDestination = it
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                splashViewModel.isLoading.value
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        splashViewModel.dispatch(SplashUiIntent.IsNeedRefreshTokenIntent)
    }
}