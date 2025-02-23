package com.mrl.pixiv.common.datasource.local.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mrl.pixiv.common.datasource.local.datastore.base.BaseDataSource
import com.mrl.pixiv.common.datasource.local.datastore.base.dataStorePath
import org.koin.core.annotation.Single

@Single(binds = [UserAuthDataSource::class])
class UserAuthDataSource : BaseDataSource(userAuthDataStore) {
    companion object {
        val KEY_USER_REFRESH_TOKEN = stringPreferencesKey("user_refresh_token")
        val KEY_USER_ACCESS_TOKEN = stringPreferencesKey("user_access_token")
        val KEY_ACCESS_TOKEN_EXPIRES_TIME = longPreferencesKey("access_token_expires_time")
    }

    val userRefreshToken = createField(KEY_USER_REFRESH_TOKEN, "")

    val userAccessToken = createField(KEY_USER_ACCESS_TOKEN, "")

    val accessTokenExpiresTime = createField(KEY_ACCESS_TOKEN_EXPIRES_TIME, 0L)
}

val userAuthDataStore = PreferenceDataStoreFactory.createWithPath {
    dataStorePath("user_auth.preferences_pb")
}
