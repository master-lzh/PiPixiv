package com.mrl.pixiv.home.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.navigation.NavHostController
import com.mrl.pixiv.home.R
import com.mrl.pixiv.home.TAG
import com.mrl.pixiv.home.viewmodel.HomeState
import com.mrl.pixiv.util.ToastUtil

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HomeContent(
    navHostController: NavHostController,
    scaffoldState: ScaffoldState,
    state: HomeState,
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
        navHostController,
        scaffoldState,
        lazyStaggeredGridState,
        state.recommendImageList,
        state.loadMore,
        onBookmarkClick,
        onScrollToBottom,
    )


    if (state.isRefresh) {
        Log.d(TAG, "HomeContent: dismissRefresh")
        dismissRefresh()
    }
}