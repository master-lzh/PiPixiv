package com.mrl.pixiv.data.illust

import com.mrl.pixiv.data.Illust
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IllustRecommendedResp(
    val illusts: List<Illust>,

    @SerialName("ranking_illusts")
    val rankingIllusts: List<Illust>,

    @SerialName("contest_exists")
    val contestExists: Boolean? = null,

    @SerialName("privacy_policy")
    val privacyPolicy: PrivacyPolicy,

    @SerialName("next_url")
    val nextURL: String = "",
)

@Serializable
data class IllustRelatedResp(
    val illusts: List<Illust>,

    @SerialName("next_url")
    val nextURL: String = "",
)


@Serializable
data class PrivacyPolicy(
    val version: String = "",
    val message: String = "",
    val url: String = ""
)
