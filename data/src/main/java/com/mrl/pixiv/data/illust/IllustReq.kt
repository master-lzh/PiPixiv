package com.mrl.pixiv.data.illust

import com.mrl.pixiv.data.IBaseFieldMap
import com.mrl.pixiv.data.IBaseQueryMap
import com.mrl.pixiv.data.Restrict

data class IllustRecommendedQuery(
    val filter: String,
    val includeRankingIllusts: Boolean,
    val includePrivacyPolicy: Boolean,
) : IBaseQueryMap

data class IllustBookmarkAddReq(
    val illustId: Long,
    @Restrict
    val restrict: String = Restrict.PUBLIC,
    val tags: List<String>? = null,
) : IBaseFieldMap

data class IllustBookmarkDeleteReq(
    val illustId: Long,
) : IBaseFieldMap

data class IllustRelatedQuery(
    val illustId: Long,
    val filter: String,
) : IBaseQueryMap

data class IllustDetailQuery(
    val filter: String,
    val illustId: Long,
) : IBaseQueryMap
