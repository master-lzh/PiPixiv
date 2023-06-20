package com.mrl.pixiv.data.illust

import androidx.annotation.StringDef
import com.mrl.pixiv.data.IBaseFieldMap
import com.mrl.pixiv.data.IBaseQueryMap

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

@StringDef(Restrict.PUBLIC, Restrict.PRIVATE, Restrict.ALL)
annotation class Restrict {
    companion object {
        const val PUBLIC = "public"
        const val PRIVATE = "private"
        const val ALL = "all"
    }
}
