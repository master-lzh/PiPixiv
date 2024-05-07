package com.mrl.pixiv.data.user

import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream


object UserInfoSerializer : Serializer<UserInfo> {
    override val defaultValue: UserInfo = UserInfo.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): UserInfo = UserInfo.parseFrom(input)
    override suspend fun writeTo(t: UserInfo, output: OutputStream) = t.writeTo(output)
}