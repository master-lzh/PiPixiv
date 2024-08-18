package com.mrl.pixiv.common.viewmodel.bookmark

import com.mrl.pixiv.common.viewmodel.BaseViewModel
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class BookmarkViewModel(
    reducer: BookmarkReducer,
    middleware: BookmarkMiddleware,
) : BaseViewModel<BookmarkState, BookmarkAction>(
    reducer = reducer,
    middlewares = listOf(middleware),
    initialState = BookmarkState.INITIAL,
) {
}