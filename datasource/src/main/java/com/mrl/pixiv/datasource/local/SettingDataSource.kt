package com.mrl.pixiv.datasource.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.mrl.pixiv.data.setting.UserPreference
import com.mrl.pixiv.data.setting.UserPreferenceSerializer

class SettingDataSource(
    settingDataSource: DataStore<UserPreference>
) : BaseProtoDataSource<UserPreference, UserPreference.Builder>(settingDataSource) {
    override fun defaultValue(): UserPreference = UserPreference.getDefaultInstance()
}

val Context.userPreferenceDataStore by dataStore(
    fileName = "settings.pb",
    serializer = UserPreferenceSerializer
)