package com.mrl.pixiv.data.search

import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream

object SearchSerializer : Serializer<Search> {
    override val defaultValue: Search = Search.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): Search = Search.parseFrom(input)

    override suspend fun writeTo(t: Search, output: OutputStream) = t.writeTo(output)
}
