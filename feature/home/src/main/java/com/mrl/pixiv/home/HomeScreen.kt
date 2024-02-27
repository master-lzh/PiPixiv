package com.mrl.pixiv.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.middleware.bookmark.BookmarkAction
import com.mrl.pixiv.common.middleware.bookmark.BookmarkState
import com.mrl.pixiv.common.middleware.bookmark.BookmarkViewModel
import com.mrl.pixiv.common.ui.BaseScreen
import com.mrl.pixiv.common.ui.components.TextSnackbar
import com.mrl.pixiv.common_ui.util.navigateToPictureScreen
import com.mrl.pixiv.common_ui.util.navigateToSearchScreen
import com.mrl.pixiv.data.Filter
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.illust.IllustRecommendedQuery
import com.mrl.pixiv.home.components.HomeContent
import com.mrl.pixiv.home.components.HomeTopBar
import com.mrl.pixiv.home.viewmodel.HomeAction
import com.mrl.pixiv.home.viewmodel.HomeState
import com.mrl.pixiv.home.viewmodel.HomeViewModel
import com.mrl.pixiv.util.queryParams
import com.mrl.pixiv.util.second
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

val initRecommendedQuery = IllustRecommendedQuery(
    filter = Filter.ANDROID.value,
    includeRankingIllusts = true,
    includePrivacyPolicy = true
)
const val TAG = "HomeScreen"
fun HomeViewModel.onRefresh() {
    dispatch(
        HomeAction.RefreshIllustRecommendedIntent(initRecommendedQuery)
    )
}

fun HomeViewModel.onScrollToBottom() {
    dispatch(
        HomeAction.LoadMoreIllustRecommendedIntent(
            queryMap = state.nextUrl.queryParams
        )
    )
}


internal enum class HomeSnackbar(val actionLabel: String) {
    REVOKE_UNBOOKMARK("撤销取消收藏"),
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    homeViewModel: HomeViewModel = koinViewModel(),
    bookmarkViewModel: BookmarkViewModel,
    offsetAnimation: IntOffset,
) {
//    OnLifecycle(onLifecycle = homeViewModel::onCreate, lifecycleEvent = Lifecycle.Event.ON_CREATE)
    HomeScreen(
        modifier = modifier,
        state = homeViewModel.state,
        bookmarkState = bookmarkViewModel.state,
        bookmarkDispatch = bookmarkViewModel::dispatch,
        navToPictureScreen = navHostController::navigateToPictureScreen,
        navToSearchScreen = navHostController::navigateToSearchScreen,
        homeViewModel = homeViewModel,
        dispatch = homeViewModel::dispatch,
        offsetAnimation = offsetAnimation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeState,
    bookmarkState: BookmarkState,
    bookmarkDispatch: (BookmarkAction) -> Unit,
    navToPictureScreen: (Illust) -> Unit,
    navToSearchScreen: () -> Unit,
    homeViewModel: HomeViewModel = koinViewModel(),
    dispatch: (HomeAction) -> Unit = {},
    offsetAnimation: IntOffset,
) {
    val lazyStaggeredGridState = rememberLazyStaggeredGridState()
    val scope = rememberCoroutineScope()
    val pullRefreshState =
        rememberPullRefreshState(refreshing = state.isRefresh, onRefresh = homeViewModel::onRefresh)
    val snackBarHostState = remember { SnackbarHostState() }
    val onUnBookmark = { id: Long ->
        scope.launch {
            val result = snackBarHostState.showSnackbar(
                message = "撤销",
                actionLabel = HomeSnackbar.REVOKE_UNBOOKMARK.actionLabel,
                duration = SnackbarDuration.Long,
            )
            when (result) {
                SnackbarResult.Dismissed -> {}

                SnackbarResult.ActionPerformed -> {
                    bookmarkDispatch(BookmarkAction.IllustBookmarkAddIntent(id))
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        scope.launch {
            homeViewModel.exception.collect {
                snackBarHostState.showSnackbar(it?.message ?: "未知错误")
            }
        }
    }

    BaseScreen(
        title = stringResource(R.string.app_name),
        actions = {
            HomeTopBar(
                navigateToSearchScreen = navToSearchScreen,
                onRefreshToken = { dispatch(HomeAction.RefreshTokenIntent) }
            ) {
                dispatch(
                    HomeAction.RefreshIllustRecommendedIntent(
                        initRecommendedQuery
                    )
                )
            }
        },
        snackBarHost = {
            SnackbarHost(snackBarHostState) {
                when (it.visuals.actionLabel) {
                    HomeSnackbar.REVOKE_UNBOOKMARK.actionLabel -> {
                        TextSnackbar(
                            text = it.visuals.message,
                            action = {
                                Row {
                                    IconButton(
                                        onClick = { snackBarHostState.currentSnackbarData?.performAction() },
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Rounded.Undo,
                                            contentDescription = null
                                        )
                                    }
                                    it.visuals.actionLabel?.let {
                                        Text(text = it)
                                    }
                                }
                            }
                        )
                    }

                    else -> {
                        TextSnackbar(text = it.visuals.message)
                    }
                }
            }
        },
        floatingActionButton = {
            if (state.recommendImageList.isNotEmpty()) {
                FloatingActionButton(
                    modifier = Modifier
                        .padding(bottom = 56.dp)
                        .offset { offsetAnimation },
                    onClick = {
                        scope.launch {
                            lazyStaggeredGridState.scrollToItem(0)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.background,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowUpward,
                        contentDescription = null
                    )
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .fillMaxSize()
                .padding(it)
        ) {
            HomeContent(
                navToPictureScreen = navToPictureScreen,
                state = state,
                bookmarkState = bookmarkState,
                lazyStaggeredGridState = lazyStaggeredGridState,
                onBookmarkClick = { id, bookmark ->
                    if (bookmark) {
                        bookmarkDispatch(BookmarkAction.IllustBookmarkDeleteIntent(id))
                    } else {
                        bookmarkDispatch(BookmarkAction.IllustBookmarkAddIntent(id))
                    }
                    if (!bookmark) {
                        onUnBookmark(id)
                    }
                },
                dismissRefresh = {
                    scope.launch {
                        lazyStaggeredGridState.scrollToItem(0)
                        delay(1.second)
                        dispatch(HomeAction.DismissLoading)
                    }
                },
                onScrollToBottom = homeViewModel::onScrollToBottom,
            )
            PullRefreshIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .apply {
                        if (state.recommendImageList.isEmpty()) {
                            fillMaxSize()
                        }
                    },
                refreshing = state.isRefresh,
                state = pullRefreshState,
            )
        }
    }
}