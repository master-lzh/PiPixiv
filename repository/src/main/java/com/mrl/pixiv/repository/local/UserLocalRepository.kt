package com.mrl.pixiv.repository.local

import com.mrl.pixiv.datasource.local.UserAuthDataSource
import kotlinx.coroutines.flow.zip

class UserLocalRepository constructor(
    private val userAuthDataSource: UserAuthDataSource,
) {
    val userRefreshToken = userAuthDataSource.userRefreshToken
    val userAccessToken = userAuthDataSource.userAccessToken
    val accessTokenExpiresTime = userAuthDataSource.accessTokenExpiresTime
    val isLogin = userAccessToken.zip(userRefreshToken) { it1, it2 ->
        it1.isNotEmpty() && it2.isNotEmpty()
    }
    val isNeedRefreshToken = accessTokenExpiresTime.zip(userAccessToken) { it1, it2 ->
        !(it1 > System.currentTimeMillis() && it2.isNotEmpty())
    }

    suspend fun setUserRefreshToken(refreshToken: String) =
        userAuthDataSource.setUserRefreshToken(refreshToken)

    suspend fun setUserAccessToken(accessToken: String) =
        userAuthDataSource.setUserAccessToken(accessToken)

    suspend fun setAccessTokenExpiresTime(expiresTime: Long) =
        userAuthDataSource.setAccessTokenExpiresTime(expiresTime)

}