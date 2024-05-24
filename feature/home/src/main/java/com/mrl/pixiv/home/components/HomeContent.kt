package com.mrl.pixiv.home.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.mrl.pixiv.common.middleware.bookmark.BookmarkAction
import com.mrl.pixiv.common.middleware.bookmark.BookmarkState
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.home.TAG
import com.mrl.pixiv.home.viewmodel.HomeState
import kotlinx.collections.immutable.toImmutableList

typealias OnBookmarkClick = (Long, Boolean, restrict: String, tags: List<String>?) -> Unit

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HomeContent(
    navToPictureScreen: (Illust, String) -> Unit,
    state: HomeState,
    bookmarkState: BookmarkState,
    lazyStaggeredGridState: LazyStaggeredGridState,
    onBookmarkClick: OnBookmarkClick,
    dismissRefresh: () -> Unit,
    onScrollToBottom: () -> Unit,
    dispatch: (BookmarkAction) -> Unit
) {
    RecommendGrid(
        navToPictureScreen = navToPictureScreen,
        bookmarkState = bookmarkState,
        lazyStaggeredGridState = lazyStaggeredGridState,
        recommendImageList = state.recommendImageList.toImmutableList(),
        onBookmarkClick = onBookmarkClick,
        onScrollToBottom = onScrollToBottom,
        dispatch = dispatch,
    )

    LaunchedEffect(state.isRefresh) {
        if (state.isRefresh) {
            Log.d(TAG, "HomeContent: dismissRefresh")
            dismissRefresh()
        }
    }
}