package com.mrl.pixiv.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

class UserAuthDataSource constructor(
    userAuthDataStore: DataStore<Preferences>,
) : BaseDataSource(userAuthDataStore) {
    companion object {
        val KEY_USER_REFRESH_TOKEN = stringPreferencesKey("user_refresh_token")
        val KEY_USER_ACCESS_TOKEN = stringPreferencesKey("user_access_token")
        val KEY_ACCESS_TOKEN_EXPIRES_TIME = longPreferencesKey("access_token_expires_time")
    }

    val userRefreshToken = get(KEY_USER_REFRESH_TOKEN)

    val userAccessToken = get(KEY_USER_ACCESS_TOKEN)

    val accessTokenExpiresTime = get(KEY_ACCESS_TOKEN_EXPIRES_TIME)

    suspend fun setUserRefreshToken(refreshToken: String) = set(KEY_USER_REFRESH_TOKEN, refreshToken)

    suspend fun setUserAccessToken(accessToken: String) = set(KEY_USER_ACCESS_TOKEN, accessToken)

    suspend fun setAccessTokenExpiresTime(expiresTime: Long) = set(KEY_ACCESS_TOKEN_EXPIRES_TIME, expiresTime)
}