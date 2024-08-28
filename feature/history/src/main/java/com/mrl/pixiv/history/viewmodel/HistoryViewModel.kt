package com.mrl.pixiv.history.viewmodel

import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State
import com.mrl.pixiv.data.Illust
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.koin.android.annotation.KoinViewModel

data class HistoryState(
    val loading: Boolean,
    val currentSearch: String,
    val illusts: ImmutableList<Illust>,
    val illustNextUrl: String?,
) : State {
    companion object {
        val INITIAL = HistoryState(
            loading = true,
            currentSearch = "",
            illusts = persistentListOf(),
            illustNextUrl = null
        )
    }
}

sealed class HistoryAction : Action {
    data class LoadHistory(val queryParams: Map<String, String>?) : HistoryAction()

    data class UpdateHistory(val illusts: List<Illust>, val nextUrl: String?) : HistoryAction()
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
) {
    init {
        dispatch(HistoryAction.LoadHistory(null))
    }
}