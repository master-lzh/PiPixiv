package com.mrl.pixiv.common.middleware.bookmark

import com.mrl.pixiv.common.base.BaseViewModel
import com.mrl.pixiv.data.Illust

class BookmarkViewModel(
    illust: Illust,
    reducer: BookmarkReducer,
    middleware: BookmarkMiddleware,
) : BaseViewModel<BookmarkState, BookmarkAction>(
    reducer = reducer,
    middlewares = listOf(middleware),
    initialState = BookmarkState.INITIAL,
) {
    init {
        dispatch(BookmarkAction.UpdateState(state = BookmarkState(isBookmark = illust.isBookmarked)))
    }
}