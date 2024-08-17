package com.mrl.pixiv.data.user

import androidx.datastore.core.okio.OkioSerializer
import com.mrl.pixiv.data.JSON
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import okio.BufferedSink
import okio.BufferedSource
import okio.use

@Serializable
data class UserInfo(
    val uid: Long,
    val username: String,
    val avatar: String,
    val comment: String,
) {
    companion object {
        val defaultInstance = UserInfo(
            uid = 0,
            username = "",
            avatar = "",
            comment = "",
        )
    }

}

object UserInfoSerializer : OkioSerializer<UserInfo> {
    override val defaultValue: UserInfo = UserInfo.defaultInstance

    override suspend fun readFrom(source: BufferedSource): UserInfo =
        JSON.decodeFromString(source.readUtf8())

    override suspend fun writeTo(t: UserInfo, sink: BufferedSink) {
        sink.use {
            it.writeUtf8(JSON.encodeToString(t))
        }
    }
}