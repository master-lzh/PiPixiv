package com.mrl.pixiv.datasource.local.datastore

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import com.mrl.pixiv.data.user.UserInfo
import com.mrl.pixiv.data.user.UserInfoSerializer
import com.mrl.pixiv.datasource.local.datastore.base.BaseProtoDataSource
import com.mrl.pixiv.datasource.local.datastore.base.dataStorePath
import okio.FileSystem
import org.koin.core.annotation.Single

@Single(binds = [UserInfoDataSource::class])
class UserInfoDataSource : BaseProtoDataSource<UserInfo>(userInfoDataStore) {
    override fun defaultValue(): UserInfo = UserInfo.defaultInstance
}

val userInfoDataStore = DataStoreFactory.create(
    storage = OkioStorage(
        fileSystem = FileSystem.SYSTEM,
        serializer = UserInfoSerializer,
        producePath = {
            dataStorePath("user_info.pb")
        },
    ),
)