package com.mrl.pixiv.data.search

import android.content.Context
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import java.io.InputStream
import java.io.OutputStream

object SearchSerializer : Serializer<Search> {
    override val defaultValue: Search = Search.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): Search = Search.parseFrom(input)

    override suspend fun writeTo(t: Search, output: OutputStream) = t.writeTo(output)
}

val Context.searchDataStore by dataStore(
    fileName = "search.pb",
    serializer = SearchSerializer
)