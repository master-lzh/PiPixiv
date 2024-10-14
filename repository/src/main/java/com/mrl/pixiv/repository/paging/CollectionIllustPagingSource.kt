package com.mrl.pixiv.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mrl.pixiv.common.data.Rlt
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.Restrict
import com.mrl.pixiv.data.user.UserBookmarksIllustQuery
import com.mrl.pixiv.repository.CollectionRepository
import com.mrl.pixiv.util.queryParams
import kotlinx.coroutines.flow.first

class CollectionIllustPagingSource(
    private val collectionRepository: CollectionRepository,
    private val userId: Long,
    private val query: UserBookmarksIllustQuery
) : PagingSource<UserBookmarksIllustQuery, Illust>() {
    override suspend fun load(params: LoadParams<UserBookmarksIllustQuery>): LoadResult<UserBookmarksIllustQuery, Illust> {
        val respFlow = if (params.key == null) {
            collectionRepository.getUserBookmarksIllusts(query)
        } else {
            collectionRepository.loadMoreUserBookmarksIllusts(params.key?.toMap() ?: emptyMap())
        }
        return when (val resp = respFlow.first()) {
            is Rlt.Success -> {
                LoadResult.Page(
                    data = resp.data.illusts.distinctBy { it.id },
                    prevKey = params.key,
                    nextKey = resp.data.nextURL?.queryParams?.let {
                        UserBookmarksIllustQuery(
                            restrict = it["restrict"] ?: Restrict.PUBLIC,
                            tag = it["tag"] ?: "",
                            userId = userId,
                            maxBookmarkId = it["max_bookmark_id"]?.toLongOrNull()
                        )
                    }
                )
            }

            is Rlt.Failed -> {
                LoadResult.Error(resp.error.exception)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<UserBookmarksIllustQuery, Illust>): UserBookmarksIllustQuery? {
        return null
    }
}