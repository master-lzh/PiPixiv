package com.mrl.pixiv.common.data.illust

import com.mrl.pixiv.common.data.Restrict

data class IllustRecommendedQuery(
    val filter: String,
    val includeRankingIllusts: Boolean,
    val includePrivacyPolicy: Boolean,
)

data class IllustBookmarkAddReq(
    val illustId: Long,
    @Restrict
    val restrict: String = Restrict.PUBLIC,
    val tags: List<String>? = null,
)

data class IllustBookmarkDeleteReq(
    val illustId: Long,
)

data class IllustRelatedQuery(
    val illustId: Long,
    val filter: String,
)

data class IllustDetailQuery(
    val filter: String,
    val illustId: Long,
)

data class IllustBookmarkDetailQuery(
    val illustId: Long,
) 
