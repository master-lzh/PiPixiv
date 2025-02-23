package com.mrl.pixiv.common.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.data.Rlt
import com.mrl.pixiv.common.data.search.SearchAiType
import com.mrl.pixiv.common.data.search.SearchIllustQuery
import com.mrl.pixiv.common.data.search.SearchSort
import com.mrl.pixiv.common.data.search.SearchTarget
import com.mrl.pixiv.common.repository.SearchRepository
import com.mrl.pixiv.common.util.queryParams
import kotlinx.coroutines.flow.first

class SearchIllustPagingSource(
    private val searchRepository: SearchRepository,
    private val query: SearchIllustQuery,
) : PagingSource<SearchIllustQuery, Illust>() {
    override fun getRefreshKey(state: PagingState<SearchIllustQuery, Illust>): SearchIllustQuery? {
        return null
    }

    override suspend fun load(params: LoadParams<SearchIllustQuery>): LoadResult<SearchIllustQuery, Illust> {
        val respFlow = if (params.key == null) {
            searchRepository.searchIllust(query)
        } else {
            searchRepository.searchIllustNext(params.key!!.toMap())
        }
        return when (val resp = respFlow.first()) {
            is Rlt.Success -> {
                val query = resp.data.nextUrl?.queryParams
                val nextKey = SearchIllustQuery(
                    word = query?.get("word") ?: "",
                    searchTarget = query?.get("search_target")
                        ?.let { SearchTarget.valueOf(it.uppercase()) }
                        ?: SearchTarget.PARTIAL_MATCH_FOR_TAGS,
                    sort = query?.get("sort")?.let { SearchSort.valueOf(it.uppercase()) }
                        ?: SearchSort.POPULAR_DESC,
                    searchAiType = query?.get("search_ai_type")
                        ?.let { type -> SearchAiType.entries.find { it.value == type.toInt() } }
                        ?: SearchAiType.HIDE_AI,
                    offset = query?.get("offset")?.toInt() ?: 0,
                )
                LoadResult.Page(
                    data = resp.data.illusts.distinctBy { it.id },
                    prevKey = params.key,
                    nextKey = nextKey
                )
            }

            is Rlt.Failed -> {
                LoadResult.Error(resp.error.exception)
            }
        }
    }
}