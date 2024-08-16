package com.mrl.pixiv.history.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer
import kotlinx.collections.immutable.toImmutableList

class HistoryReducer : Reducer<HistoryState, HistoryAction> {
    override fun HistoryState.reduce(action: HistoryAction): HistoryState {
        return when (action) {
            is HistoryAction.UpdateHistory -> copy(
                illusts = (illusts + action.illusts).toImmutableList(),
                illustNextUrl = action.nextUrl,
                loading = false
            )

            is HistoryAction.UpdateSearch -> copy(currentSearch = action.search)
            else -> this
        }
    }
}
