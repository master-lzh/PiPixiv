package com.mrl.pixiv.common.middleware.bookmark

import com.mrl.pixiv.common.viewmodel.Reducer

class BookmarkReducer : Reducer<BookmarkState, BookmarkAction> {
    override fun reduce(state: BookmarkState, action: BookmarkAction): BookmarkState {
        return when (action) {
            is BookmarkAction.UpdateBookmarkState -> state.apply {
                bookmarkStatus[action.illustId] = action.isBookmarked
            }

            else -> state
        }
    }
}