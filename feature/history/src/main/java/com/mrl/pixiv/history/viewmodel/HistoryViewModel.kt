package com.mrl.pixiv.history.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State
import com.mrl.pixiv.repository.paging.HistoryIllustPagingSource
import org.koin.android.annotation.KoinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

data class HistoryState(
    val currentSearch: String,
) : State {
    companion object {
        val INITIAL = HistoryState(
            currentSearch = ""
        )
    }
}

sealed class HistoryAction : Action {
    data class UpdateSearch(val search: String) : HistoryAction()
}

@KoinViewModel
class HistoryViewModel(
    reducer: HistoryReducer,
    middleware: HistoryMiddleware,
) : BaseViewModel<HistoryState, HistoryAction>(
    initialState = HistoryState.INITIAL,
    reducer = reducer,
    middlewares = listOf(middleware),
), KoinComponent {
    val illusts = Pager(PagingConfig(pageSize = 20)) {
        get<HistoryIllustPagingSource>()
    }.flow.cachedIn(viewModelScope)
}