package com.mrl.pixiv

import android.app.Application
import android.content.Context
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import com.mrl.pixiv.data.setting.setAppCompatDelegateThemeMode
import com.mrl.pixiv.di.allModule
import com.mrl.pixiv.domain.setting.GetAppThemeUseCase
import com.mrl.pixiv.util.AppUtil
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okio.Path.Companion.toOkioPath
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    companion object {
        lateinit var instance: App
    }

    private val getAppThemeUseCase: GetAppThemeUseCase by inject()


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        instance = this
        AppUtil.init(this)
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(allModule)
        }
        GlobalScope.launch {
            setAppCompatDelegateThemeMode(getAppThemeUseCase().first())
        }
    }
}

internal object CoilDiskCache {
    private const val FOLDER_NAME = "image_cache"
    private var instance: DiskCache? = null

    @Synchronized
    fun get(context: PlatformContext): DiskCache {
        return instance ?: run {
            val safeCacheDir = context.cacheDir.apply { mkdirs() }
            // Create the singleton disk cache instance.
            DiskCache.Builder()
                .directory(safeCacheDir.resolve(FOLDER_NAME).toOkioPath())
                .build()
                .also { instance = it }
        }
    }
}

internal object CoilMemoryCache {
    private var instance: MemoryCache? = null

    fun get(context: PlatformContext): MemoryCache {
        return instance ?: run {
            MemoryCache.Builder()
                .maxSizePercent(context, 0.25)
                .build()
                .also { instance = it }
        }
    }
}
