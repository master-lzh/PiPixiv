package com.mrl.pixiv.common.middleware.bookmark

import com.mrl.pixiv.common.data.Reducer

class BookmarkReducer:Reducer<BookmarkState, BookmarkAction> {
    override fun reduce(state: BookmarkState, action: BookmarkAction): BookmarkState {
        return when (action) {
            is BookmarkAction.UpdateState -> action.state
            else -> state
        }
    }
}