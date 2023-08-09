package com.mrl.pixiv.home.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mrl.pixiv.home.HomeViewModel
import com.mrl.pixiv.home.R
import com.mrl.pixiv.home.TAG
import com.mrl.pixiv.util.ToastUtil

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    scaffoldState: ScaffoldState,
    viewModel: HomeViewModel,
    lazyStaggeredGridState: LazyStaggeredGridState,
    onBookmarkClick: (Long, Boolean) -> Unit,
    dismissRefresh: () -> Unit,
    onScrollToBottom: () -> Unit,
) {
    val homeState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    SideEffect {
        Log.d(TAG, "setComposePage: ${homeState.refreshTokenResult}")
    }

    if (homeState.refreshTokenResult) {
        ToastUtil.shortToast(R.string.refresh_token_success)
    }

    RecommendGrid(
        scaffoldState,
        lazyStaggeredGridState,
        homeState.recommendImageList,
        homeState.loadMore,
        onBookmarkClick,
        onScrollToBottom,
    )


    if (homeState.isRefresh) {
        Log.d(TAG, "HomeContent: dismissRefresh")
        dismissRefresh()
    }
}