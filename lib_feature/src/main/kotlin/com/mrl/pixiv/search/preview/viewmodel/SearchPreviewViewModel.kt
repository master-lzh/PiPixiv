package com.mrl.pixiv.search.preview.viewmodel

import com.mrl.pixiv.common.data.search.TrendingTag
import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.koin.android.annotation.KoinViewModel


sealed class SearchPreviewAction : Action {
    data object LoadTrendingTags : SearchPreviewAction()
    data class AddSearchHistory(val keyword: String) : SearchPreviewAction()

    data class UpdateTrendingTags(val trendingTags: List<TrendingTag>) : SearchPreviewAction()
}

data class SearchPreviewState(
    val refreshing: Boolean,
    val trendingTags: ImmutableList<TrendingTag>,
) : State {
    companion object {
        val INITIAL = SearchPreviewState(
            refreshing = false,
            trendingTags = persistentListOf()
        )
    }
}

@KoinViewModel
class SearchPreviewViewModel(
    reducer: SearchPreviewReducer,
    middleware: SearchPreviewMiddleware,
) : BaseViewModel<SearchPreviewState, SearchPreviewAction>(
    initialState = SearchPreviewState.INITIAL,
    reducer = reducer,
    middlewares = listOf(middleware),
) {
    init {
        dispatch(SearchPreviewAction.LoadTrendingTags)
    }
}