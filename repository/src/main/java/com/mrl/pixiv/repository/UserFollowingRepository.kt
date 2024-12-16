package com.mrl.pixiv.repository

import com.mrl.pixiv.data.user.UserFollowingQuery
import com.mrl.pixiv.datasource.remote.UserHttpService
import org.koin.core.annotation.Single

@Single
class UserFollowingRepository(
    private val userHttpService: UserHttpService,
) {
    suspend fun getUserFollowing(userFollowingQuery: UserFollowingQuery) =
        userHttpService.getUserFollowing(userFollowingQuery)
}