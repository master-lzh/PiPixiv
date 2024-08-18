package com.mrl.pixiv.common.viewmodel.bookmark

import com.mrl.pixiv.common.viewmodel.Reducer

class BookmarkReducer : Reducer<BookmarkState, BookmarkAction> {
    override fun BookmarkState.reduce(action: BookmarkAction): BookmarkState {
        return when (action) {
            is BookmarkAction.UpdateBookmarkState -> {
                bookmarkStatus[action.illustId] = action.isBookmarked
                this
            }

            else -> this
        }
    }
}