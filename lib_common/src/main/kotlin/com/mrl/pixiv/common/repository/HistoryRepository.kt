package com.mrl.pixiv.common.repository

import com.mrl.pixiv.common.datasource.remote.UserHttpService
import org.koin.core.annotation.Single

@Single
class HistoryRepository(
    private val userHttpService: UserHttpService
) {
    suspend fun getUserBrowsingHistoryIllusts() = userHttpService.getUserBrowsingHistoryIllusts()

    suspend fun loadMoreUserBrowsingHistoryIllusts(map: Map<String, String>) =
        userHttpService.loadMoreUserBrowsingHistoryIllusts(map)
}