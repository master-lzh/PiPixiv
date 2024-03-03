package com.mrl.pixiv.repository.local

import com.mrl.pixiv.data.user.UserInfo
import com.mrl.pixiv.datasource.local.UserAuthDataSource
import com.mrl.pixiv.datasource.local.UserInfoDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.zip

class UserLocalRepository(
    private val userAuthDataSource: UserAuthDataSource,
    private val userInfoDataSource: UserInfoDataSource,
) {
    val userRefreshToken = userAuthDataSource.userRefreshToken.get("")
    fun setUserRefreshToken(userRefreshToken: String) =
        userAuthDataSource.userRefreshToken.set(userRefreshToken)


    val userAccessToken = userAuthDataSource.userAccessToken.get("")
    fun setUserAccessToken(userAccessToken: String) =
        userAuthDataSource.userAccessToken.set(userAccessToken)


    private val accessTokenExpiresTime = userAuthDataSource.accessTokenExpiresTime.get(0L)
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