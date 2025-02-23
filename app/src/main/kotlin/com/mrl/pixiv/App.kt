package com.mrl.pixiv

import android.app.Application
import android.content.Context
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import com.mrl.pixiv.common.data.setting.setAppCompatDelegateThemeMode
import com.mrl.pixiv.common.datasource.local.datastore.UserAuthDataSource.Companion.KEY_ACCESS_TOKEN_EXPIRES_TIME
import com.mrl.pixiv.common.datasource.local.datastore.UserAuthDataSource.Companion.KEY_USER_ACCESS_TOKEN
import com.mrl.pixiv.common.datasource.local.datastore.UserAuthDataSource.Companion.KEY_USER_REFRESH_TOKEN
import com.mrl.pixiv.common.datasource.local.datastore.userAuthDataStore
import com.mrl.pixiv.common.domain.setting.GetAppThemeUseCase
import com.mrl.pixiv.common.util.AppUtil
import com.mrl.pixiv.common.util.initializeFirebase
import com.mrl.pixiv.common.util.isFileExists
import com.mrl.pixiv.di.allModule
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import okio.Path.Companion.toOkioPath
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

@OptIn(DelicateCoroutinesApi::class)
class App : Application() {
    companion object {
        lateinit var instance: App
    }

    private val getAppThemeUseCase: GetAppThemeUseCase by inject()


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        MMKV.initialize(this)
        initializeFirebase()
        AppUtil.init(this)
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(allModule)
        }
        migrateDataStoreToMMKV()
        GlobalScope.launch {
            setAppCompatDelegateThemeMode(getAppThemeUseCase().first())
        }
    }

    private fun migrateDataStoreToMMKV() {
        val mmkv = MMKV.mmkvWithID("pixiv.user")
        runBlocking(Dispatchers.IO) {
            if (filesDir.resolve("mmkv").exists()) {
                return@runBlocking
            }
            val auth = async {
                val file = filesDir.resolve("datastore/user_auth.preferences_pb")
                if (isFileExists(file)) {
                    val tokens = userAuthDataStore.data.first()
                    mmkv.encode("userAccessToken", tokens[KEY_USER_ACCESS_TOKEN] ?: "")
                    mmkv.encode("userRefreshToken", tokens[KEY_USER_REFRESH_TOKEN] ?: "")
                    mmkv.encode(
                        "accessTokenExpiresTime",
                        tokens[KEY_ACCESS_TOKEN_EXPIRES_TIME] ?: 0L
                    )
//                    deleteFiles(file)
                }
            }
            val search = async {
                val file = filesDir.resolve("datastore/search.pb")
                if (isFileExists(file)) {
                    val bytes = file.readBytes()
                    mmkv.encode("searchHistory", bytes)
//                    deleteFiles(file)
                }
            }
            val userPreference = async {
                val file = filesDir.resolve("datastore/user_preference.json")
                if (isFileExists(file)) {
                    val bytes = file.readBytes()
                    mmkv.encode("userPreference", bytes)
//                    deleteFiles(file)
                }
            }
            val userInfo = async {
                val file = filesDir.resolve("datastore/user_info.json")
                if (isFileExists(file)) {
                    val bytes = file.readBytes()
                    mmkv.encode("userInfo", bytes)
//                    deleteFiles(file)
                }
            }
            awaitAll(auth, search, userPreference, userInfo)
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
