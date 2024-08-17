package com.mrl.pixiv.datasource.local.datastore

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import com.mrl.pixiv.data.setting.UserPreference
import com.mrl.pixiv.data.setting.UserPreferenceSerializer
import com.mrl.pixiv.datasource.local.datastore.base.BaseProtoDataSource
import com.mrl.pixiv.datasource.local.datastore.base.dataStorePath
import okio.FileSystem
import org.koin.core.annotation.Single

@Single(binds = [SettingDataSource::class])
class SettingDataSource : BaseProtoDataSource<UserPreference>(userPreferenceDataStore) {
    override fun defaultValue(): UserPreference = UserPreference.defaultInstance
}

val userPreferenceDataStore = DataStoreFactory.create(
    storage = OkioStorage(
        fileSystem = FileSystem.SYSTEM,
        serializer = UserPreferenceSerializer,
        producePath = {
            dataStorePath("user_preference.json")
        },
    ),
)