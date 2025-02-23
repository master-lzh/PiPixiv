package com.mrl.pixiv.history.viewmodel

import com.mrl.pixiv.common.viewmodel.Reducer
import org.koin.core.annotation.Single

@Single
class HistoryReducer : Reducer<HistoryState, HistoryAction> {
    override fun HistoryState.reduce(action: HistoryAction): HistoryState {
        return when (action) {
            is HistoryAction.UpdateSearch -> copy(currentSearch = action.search)
        }
    }
}
