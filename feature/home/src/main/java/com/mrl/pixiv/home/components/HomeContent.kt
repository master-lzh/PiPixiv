package com.mrl.pixiv.home.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.mrl.pixiv.common.middleware.bookmark.BookmarkState
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.home.R
import com.mrl.pixiv.home.TAG
import com.mrl.pixiv.home.viewmodel.HomeState
import com.mrl.pixiv.util.ToastUtil

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
    SideEffect {
        Log.d(TAG, "setComposePage: ${state.refreshTokenResult}")
    }

    if (state.refreshTokenResult) {
        ToastUtil.safeShortToast(R.string.refresh_token_success)
    }

    RecommendGrid(
        navToPictureScreen = navToPictureScreen,
        bookmarkState = bookmarkState,
        lazyStaggeredGridState = lazyStaggeredGridState,
        recommendImageList = state.recommendImageList,
        loadMore = state.loadMore,
        onBookmarkClick = onBookmarkClick,
        onScrollToBottom = onScrollToBottom,
    )


    if (state.isRefresh) {
        Log.d(TAG, "HomeContent: dismissRefresh")
        dismissRefresh()
    }
}