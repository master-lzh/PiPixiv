package com.mrl.pixiv.repository.remote

import com.mrl.pixiv.data.user.UserBookmarksIllustQuery
import com.mrl.pixiv.data.user.UserBookmarksNovelQuery
import com.mrl.pixiv.data.user.UserDetailQuery
import com.mrl.pixiv.data.user.UserFollowAddReq
import com.mrl.pixiv.data.user.UserFollowDeleteReq
import com.mrl.pixiv.data.user.UserIllustsQuery
import com.mrl.pixiv.datasource.remote.UserHttpService

class UserRemoteRepository(
    private val userHttpService: UserHttpService,
) {
    suspend fun getUserDetail(userDetailQuery: UserDetailQuery) =
        userHttpService.getUserDetail(userDetailQuery)

    suspend fun getUserIllusts(userIllustsQuery: UserIllustsQuery) =
        userHttpService.getUserIllusts(userIllustsQuery)

    suspend fun getUserBookmarksIllust(userBookmarksIllustQuery: UserBookmarksIllustQuery) =
        userHttpService.getUserBookmarksIllust(userBookmarksIllustQuery)

    suspend fun getUserBookmarksNovels(userBookmarksNovelQuery: UserBookmarksNovelQuery) =
        userHttpService.getUserBookmarksNovels(userBookmarksNovelQuery)

    suspend fun followUser(userFollowAddReq: UserFollowAddReq) =
        userHttpService.followUser(userFollowAddReq)

    suspend fun unFollowUser(userFollowDeleteReq: UserFollowDeleteReq) =
        userHttpService.unFollowUser(userFollowDeleteReq)
}