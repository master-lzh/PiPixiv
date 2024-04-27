package com.mrl.pixiv.repository

import com.mrl.pixiv.datasource.remote.UserHttpService

class HistoryRepository(
    private val userHttpService: UserHttpService
) {
    suspend fun getUserBrowsingHistoryIllusts() = userHttpService.getUserBrowsingHistoryIllusts()

    suspend fun loadMoreUserBrowsingHistoryIllusts(map: Map<String, String>) =
        userHttpService.loadMoreUserBrowsingHistoryIllusts(map)
}