package com.mrl.pixiv.common.datasource

import com.mrl.pixiv.common.coroutine.launchIO
import com.mrl.pixiv.common.datasource.local.datastore.UserAuthDataSource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object TokenManager : KoinComponent {
    private val authDataSource by inject<UserAuthDataSource>()

    init {
        launchIO {
            authDataSource.userAccessToken.get().collect {
                token = it
            }
        }
    }

    var token: String? = null
        private set

    fun setToken(token: String) {
        this.token = token
        authDataSource.userAccessToken.set(token)
    }
}