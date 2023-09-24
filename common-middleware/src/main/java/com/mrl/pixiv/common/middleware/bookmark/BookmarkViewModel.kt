package com.mrl.pixiv.common.middleware.bookmark

import com.mrl.pixiv.common.base.BaseViewModel

class BookmarkViewModel(
    reducer: BookmarkReducer,
    middleware: BookmarkMiddleware,
) : BaseViewModel<BookmarkState, BookmarkAction>(
    reducer = reducer,
    middlewares = listOf(middleware),
    initialState = BookmarkState.INITIAL,
)