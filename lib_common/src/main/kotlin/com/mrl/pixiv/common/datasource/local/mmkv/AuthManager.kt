package com.mrl.pixiv.common.datasource.local.mmkv

import com.mrl.pixiv.common.coroutine.withIOContext
import com.mrl.pixiv.common.data.auth.AuthTokenFieldReq
import com.mrl.pixiv.common.data.auth.GrantType
import com.mrl.pixiv.common.mmkv.MMKVUser
import com.mrl.pixiv.common.mmkv.mmkvLong
import com.mrl.pixiv.common.mmkv.mmkvString
import com.mrl.pixiv.common.repository.PixivRepository
import com.mrl.pixiv.common.util.currentTimeMillis

object AuthManager : MMKVUser {
    private var userRefreshToken by mmkvString()
    private var userAccessToken by mmkvString()
    private var accessTokenExpiresTime: Long by mmkvLong()

    suspend fun requireUserAccessToken(): String =
        withIOContext {
            return@withIOContext runCatching {
                if (userAccessToken.isEmpty()) {
                    throw IllegalStateException("User access token is empty")
                }
                if (isNeedRefreshToken) {
                    val resp = PixivRepository.refreshToken(
                        AuthTokenFieldReq(
                            grantType = GrantType.REFRESH_TOKEN.value,
                            refreshToken = userRefreshToken
                        )
                    )
                    userAccessToken = resp.accessToken
                    userRefreshToken = resp.refreshToken
                    accessTokenExpiresTime = currentTimeMillis() + resp.expiresIn * 1000
                }
                userAccessToken
            }.getOrNull().orEmpty()
        }

    val hasTokens: Boolean
        get() = userRefreshToken.isNotEmpty() && userAccessToken.isNotEmpty()

    val isNeedRefreshToken: Boolean
        get() = accessTokenExpiresTime != 0L && accessTokenExpiresTime <= currentTimeMillis()

    val isLogin: Boolean
        get() = hasTokens && accessTokenExpiresTime > currentTimeMillis()
}