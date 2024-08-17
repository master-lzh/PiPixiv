package com.mrl.pixiv.data.setting

import androidx.datastore.core.okio.OkioSerializer
import com.mrl.pixiv.data.JSON
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import okio.BufferedSink
import okio.BufferedSource
import okio.use

@Serializable
data class UserPreference(
    val theme: String,
    val enableBypassSniffing: Boolean,
    val isR18Enabled: Boolean,
    val imageHost: String,
    val hasShowBookmarkTip: Boolean,
) {
    companion object {
        val defaultInstance = UserPreference(
            theme = SettingTheme.SYSTEM.name,
            enableBypassSniffing = false,
            isR18Enabled = false,
            imageHost = "i.pximg.net",
            hasShowBookmarkTip = false,
        )
    }
}

object UserPreferenceSerializer : OkioSerializer<UserPreference> {
    override val defaultValue: UserPreference = UserPreference.defaultInstance

    override suspend fun readFrom(source: BufferedSource): UserPreference =
        JSON.decodeFromString(source.readUtf8())


    override suspend fun writeTo(t: UserPreference, sink: BufferedSink) {
        sink.use {
            it.writeUtf8(JSON.encodeToString(t))
        }
    }
}