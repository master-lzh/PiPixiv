package com.mrl.pixiv.home.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.navigation.NavHostController
import com.mrl.pixiv.home.R
import com.mrl.pixiv.home.TAG
import com.mrl.pixiv.home.viewmodel.HomeViewModel
import com.mrl.pixiv.util.ToastUtil

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    navHostController: NavHostController,
    scaffoldState: ScaffoldState,
    viewModel: HomeViewModel,
    lazyStaggeredGridState: LazyStaggeredGridState,
    onBookmarkClick: (Long, Boolean) -> Unit,
    dismissRefresh: () -> Unit,
    onScrollToBottom: () -> Unit,
) {
    val homeState = viewModel.state

    SideEffect {
        Log.d(TAG, "setComposePage: ${homeState.refreshTokenResult}")
    }

    if (homeState.refreshTokenResult) {
        ToastUtil.safeShortToast(R.string.refresh_token_success)
    }

    RecommendGrid(
        navHostController,
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