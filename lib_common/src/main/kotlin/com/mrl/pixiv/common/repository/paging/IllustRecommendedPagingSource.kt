package com.mrl.pixiv.common.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mrl.pixiv.common.data.Filter
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.data.Rlt
import com.mrl.pixiv.common.data.illust.IllustRecommendedQuery
import com.mrl.pixiv.common.repository.IllustRepository
import com.mrl.pixiv.common.util.queryParams
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
                    data = resp.data.illusts + resp.data.rankingIllusts,
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