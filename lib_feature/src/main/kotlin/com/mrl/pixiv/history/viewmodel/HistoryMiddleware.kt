package com.mrl.pixiv.history.viewmodel

import com.mrl.pixiv.common.viewmodel.Middleware
import org.koin.core.annotation.Factory

@Factory
class HistoryMiddleware : Middleware<HistoryState, HistoryAction>() {
    override suspend fun process(state: HistoryState, action: HistoryAction) {

    }
}
