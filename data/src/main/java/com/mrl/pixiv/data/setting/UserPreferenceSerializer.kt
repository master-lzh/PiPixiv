package com.mrl.pixiv.data.setting

import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream

object UserPreferenceSerializer : Serializer<UserPreference> {
    override val defaultValue: UserPreference = UserPreference.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): UserPreference = UserPreference.parseFrom(input)

    override suspend fun writeTo(t: UserPreference, output: OutputStream) = t.writeTo(output)
}