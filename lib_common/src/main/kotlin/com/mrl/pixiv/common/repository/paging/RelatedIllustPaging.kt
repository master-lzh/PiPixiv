package com.mrl.pixiv.common.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mrl.pixiv.common.data.Filter
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.repository.PixivRepository
import com.mrl.pixiv.common.util.queryParams

class RelatedIllustPaging(
    private val illustId: Long
) : PagingSource<String, Illust>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, Illust> {
        return try {
            val resp = if (params.key.isNullOrEmpty()) {
                PixivRepository.getIllustRelated(
                    illustId = illustId,
                    filter = Filter.ANDROID.value
                )
            } else {
                PixivRepository.loadMoreIllustRelated(
                    params.key?.queryParams ?: emptyMap()
                )
            }
            val query = resp.nextURL
            LoadResult.Page(
                data = resp.illusts.distinctBy { it.id },
                prevKey = params.key,
                nextKey = query
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, Illust>): String? {
        return null
    }
}