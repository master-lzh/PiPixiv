package com.mrl.pixiv.network

import android.os.Build
import android.util.Log
import com.mrl.pixiv.common.coroutine.launchIO
import com.mrl.pixiv.data.setting.UserPreference
import com.mrl.pixiv.domain.auth.RefreshUserAccessTokenUseCase
import com.mrl.pixiv.repository.local.SettingLocalRepository
import com.mrl.pixiv.repository.local.UserLocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Converter
import retrofit2.Retrofit
import java.net.SocketTimeoutException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

class HttpManager(
    private val userLocalRepository: UserLocalRepository,
    settingLocalRepository: SettingLocalRepository,
    private val jsonConvertFactory: Converter.Factory,
): KoinComponent {
    private var allSetting: UserPreference

    private val refreshUserAccessTokenUseCase: RefreshUserAccessTokenUseCase by inject()
    init {
        allSetting = settingLocalRepository.allSettingsSync
        launchIO {
            userLocalRepository.userAccessToken.collect {
                token = it
            }
        }
    }

    private lateinit var token: String
    private val enableBypassSniffing: Boolean = allSetting.enableBypassSniffing
    private val imageHost: String = allSetting.imageHost.ifEmpty { IMAGE_HOST }
    private val hostnameVerifier = HostnameVerifier { hostname, session ->
        // 检查主机名是否是你期望连接的IP地址或域名
        hostname in hostMap.keys || hostname in hostMap.values || hostname == imageHost || hostname == "doh.dns.sb"
    }

    private val logInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }


    companion object {
        private const val TAG = "HttpManager"
        private const val HashSalt =
            "28c1fdd170a5204386cb1313c7077b34f83e4aaf4aa829ce78c231e05b0bae2c"
        private const val API_HOST = "app-api.pixiv.net"
        private const val IMAGE_HOST = "i.pximg.net"
        private const val STATIC_IMAGE_HOST = "s.pximg.net"
        private const val AUTH_HOST = "oauth.secure.pixiv.net"
        private val hostMap: Map<String, String> = mapOf(
            API_HOST to "210.140.131.199",
            AUTH_HOST to "210.140.131.219",
            IMAGE_HOST to "210.140.92.144",
            STATIC_IMAGE_HOST to "210.140.92.143",
            "doh" to "doh.dns.sb",
        )

    }


    private fun encode(text: String): String {
        try {
            val instance: MessageDigest = MessageDigest.getInstance("MD5")
            val digest: ByteArray = instance.digest(text.toByteArray())
            val sb = StringBuffer()
            for (b in digest) {
                val i: Int = b.toInt() and 0xff
                var hexString = Integer.toHexString(i)
                if (hexString.length < 2) {
                    hexString = "0$hexString"
                }
                sb.append(hexString)
            }
            return sb.toString()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    private val commonHeaderInterceptor by lazy {
        Interceptor { chain ->
            val requestBuilder = addAuthHeader(chain)
            val request = requestBuilder
                .header("Host", API_HOST)
                .build()
            val response = switchHostResponse(chain, request)
            if (response.code in 400..499) {
                response.close()
                Log.d(TAG, "commonHeaderInterceptor: ${response.code}")
                Log.d(TAG, "current Thread: ${Thread.currentThread().name}")
                runBlocking(Dispatchers.IO) {
                    Log.d(TAG, "current Thread: ${Thread.currentThread().name}")
                    refreshUserAccessTokenUseCase {
                        token = it
                    }
                }
                val newRequest = addAuthHeader(chain).header("Host", API_HOST).build()
                return@Interceptor switchHostResponse(chain, newRequest)
            }
            response
        }
    }

    private fun addAuthHeader(chain: Interceptor.Chain): Request.Builder {
        val local = Locale.getDefault()
        val ISO8601_DATETIME_FORMAT =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", local)
        val isoDate = ISO8601_DATETIME_FORMAT.format(Date())
        val original = chain.request()
        return original.newBuilder()
            .removeHeader("User-Agent")
            .addHeader(
                "User-Agent",
                "PixivAndroidApp/5.0.166 (Android ${Build.VERSION.RELEASE}; ${Build.MODEL})"
            )
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Accept-Language", "${local.language}_${local.country}")
            .addHeader("App-OS", "Android")
            .addHeader("App-OS-Version", Build.VERSION.RELEASE)
            .header("App-Version", "5.0.166")
            .addHeader("X-Client-Time", isoDate)
            .addHeader("X-Client-Hash", encode("$isoDate$HashSalt"))
            .header("Host", API_HOST)
    }

    private fun switchHostResponse(chain: Interceptor.Chain, request: Request) = try {
        chain.proceed(request)
    } catch (e: SocketTimeoutException) {
        val host = request.url.host
        val newUrl = request.url.newBuilder()
            .host(hostMap[host] ?: host)
            .build()
        val newRequest = request.newBuilder().url(newUrl).header("Host", host).build()
        Log.d(TAG, "switchHostResponse: $newRequest")
        chain.proceed(newRequest)
    }

    private val imageHeaderInterceptor by lazy {
        Interceptor { chain ->
            val original = chain.request()
            val url = original.url.newBuilder()
                .host(imageHost)
                .build()
            val requestBuilder = original.newBuilder()
                .url(url)
                .removeHeader("Referer")
                .addHeader("Referer", "https://app-api.pixiv.net/")
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    private val baseOkHttpClient by lazy {
        OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(logInterceptor)
            .hostnameVerifier(hostnameVerifier)
            .build()
    }

    val imageOkHttpClient by lazy {
        baseOkHttpClient.newBuilder()
            .addInterceptor(imageHeaderInterceptor)
            .build()
    }

    private val commonOkHttpClient by lazy {
        baseOkHttpClient.newBuilder()
            .addInterceptor(commonHeaderInterceptor)
            .build()
    }

    private val authRetrofit by lazy {
        val authHttpClient = commonOkHttpClient.newBuilder()
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .header("Host", AUTH_HOST)
                    .removeHeader("Authorization")
                    .build()
                switchHostResponse(chain, req)
            }.build()
        Retrofit.Builder()
            .baseUrl("https://${if (enableBypassSniffing) AUTH_HOST else hostMap[AUTH_HOST]}")
            .addConverterFactory(jsonConvertFactory)
            .client(authHttpClient)
            .build()
    }

    private val commonRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://${if (enableBypassSniffing) API_HOST else hostMap[API_HOST]}")
            .addConverterFactory(jsonConvertFactory)
            .client(commonOkHttpClient)
            .build()
    }

    fun <T> getAuthService(kClass: Class<T>): T = authRetrofit.create(kClass)

    fun <T> getCommonService(kClass: Class<T>): T = commonRetrofit.create(kClass)
}