package com.mrl.pixiv.search.viewmodel

import com.mrl.pixiv.common.viewmodel.BaseViewModel


class SearchViewModel(
    reducer: SearchReducer,
    middleware: SearchMiddleware
) : BaseViewModel<SearchState, SearchAction>(
    reducer = reducer,
    middlewares = listOf(middleware),
    initialState = SearchState.INITIAL,
) {
}

