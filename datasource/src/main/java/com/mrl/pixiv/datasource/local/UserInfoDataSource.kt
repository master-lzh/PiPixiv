package com.mrl.pixiv.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey

class UserInfoDataSource(
    userInfoDataSource: DataStore<Preferences>,
) : BaseDataSource(userInfoDataSource) {
    companion object {
        val KEY_USER_ID = longPreferencesKey("user_id")
    }

    val userId = get(KEY_USER_ID)
    suspend fun setUserId(userId: Long) = set(KEY_USER_ID, userId)
}