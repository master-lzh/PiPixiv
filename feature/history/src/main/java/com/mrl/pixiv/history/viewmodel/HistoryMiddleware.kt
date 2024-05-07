package com.mrl.pixiv.history.viewmodel

import com.mrl.pixiv.common.viewmodel.Middleware
import com.mrl.pixiv.repository.HistoryRepository

class HistoryMiddleware(
    private val historyRepository: HistoryRepository
) : Middleware<HistoryState, HistoryAction>() {
    override suspend fun process(state: HistoryState, action: HistoryAction) {
        when (action) {
            is HistoryAction.LoadHistory -> loadHistory(action.queryParams)
            else -> Unit
        }
    }

    private fun loadHistory(queryParams: Map<String, String>?) {
        launchNetwork {
            requestHttpDataWithFlow(
                request =
                if (queryParams == null)
                    historyRepository.getUserBrowsingHistoryIllusts()
                else
                    historyRepository.loadMoreUserBrowsingHistoryIllusts(queryParams)
            ) {
                dispatch(HistoryAction.UpdateHistory(it.illusts.distinctBy { it.id }, it.nextURL))
            }
        }
    }
}
