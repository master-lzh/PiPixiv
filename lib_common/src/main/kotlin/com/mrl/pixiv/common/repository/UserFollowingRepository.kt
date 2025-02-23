package com.mrl.pixiv.common.repository

import com.mrl.pixiv.common.data.user.UserFollowingQuery
import com.mrl.pixiv.common.datasource.remote.UserHttpService
import org.koin.core.annotation.Single

@Single
class UserFollowingRepository(
    private val userHttpService: UserHttpService,
) {
    suspend fun getUserFollowing(userFollowingQuery: UserFollowingQuery) =
        userHttpService.getUserFollowing(userFollowingQuery)
}