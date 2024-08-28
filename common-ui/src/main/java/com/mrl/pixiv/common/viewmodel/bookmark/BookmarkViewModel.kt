package com.mrl.pixiv.common.viewmodel.bookmark

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.common.viewmodel.BaseViewModel
import com.mrl.pixiv.common.viewmodel.State
import com.mrl.pixiv.data.Restrict
import org.koin.android.annotation.KoinViewModel

@Stable
data class BookmarkState(
    val bookmarkStatus: SnapshotStateMap<Long, Boolean>,
) : State {
    companion object {
        val INITIAL = BookmarkState(
            bookmarkStatus = mutableStateMapOf()
        )
    }
}

sealed class BookmarkAction : Action {
    data class IllustBookmarkAddIntent(
        val illustId: Long,
        val restrict: String = Restrict.PUBLIC,
        val tags: List<String>? = null,
    ) : BookmarkAction()

    data class IllustBookmarkDeleteIntent(val illustId: Long) : BookmarkAction()

    data class UpdateBookmarkState(val illustId: Long, val isBookmarked: Boolean) : BookmarkAction()
}

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