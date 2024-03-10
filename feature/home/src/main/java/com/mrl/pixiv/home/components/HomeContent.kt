package com.mrl.pixiv.home.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.mrl.pixiv.common.middleware.bookmark.BookmarkState
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.home.TAG
import com.mrl.pixiv.home.viewmodel.HomeState
import kotlinx.collections.immutable.toImmutableList

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HomeContent(
    navToPictureScreen: (Illust) -> Unit,
    state: HomeState,
    bookmarkState: BookmarkState,
    lazyStaggeredGridState: LazyStaggeredGridState,
    onBookmarkClick: (Long, Boolean) -> Unit,
    dismissRefresh: () -> Unit,
    onScrollToBottom: () -> Unit,
) {
    RecommendGrid(
        navToPictureScreen = navToPictureScreen,
        bookmarkState = bookmarkState,
        lazyStaggeredGridState = lazyStaggeredGridState,
        recommendImageList = state.recommendImageList.toImmutableList(),
        onBookmarkClick = onBookmarkClick,
        onScrollToBottom = onScrollToBottom,
    )

    LaunchedEffect(state.isRefresh) {
        if (state.isRefresh) {
            Log.d(TAG, "HomeContent: dismissRefresh")
            dismissRefresh()
        }
    }
}