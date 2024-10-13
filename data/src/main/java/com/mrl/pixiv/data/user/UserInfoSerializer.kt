package com.mrl.pixiv.data.user

import androidx.datastore.core.okio.OkioSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
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
        ProtoBuf.decodeFromByteArray(source.readByteArray())

    override suspend fun writeTo(t: UserInfo, sink: BufferedSink) {
        sink.use {
            it.write(ProtoBuf.encodeToByteArray(t))
        }
    }
}