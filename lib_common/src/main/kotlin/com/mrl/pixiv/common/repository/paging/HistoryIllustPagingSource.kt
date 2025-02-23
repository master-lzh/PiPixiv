package com.mrl.pixiv.common.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.data.Rlt
import com.mrl.pixiv.common.repository.HistoryRepository
import com.mrl.pixiv.common.util.queryParams
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory

@Factory
class HistoryIllustPagingSource(
    private val historyRepository: HistoryRepository,
) : PagingSource<String, Illust>() {
    override fun getRefreshKey(state: PagingState<String, Illust>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Illust> {
        val respFlow = if (params.key.isNullOrEmpty()) {
            historyRepository.getUserBrowsingHistoryIllusts()
        } else {
            historyRepository.loadMoreUserBrowsingHistoryIllusts(params.key?.queryParams ?: emptyMap())
        }
        return when (val resp = respFlow.first()) {
            is Rlt.Success -> {
                LoadResult.Page(
                    data = resp.data.illusts.distinctBy { it.id },
                    prevKey = params.key,
                    nextKey = resp.data.nextURL
                )
            }

            is Rlt.Failed -> {
                LoadResult.Error(resp.error.exception)
            }
        }
    }

}