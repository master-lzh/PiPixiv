package com.mrl.pixiv.common.data.user

import com.mrl.pixiv.common.data.Filter
import com.mrl.pixiv.common.data.IBaseQueryMap
import com.mrl.pixiv.common.data.Restrict

data class UserDetailQuery(
    val filter: String = Filter.ANDROID.value,
    val userId: Long,
) : IBaseQueryMap

data class UserIllustsQuery(
    val filter: String = Filter.ANDROID.value,
    val userId: Long,
    val type: String,
) : IBaseQueryMap

data class UserBookmarksIllustQuery(
    @Restrict
    val restrict: String,
    val userId: Long,
    val tag: String = "",
    val maxBookmarkId: Long? = null,
) : IBaseQueryMap

data class UserBookmarksNovelQuery(
    @Restrict
    val restrict: String,
    val userId: Long,
    val tag: String = "",
) : IBaseQueryMap

data class UserFollowAddReq(
    val userId: Long,
    val restrict: String,
) : IBaseQueryMap

data class UserFollowDeleteReq(
    val userId: Long,
) : IBaseQueryMap

data class UserBookmarkTagsQuery(
    val userId: Long,
    @Restrict
    val restrict: String,
) : IBaseQueryMap

data class UserFollowingQuery(
    val filter: Filter = Filter.ANDROID,
    val userId: Long,
    val restrict: String = Restrict.PUBLIC,
    val offset: Int? = null,
) : IBaseQueryMap