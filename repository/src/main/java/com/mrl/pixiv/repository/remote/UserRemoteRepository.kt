package com.mrl.pixiv.repository.remote

import com.mrl.pixiv.data.user.UserBookmarksIllustQuery
import com.mrl.pixiv.data.user.UserDetailQuery
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
}