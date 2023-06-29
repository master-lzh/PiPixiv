package com.mrl.pixiv.repository.local

import com.mrl.pixiv.datasource.local.UserAuthDataSource
import com.mrl.pixiv.datasource.local.UserInfoDataSource
import kotlinx.coroutines.flow.zip

class UserLocalRepository constructor(
    private val userAuthDataSource: UserAuthDataSource,
    private val userInfoDataSource: UserInfoDataSource,
) {
    val userRefreshToken = userAuthDataSource.userRefreshToken
    suspend fun setUserRefreshToken(refreshToken: String) =
        userAuthDataSource.setUserRefreshToken(refreshToken)

    val userAccessToken = userAuthDataSource.userAccessToken
    suspend fun setUserAccessToken(accessToken: String) =
        userAuthDataSource.setUserAccessToken(accessToken)

    val accessTokenExpiresTime = userAuthDataSource.accessTokenExpiresTime
    suspend fun setAccessTokenExpiresTime(expiresTime: Long) =
        userAuthDataSource.setAccessTokenExpiresTime(expiresTime)

    val isLogin = userAccessToken.zip(userRefreshToken) { it1, it2 ->
        it1.isNotEmpty() && it2.isNotEmpty()
    }
    val isNeedRefreshToken = accessTokenExpiresTime.zip(userAccessToken) { it1, it2 ->
        !(it1 > System.currentTimeMillis() && it2.isNotEmpty())
    }

    val userId = userInfoDataSource.userId
    suspend fun setUserId(userId: Long) = userInfoDataSource.setUserId(userId)

}