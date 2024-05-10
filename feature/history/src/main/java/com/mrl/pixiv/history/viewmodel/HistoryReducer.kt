package com.mrl.pixiv.history.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer
import kotlinx.collections.immutable.toImmutableList

class HistoryReducer : Reducer<HistoryState, HistoryAction> {
    override fun reduce(state: HistoryState, action: HistoryAction): HistoryState {
        return when (action) {
            is HistoryAction.UpdateHistory -> state.copy(
                illusts = (state.illusts + action.illusts).toImmutableList(),
                illustNextUrl = action.nextUrl,
                loading = false
            )

            is HistoryAction.UpdateSearch -> state.copy(currentSearch = action.search)

            else -> state
        }
    }
}
