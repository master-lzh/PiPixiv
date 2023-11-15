package com.mrl.pixiv.datasource.remote

import com.mrl.pixiv.api.UserApi
import com.mrl.pixiv.data.user.UserBookmarksIllustQuery
import com.mrl.pixiv.data.user.UserBookmarksNovelQuery
import com.mrl.pixiv.data.user.UserDetailQuery
import com.mrl.pixiv.data.user.UserFollowAddReq
import com.mrl.pixiv.data.user.UserFollowDeleteReq
import com.mrl.pixiv.data.user.UserIllustsQuery

class UserHttpService(
    private val userApi: UserApi,
) {
    suspend fun getUserDetail(userDetailQuery: UserDetailQuery) =
        userApi.getUserDetail(userDetailQuery.toMap())

    suspend fun getUserIllusts(userIllustsQuery: UserIllustsQuery) =
        userApi.getUserIllusts(userIllustsQuery.toMap())

    suspend fun getUserBookmarksIllust(userBookmarksIllustQuery: UserBookmarksIllustQuery) =
        userApi.getUserBookmarksIllust(userBookmarksIllustQuery.toMap())

    suspend fun getUserBookmarksNovels(userBookmarksNovelQuery: UserBookmarksNovelQuery) =
        userApi.getUserBookmarksNovel(userBookmarksNovelQuery.toMap())

    suspend fun followUser(userFollowAddReq: UserFollowAddReq) =
        userApi.followUser(userFollowAddReq.toMap())

    suspend fun unFollowUser(userFollowDeleteReq: UserFollowDeleteReq) =
        userApi.unFollowUser(userFollowDeleteReq.toMap())
}