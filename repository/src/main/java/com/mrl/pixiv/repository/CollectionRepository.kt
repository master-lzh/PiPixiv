package com.mrl.pixiv.repository

import com.mrl.pixiv.data.user.UserBookmarkTagsQuery
import com.mrl.pixiv.data.user.UserBookmarksIllustQuery
import com.mrl.pixiv.data.user.UserBookmarksNovelQuery
import com.mrl.pixiv.datasource.local.datastore.UserInfoDataSource
import com.mrl.pixiv.datasource.remote.UserHttpService
import kotlinx.coroutines.flow.first

class CollectionRepository(
    private val userHttpService: UserHttpService,
    private val userInfoDataSource: UserInfoDataSource
) {
    suspend fun getUserInfo() = userInfoDataSource.data.first()
    suspend fun getUserBookmarksIllusts(userBookmarksIllustQuery: UserBookmarksIllustQuery) =
        userHttpService.getUserBookmarksIllust(userBookmarksIllustQuery)

    suspend fun loadMoreUserBookmarksIllusts(map: Map<String, String>) =
        userHttpService.loadMoreUserBookmarksIllust(map)

    suspend fun getUserBookmarksNovels(userBookmarksNovelQuery: UserBookmarksNovelQuery) =
        userHttpService.getUserBookmarksNovels(userBookmarksNovelQuery)

    suspend fun getUserBookmarkTagsIllust(userBookmarkTagsQuery: UserBookmarkTagsQuery) =
        userHttpService.getUserBookmarkTagsIllust(userBookmarkTagsQuery)

    suspend fun getUserBookmarkTagsNovel(userBookmarkTagsQuery: UserBookmarkTagsQuery) =
        userHttpService.getUserBookmarkTagsNovel(userBookmarkTagsQuery)
}