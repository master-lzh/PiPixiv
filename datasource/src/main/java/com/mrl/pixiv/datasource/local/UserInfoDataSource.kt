package com.mrl.pixiv.datasource.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.mrl.pixiv.data.user.UserInfo
import com.mrl.pixiv.data.user.UserInfoSerializer
import com.mrl.pixiv.datasource.local.base.BaseProtoDataSource

class UserInfoDataSource(
    userInfoDataSource: DataStore<UserInfo>,
) : BaseProtoDataSource<UserInfo, UserInfo.Builder>(userInfoDataSource) {
    override fun defaultValue(): UserInfo {
        return UserInfo.getDefaultInstance()
    }
}

val Context.userInfoDataStore by dataStore(
    fileName = "user_info.pb",
    serializer = UserInfoSerializer
)