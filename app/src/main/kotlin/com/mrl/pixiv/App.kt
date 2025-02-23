package com.mrl.pixiv

import android.app.Application
import android.content.Context
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.initialize
import com.mrl.pixiv.common.coroutine.launchIO
import com.mrl.pixiv.common.data.search.Search
import com.mrl.pixiv.common.data.setting.UserPreference
import com.mrl.pixiv.common.data.setting.setAppCompatDelegateThemeMode
import com.mrl.pixiv.common.data.user.UserInfo
import com.mrl.pixiv.common.domain.setting.GetAppThemeUseCase
import com.mrl.pixiv.common.util.AppUtil
import com.mrl.pixiv.common.util.deleteFiles
import com.mrl.pixiv.common.util.isDebug
import com.mrl.pixiv.common.util.isFileExists
import com.mrl.pixiv.di.allModule
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
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
        migrateJsonToProtobuf()
        instance = this
        initializeFirebase()
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

    private fun migrateJsonToProtobuf() {
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
        }
        GlobalScope.launchIO {
            val search = async {
                val file = filesDir.resolve("datastore/search.json")
                if (isFileExists(file)) {
                    val text = file.readText()
                    val search = json.decodeFromString(Search.serializer(), text)
                    val byteArray = ProtoBuf.encodeToByteArray(Search.serializer(), search)
                    filesDir.resolve("datastore/search.pb").writeBytes(byteArray)
                    deleteFiles(file)
                }
            }
            val userPreference = async {
                val file = filesDir.resolve("datastore/user_preference.json")
                if (isFileExists(file)) {
                    val text = file.readText()
                    val userPreference = json.decodeFromString(UserPreference.serializer(), text)
                    val byteArray =
                        ProtoBuf.encodeToByteArray(UserPreference.serializer(), userPreference)
                    filesDir.resolve("datastore/user_preference.pb").writeBytes(byteArray)
                    deleteFiles(file)
                }
            }
            val userInfo = async {
                val file = filesDir.resolve("datastore/user_info.json")
                if (isFileExists(file)) {
                    val text = file.readText()
                    val userInfo = json.decodeFromString(UserInfo.serializer(), text)
                    val byteArray =
                        ProtoBuf.encodeToByteArray(UserInfo.serializer(), userInfo)
                    filesDir.resolve("datastore/user_info.pb").writeBytes(byteArray)
                    deleteFiles(file)
                }
            }
            search.await()
            userPreference.await()
            userInfo.await()
        }
    }

    private fun initializeFirebase() {
        Firebase.initialize(this)
        Firebase.crashlytics.isCrashlyticsCollectionEnabled = !isDebug
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
