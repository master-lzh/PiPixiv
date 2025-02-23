package com.mrl.pixiv.common.repository

import com.mrl.pixiv.common.data.user.UserBookmarksIllustQuery
import com.mrl.pixiv.common.data.user.UserBookmarksNovelQuery
import com.mrl.pixiv.common.data.user.UserDetailQuery
import com.mrl.pixiv.common.data.user.UserFollowAddReq
import com.mrl.pixiv.common.data.user.UserFollowDeleteReq
import com.mrl.pixiv.common.data.user.UserIllustsQuery
import com.mrl.pixiv.common.data.user.UserInfo
import com.mrl.pixiv.common.datasource.local.datastore.UserAuthDataSource
import com.mrl.pixiv.common.datasource.local.datastore.UserInfoDataSource
import com.mrl.pixiv.common.datasource.remote.UserHttpService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.zip
import org.koin.core.annotation.Single

@Single
class UserRepository(
    private val userHttpService: UserHttpService,
    private val userAuthDataSource: UserAuthDataSource,
    private val userInfoDataSource: UserInfoDataSource,
) {
    // ---------------------remote-------------------------
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


    // ---------------------local-------------------------
    val userRefreshToken = userAuthDataSource.userRefreshToken.get()
    fun setUserRefreshToken(userRefreshToken: String) =
        userAuthDataSource.userRefreshToken.set(userRefreshToken)


    val userAccessToken = userAuthDataSource.userAccessToken.get()
    fun setUserAccessToken(userAccessToken: String) =
        userAuthDataSource.userAccessToken.set(userAccessToken)


    private val accessTokenExpiresTime = userAuthDataSource.accessTokenExpiresTime.get()
    fun setAccessTokenExpiresTime(accessTokenExpiresTime: Long) =
        userAuthDataSource.accessTokenExpiresTime.set(accessTokenExpiresTime)

    val isLogin = userAccessToken.zip(userRefreshToken) { it1, it2 ->
        it1.isNotEmpty() && it2.isNotEmpty()
    }

    val isNeedRefreshToken = accessTokenExpiresTime.zip(userAccessToken) { it1, it2 ->
        !(it1 > System.currentTimeMillis() && it2.isNotEmpty())
    }

    val userInfo = userInfoDataSource.data
    fun setUserInfo(userInfo: (UserInfo) -> UserInfo) =
        userInfoDataSource.updateData { userInfo(it) }
    suspend fun getUserId() = userInfo.first().uid
}