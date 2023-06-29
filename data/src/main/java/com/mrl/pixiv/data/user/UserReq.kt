package com.mrl.pixiv.data.user

import com.mrl.pixiv.data.Filter
import com.mrl.pixiv.data.IBaseQueryMap

data class UserDetailQuery(
    val filter: String = Filter.ANDROID.filter,
    val userId: Long,
) : IBaseQueryMap

data class UserIllustsQuery(
    val filter: String = Filter.ANDROID.filter,
    val userId: Long,
    val type: String,
) : IBaseQueryMap

data class UserBookmarksIllustQuery(
    val restrict: String,
    val userId: Long,
    val tag: String,
) : IBaseQueryMap