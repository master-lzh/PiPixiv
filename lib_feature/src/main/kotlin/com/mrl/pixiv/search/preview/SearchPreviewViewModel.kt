package com.mrl.pixiv.search.preview

import com.mrl.pixiv.common.data.Filter
import com.mrl.pixiv.common.data.search.TrendingTag
import com.mrl.pixiv.common.repository.PixivRepository
import com.mrl.pixiv.common.viewmodel.BaseMviViewModel
import com.mrl.pixiv.common.viewmodel.ViewIntent
import com.mrl.pixiv.search.SearchManager
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.koin.android.annotation.KoinViewModel


sealed class SearchPreviewAction : ViewIntent {
    data object LoadTrendingTags : SearchPreviewAction()
    data class AddSearchHistory(val keyword: String) : SearchPreviewAction()
}

data class SearchPreviewState(
    val refreshing: Boolean = false,
    val trendingTags: ImmutableList<TrendingTag> = persistentListOf(),
)

@KoinViewModel
class SearchPreviewViewModel : BaseMviViewModel<SearchPreviewState, SearchPreviewAction>(
    initialState = SearchPreviewState()
) {
    override suspend fun handleIntent(intent: SearchPreviewAction) {
        when (intent) {
            is SearchPreviewAction.LoadTrendingTags -> loadTrendingTags()
            is SearchPreviewAction.AddSearchHistory -> addSearchHistory(intent.keyword)
        }
    }

    init {
        dispatch(SearchPreviewAction.LoadTrendingTags)
    }

    private fun addSearchHistory(keyword: String) {
        SearchManager.addSearchHistory(keyword)
    }

    private fun loadTrendingTags() {
        launchIO {
            updateState { copy(refreshing = true) }
            val resp = PixivRepository.trendingTags(Filter.ANDROID)
            updateState {
                copy(trendingTags = resp.trendTags.toImmutableList(), refreshing = false)
            }
        }
    }
}