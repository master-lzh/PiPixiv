package com.mrl.pixiv

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.mrl.pixiv.util.AppUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

class App : Application(), ImageLoaderFactory {
    companion object {
        lateinit var instance: App
    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        AppUtil.init(this)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .error(R.drawable.ic_error_outline_24)
//            .placeholder(R.drawable.ic_downloading_24)
            .diskCache(CoilDiskCache.get(this))
            .memoryCache(CoilMemoryCache.get(this))
            .allowRgb565(getSystemService<ActivityManager>()!!.isLowRamDevice)
            // Coil spawns a new thread for every image load by default
            .fetcherDispatcher(Dispatchers.IO.limitedParallelism(8))
            .decoderDispatcher(Dispatchers.IO.limitedParallelism(2))
            .transformationDispatcher(Dispatchers.IO.limitedParallelism(2))
            .components {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }.callFactory(httpManager.imageOkHttpClient)
            .build()


}

internal object CoilDiskCache {

    private const val FOLDER_NAME = "image_cache"
    private var instance: DiskCache? = null

    @Synchronized
    fun get(context: Context): DiskCache {
        return instance ?: run {
            val safeCacheDir = context.cacheDir.apply { mkdirs() }
            // Create the singleton disk cache instance.
            DiskCache.Builder()
                .directory(safeCacheDir.resolve(FOLDER_NAME))
                .build()
                .also { instance = it }
        }
    }
}

internal object CoilMemoryCache {

    private var instance: MemoryCache? = null

    @Synchronized
    fun get(context: Context): MemoryCache {
        return instance ?: run {
            MemoryCache.Builder(context)
                .maxSizePercent(0.25)
                .build()
                .also { instance = it }
        }
    }
}