package com.mrl.pixiv

import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.mrl.pixiv.common.base.BaseActivity
import com.mrl.pixiv.common.compose.OnLifecycle
import com.mrl.pixiv.navigation.root.RootNavigationGraph
import com.mrl.pixiv.splash.viewmodel.SplashAction
import com.mrl.pixiv.splash.viewmodel.SplashViewModel
import com.mrl.pixiv.theme.PiPixivTheme
import kotlinx.coroutines.delay
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.time.Duration.Companion.minutes

class MainActivity : BaseActivity() {
    private val splashViewModel: SplashViewModel by viewModel()

    @Composable
    override fun BuildContent() {
        LaunchedEffect(Unit) {
            while (true) {
                delay(30.minutes)
                splashViewModel.dispatch(SplashAction.RefreshAccessTokenIntent)
            }
        }
        OnLifecycle(onLifecycle = splashViewModel::onStart)
        PiPixivTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                splashViewModel.state.startDestination?.let {
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
                splashViewModel.state.isLoading
            }
        }
        super.onCreate(savedInstanceState)
    }
}