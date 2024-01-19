package com.mrl.pixiv.data.search

import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.Tag
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchIllustResp (
    val illusts: List<Illust>,

    @SerialName("next_url")
    val nextUrl: String,

    @SerialName("search_span_limit")
    val searchSpanLimit: Long,

    @SerialName("show_ai")
    val showAi: Boolean
)

@Serializable
data class SearchAutoCompleteResp (
    val tags: List<Tag>
)