package com.mrl.pixiv.search.viewmodel.result

import com.mrl.pixiv.common.viewmodel.Middleware
import org.koin.core.annotation.Factory

@Factory
class SearchResultMiddleware : Middleware<SearchResultState, SearchResultAction>() {
    override suspend fun process(state: SearchResultState, action: SearchResultAction) {
    }
}