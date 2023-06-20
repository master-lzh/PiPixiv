package com.mrl.pixiv

import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.mrl.pixiv.common.base.BaseActivity
import com.mrl.pixiv.common.ui.BaseScreen
import com.mrl.pixiv.navigation.root.RootNavigationGraph
import com.mrl.pixiv.theme.PiPixivTheme

class MainActivity : BaseActivity() {
    @Composable
    override fun BuildContent() {
        PiPixivTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                RootNavigationGraph(
                    navHostController = rememberNavController(),
                    startDestination =
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                true
            }
        }
        super.onCreate(savedInstanceState)
    }
}