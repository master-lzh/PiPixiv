package com.mrl.pixiv.common.data.setting

import androidx.datastore.core.okio.OkioSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
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
        ProtoBuf.decodeFromByteArray(source.readByteArray())


    override suspend fun writeTo(t: UserPreference, sink: BufferedSink) {
        sink.use {
            it.write(ProtoBuf.encodeToByteArray(t))
        }
    }
}