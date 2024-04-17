package com.mrl.pixiv.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.lifecycle.OnLifecycle
import com.mrl.pixiv.common.middleware.bookmark.BookmarkAction
import com.mrl.pixiv.common.middleware.bookmark.BookmarkState
import com.mrl.pixiv.common.middleware.bookmark.BookmarkViewModel
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.Screen
import com.mrl.pixiv.common.ui.components.TextSnackbar
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common_ui.util.navigateToPictureScreen
import com.mrl.pixiv.data.Filter
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.illust.IllustRecommendedQuery
import com.mrl.pixiv.home.components.HomeContent
import com.mrl.pixiv.home.components.HomeTopBar
import com.mrl.pixiv.home.viewmodel.HomeAction
import com.mrl.pixiv.home.viewmodel.HomeState
import com.mrl.pixiv.home.viewmodel.HomeViewModel
import com.mrl.pixiv.util.AppUtil
import com.mrl.pixiv.util.queryParams
import com.mrl.pixiv.util.second
import com.mrl.pixiv.util.throttleClick
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
    REVOKE_UNBOOKMARK(AppUtil.getString(R.string.revoke_cancel_like)),
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = LocalNavigator.currentOrThrow,
    homeViewModel: HomeViewModel = koinViewModel(),
    bookmarkViewModel: BookmarkViewModel,
) {
    OnLifecycle(lifecycleEvent = Lifecycle.Event.ON_CREATE, onLifecycle = homeViewModel::onCreate)
    HomeScreen(
        modifier = modifier,
        state = homeViewModel.state,
        bookmarkState = bookmarkViewModel.state,
        bookmarkDispatch = bookmarkViewModel::dispatch,
        navToPictureScreen = navHostController::navigateToPictureScreen,
        onRefresh = homeViewModel::onRefresh,
        onScrollToBottom = homeViewModel::onScrollToBottom,
        dispatch = homeViewModel::dispatch,
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
    onRefresh: () -> Unit,
    onScrollToBottom: () -> Unit,
    dispatch: (HomeAction) -> Unit = {},
) {
    val context = LocalContext.current
    val lazyStaggeredGridState = rememberLazyStaggeredGridState()
    val scope = rememberCoroutineScope()
    val pullRefreshState =
        rememberPullRefreshState(refreshing = state.isRefresh, onRefresh = onRefresh)
    val snackBarHostState = remember { SnackbarHostState() }
    val onUnBookmark = { id: Long ->
        scope.launch {
            val result = snackBarHostState.showSnackbar(
                message = context.getString(R.string.revoke),
                actionLabel = HomeSnackbar.REVOKE_UNBOOKMARK.actionLabel,
                duration = SnackbarDuration.Short,
            )
            when (result) {
                SnackbarResult.Dismissed -> {}

                SnackbarResult.ActionPerformed -> {
                    bookmarkDispatch(BookmarkAction.IllustBookmarkAddIntent(id))
                }
            }
        }
    }
    LaunchedEffect(state.exception) {
        if (state.exception != null) {
            scope.launch {
                snackBarHostState.showSnackbar(
                    state.exception.message ?: context.getString(R.string.unknown_error)
                )
            }
        }
    }

    Screen(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    HomeTopBar(
                        onRefreshToken = { dispatch(HomeAction.RefreshTokenIntent) },
                        onRefresh = {
                            dispatch(
                                HomeAction.RefreshIllustRecommendedIntent(
                                    initRecommendedQuery
                                )
                            )
                        }
                    )
                }
            )
        },
        snackBarHost = {
            SnackbarHost(snackBarHostState) {
                when (it.visuals.actionLabel) {
                    HomeSnackbar.REVOKE_UNBOOKMARK.actionLabel -> {
                        TextSnackbar(
                            text = it.visuals.message
                        ) {
                            Row(
                                modifier = Modifier.throttleClick {
                                    snackBarHostState.currentSnackbarData?.performAction()
                                },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.Undo,
                                    contentDescription = null
                                )
                                it.visuals.actionLabel?.let {
                                    Text(text = it)
                                }
                            }
                        }
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
                    modifier = Modifier,
//                        .offset { offsetAnimation },
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
                        onUnBookmark(id)
                    } else {
                        bookmarkDispatch(BookmarkAction.IllustBookmarkAddIntent(id))
                    }
                },
                dismissRefresh = {
                    scope.launch {
                        lazyStaggeredGridState.scrollToItem(0)
                        delay(1.second)
                        dispatch(HomeAction.DismissLoading)
                    }
                },
                onScrollToBottom = onScrollToBottom,
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