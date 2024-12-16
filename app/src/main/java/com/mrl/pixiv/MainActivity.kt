package com.mrl.pixiv

import android.app.ActivityManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.content.getSystemService
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.asImage
import coil3.compose.setSingletonImageLoaderFactory
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.allowRgb565
import com.mrl.pixiv.common.activity.BaseActivity
import com.mrl.pixiv.common.data.HttpClientEnum
import com.mrl.pixiv.common.lifecycle.OnLifecycle
import com.mrl.pixiv.navigation.root.RootNavigationGraph
import com.mrl.pixiv.setting.viewmodel.SettingViewModel
import com.mrl.pixiv.splash.viewmodel.SplashAction
import com.mrl.pixiv.splash.viewmodel.SplashViewModel
import com.mrl.pixiv.theme.PiPixivTheme
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.compose.KoinContext
import org.koin.core.qualifier.named
import kotlin.time.Duration.Companion.minutes

class MainActivity : BaseActivity() {
    private val splashViewModel: SplashViewModel by viewModel()
    private val settingViewModel: SettingViewModel by viewModel()
    private val imageHttpClient: HttpClient by inject(named(HttpClientEnum.IMAGE))


    @Composable
    override fun BuildContent() {
        KoinContext {
            val errorImage =
                AppCompatResources.getDrawable(this, R.drawable.ic_error_outline_24)?.asImage()
            setSingletonImageLoaderFactory { context ->
                ImageLoader.Builder(context)
                    .error(errorImage)
                    .allowRgb565(getSystemService<ActivityManager>()!!.isLowRamDevice)
                    .diskCache(CoilDiskCache.get(this))
                    .memoryCache(CoilMemoryCache.get(this))
                    .components {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            add(AnimatedImageDecoder.Factory())
                        } else {
                            add(GifDecoder.Factory())
                        }
                        add(KtorNetworkFetcherFactory(imageHttpClient))
                    }
                    // Coil spawns a new thread for every image load by default
                    .fetcherCoroutineContext(Dispatchers.IO.limitedParallelism(8))
                    .decoderCoroutineContext(Dispatchers.IO.limitedParallelism(2))
                    .build()
            }
            LaunchedEffect(Unit) {
                while (true) {
                    delay(30.minutes)
                    splashViewModel.dispatch(SplashAction.RefreshAccessTokenIntent)
                }
            }
            LaunchedEffect(Unit) {
                handleIntent(intent)
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                splashViewModel.state().isLoading
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        splashViewModel.intent.update {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }
}