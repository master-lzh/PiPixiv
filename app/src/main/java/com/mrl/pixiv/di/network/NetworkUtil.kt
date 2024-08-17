package com.mrl.pixiv.di.network

import androidx.compose.ui.text.intl.Locale
import com.mrl.pixiv.data.setting.UserPreference
import com.mrl.pixiv.datasource.TokenManager
import com.mrl.pixiv.domain.auth.RefreshUserAccessTokenUseCase
import com.mrl.pixiv.repository.SettingRepository
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.utils.io.core.toByteArray
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate.Formats.ISO
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.alternativeParsing
import kotlinx.datetime.format.char
import kotlinx.datetime.offsetIn
import kotlinx.datetime.toLocalDateTime
import okio.ByteString.Companion.toByteString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal const val TAG = "HttpManager"
internal const val HashSalt =
    "28c1fdd170a5204386cb1313c7077b34f83e4aaf4aa829ce78c231e05b0bae2c"
internal const val API_HOST = "app-api.pixiv.net"
internal const val IMAGE_HOST = "i.pximg.net"
internal const val STATIC_IMAGE_HOST = "s.pximg.net"
internal const val AUTH_HOST = "oauth.secure.pixiv.net"
internal val hostMap: Map<String, String> = mapOf(
    API_HOST to "210.140.131.199",
    AUTH_HOST to "210.140.131.219",
    IMAGE_HOST to "210.140.92.144",
    STATIC_IMAGE_HOST to "210.140.92.143",
    "doh" to "doh.dns.sb",
)

internal val iso8601DateTimeFormat = LocalDateTime.Format {
    date(ISO)
    alternativeParsing({
        char('t')
    }) {
        char('T')
    }
    hour()
    char(':')
    minute()
    char(':')
    second()
}

internal fun encode(text: String): String {
    try {
        val md5 = text.toByteArray().toByteString().md5()
        return md5.hex()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}

fun addAuthHeader(request: HttpRequestBuilder) {
    val local = Locale.current
    val instantNow = Clock.System.now()
    val isoDate = "${
        instantNow.toLocalDateTime(TimeZone.currentSystemDefault())
            .format(iso8601DateTimeFormat)
    }${instantNow.offsetIn(TimeZone.currentSystemDefault())}"
    request.headers.apply {
        remove("User-Agent")
        set(
            "User-Agent",
            "PixivAndroidApp/5.0.166 (Android 14; 2210132C)"
        )
//            if (request.host != AUTH_HOST || request.host != hostMap[AUTH_HOST]) {
        set("Authorization", "Bearer ${TokenManager.token}")
//            }
        set("Accept-Language", "${local.language}_${local.region}")
        set("App-OS", "Android")
        set("App-OS-Version", "14")
        set("App-Version", "5.0.166")
        set("X-Client-Time", isoDate)
        set("X-Client-Hash", encode("$isoDate$HashSalt"))
//            set("Host", request.host)
    }

}

object NetworkUtil : KoinComponent {
    private val settingRepository: SettingRepository by inject()
    val refreshUserAccessTokenUseCase: RefreshUserAccessTokenUseCase by inject()
    private val allSetting: UserPreference = settingRepository.allSettingsSync
    val enableBypassSniffing: Boolean = allSetting.enableBypassSniffing
    val imageHost: String = allSetting.imageHost.ifEmpty { IMAGE_HOST }
}
