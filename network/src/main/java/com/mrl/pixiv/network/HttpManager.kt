package com.mrl.pixiv.network

import com.mrl.pixiv.repository.local.UserLocalRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class HttpManager(
    private val userLocalRepository: UserLocalRepository,
    private val jsonConvertFactory: Converter.Factory,
) {

    private val logInterceptor by lazy { HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY } }

    companion object {
        private const val HashSalt = "28c1fdd170a5204386cb1313c7077b34f83e4aaf4aa829ce78c231e05b0bae2c"
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
            var accessToken = ""
            runBlocking {
                withTimeoutOrNull(100) {
                    accessToken = userLocalRepository.userAccessToken.first()
                }
            }
            val local = Locale.getDefault()
            val ISO8601_DATETIME_FORMAT =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", local)
            val isoDate = ISO8601_DATETIME_FORMAT.format(Date())
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .removeHeader("User-Agent")
                .addHeader(
                    "User-Agent",
                    "PixivAndroidApp/5.0.166 (Android ${android.os.Build.VERSION.RELEASE}; ${android.os.Build.MODEL})"
                )
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Accept-Language", "${local.language}_${local.country}")
                .addHeader("App-OS", "Android")
                .addHeader("App-OS-Version", android.os.Build.VERSION.RELEASE)
                .header("App-Version", "5.0.166")
                .addHeader("X-Client-Time", isoDate)
                .addHeader("X-Client-Hash", encode("$isoDate$HashSalt"))

            val request = requestBuilder.build()
            val response = try {
                chain.proceed(request)
            } catch (e: Throwable) {
                throw e
            }

            response
        }
    }

    private val imageHeaderInterceptor by lazy {
        Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
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
                val req = chain.request().newBuilder().removeHeader("Authorization").build()
                chain.proceed(req)
            }.build()
        Retrofit.Builder()
            .baseUrl("https://oauth.secure.pixiv.net")
            .addConverterFactory(jsonConvertFactory)
            .client(authHttpClient)
            .build()
    }

    private val commonRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://app-api.pixiv.net")
            .addConverterFactory(jsonConvertFactory)
            .client(commonOkHttpClient)
            .build()
    }

    fun <T> getAuthService(kClass: Class<T>): T = authRetrofit.create(kClass)

    fun <T> getCommonService(kClass: Class<T>): T = commonRetrofit.create(kClass)
}