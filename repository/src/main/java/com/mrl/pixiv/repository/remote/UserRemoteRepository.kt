package com.mrl.pixiv.repository.remote

import com.mrl.pixiv.data.user.UserBookmarksIllustQuery
import com.mrl.pixiv.data.user.UserBookmarksNovelQuery
import com.mrl.pixiv.data.user.UserDetailQuery
import com.mrl.pixiv.data.user.UserFollowAddReq
import com.mrl.pixiv.data.user.UserFollowDeleteReq
import com.mrl.pixiv.data.user.UserIllustsQuery
import com.mrl.pixiv.datasource.remote.UserHttpService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn

class UserRemoteRepository(
    private val userHttpService: UserHttpService,
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun getUserDetail(userDetailQuery: UserDetailQuery) =
        userHttpService.getUserDetail(userDetailQuery).flowOn(ioDispatcher)

    suspend fun getUserIllusts(userIllustsQuery: UserIllustsQuery) =
        userHttpService.getUserIllusts(userIllustsQuery).flowOn(ioDispatcher)

    suspend fun getUserBookmarksIllust(userBookmarksIllustQuery: UserBookmarksIllustQuery) =
        userHttpService.getUserBookmarksIllust(userBookmarksIllustQuery).flowOn(ioDispatcher)

    suspend fun getUserBookmarksNovels(userBookmarksNovelQuery: UserBookmarksNovelQuery) =
        userHttpService.getUserBookmarksNovels(userBookmarksNovelQuery).flowOn(ioDispatcher)

    suspend fun followUser(userFollowAddReq: UserFollowAddReq) =
        userHttpService.followUser(userFollowAddReq).flowOn(ioDispatcher)

    suspend fun unFollowUser(userFollowDeleteReq: UserFollowDeleteReq) =
        userHttpService.unFollowUser(userFollowDeleteReq).flowOn(ioDispatcher)
}