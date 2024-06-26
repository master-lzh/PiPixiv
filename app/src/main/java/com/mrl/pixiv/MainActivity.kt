package com.mrl.pixiv

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.mrl.pixiv.common.activity.BaseActivity
import com.mrl.pixiv.common.lifecycle.OnLifecycle
import com.mrl.pixiv.navigation.root.RootNavigationGraph
import com.mrl.pixiv.setting.viewmodel.SettingViewModel
import com.mrl.pixiv.splash.viewmodel.SplashAction
import com.mrl.pixiv.splash.viewmodel.SplashViewModel
import com.mrl.pixiv.theme.PiPixivTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.time.Duration.Companion.minutes

class MainActivity : BaseActivity() {
    private val splashViewModel: SplashViewModel by viewModel()
    private val settingViewModel: SettingViewModel by viewModel()


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
                color = MaterialTheme.colorScheme.background
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

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        splashViewModel.intent.update {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }
}