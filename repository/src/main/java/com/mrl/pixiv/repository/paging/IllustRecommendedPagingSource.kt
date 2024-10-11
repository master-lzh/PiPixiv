package com.mrl.pixiv.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mrl.pixiv.common.data.Rlt
import com.mrl.pixiv.data.Filter
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.illust.IllustRecommendedQuery
import com.mrl.pixiv.repository.IllustRepository
import com.mrl.pixiv.util.queryParams
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory

@Factory
class IllustRecommendedPagingSource(
    private val illustRepository: IllustRepository,
) : PagingSource<String, Illust>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, Illust> {
        val respFlow = if (params.key.isNullOrEmpty()) {
            illustRepository.getIllustRecommended(
                IllustRecommendedQuery(
                    filter = Filter.ANDROID.value,
                    includeRankingIllusts = true,
                    includePrivacyPolicy = true
                )
            )
        } else {
            illustRepository.loadMoreIllustRecommended(params.key?.queryParams ?: emptyMap())
        }
        return when (val resp = respFlow.first()) {
            is Rlt.Success -> {
                LoadResult.Page(
                    data = resp.data.illusts,
                    prevKey = params.key,
                    nextKey = resp.data.nextURL
                )
            }

            is Rlt.Failed -> {
                LoadResult.Error(resp.error.exception)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<String, Illust>): String? {
        return null
    }
}