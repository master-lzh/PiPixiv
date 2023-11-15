package com.mrl.pixiv.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey

class UserInfoDataSource(
    userInfoDataSource: DataStore<Preferences>,
) : BaseDataSource(userInfoDataSource) {
    companion object {
        val KEY_USER_INFO = stringPreferencesKey("user_info")
    }

    var userInfo: ObjectFiled = createObjectFiled(KEY_USER_INFO)
}