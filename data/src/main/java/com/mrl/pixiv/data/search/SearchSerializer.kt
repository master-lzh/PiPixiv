package com.mrl.pixiv.data.search

import androidx.datastore.core.okio.OkioSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import okio.BufferedSink
import okio.BufferedSource
import okio.use

@Serializable
data class Search(
    val searchHistoryList: List<SearchHistory>,
) {
    companion object {
        val defaultInstance = Search(
            searchHistoryList = emptyList(),
        )
    }

}

@Serializable
data class SearchHistory(
    val keyword: String,
    val timestamp: Long,
)

object SearchSerializer : OkioSerializer<Search> {
    override val defaultValue: Search = Search.defaultInstance

    override suspend fun readFrom(source: BufferedSource): Search =
        ProtoBuf.decodeFromByteArray(source.readByteArray())

    override suspend fun writeTo(t: Search, sink: BufferedSink) {
        sink.use {
            it.write(ProtoBuf.encodeToByteArray(t))
        }
    }
}
