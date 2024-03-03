package com.mrl.pixiv.datasource.local

import androidx.datastore.core.DataStore
import com.mrl.pixiv.data.user.UserInfo

class UserInfoDataSource(
    userInfoDataSource: DataStore<UserInfo>,
) : BaseProtoDataSource<UserInfo, UserInfo.Builder>(userInfoDataSource) {
    override fun defaultValue(): UserInfo {
        return UserInfo.getDefaultInstance()
    }
}