package com.mrl.pixiv.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.ui.BaseScreen
import com.mrl.pixiv.common.ui.components.TextSnackbar
import com.mrl.pixiv.common_ui.util.navigateToPictureScreen
import com.mrl.pixiv.common_ui.util.navigateToSearchScreen
import com.mrl.pixiv.data.Filter
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.illust.IllustBookmarkAddReq
import com.mrl.pixiv.data.illust.IllustBookmarkDeleteReq
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

fun HomeViewModel.onBookmarkClick(id: Long, bookmark: Boolean) {
    if (bookmark) {
        dispatch(HomeAction.IllustBookmarkAddIntent(IllustBookmarkAddReq(id)))
    } else {
        dispatch(HomeAction.IllustBookmarkDeleteIntent(IllustBookmarkDeleteReq(id)))
    }
}

internal enum class HomeSnackbar(val actionLabel: String) {
    REVOKE_UNBOOKMARK("撤销取消收藏"),
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    homeViewModel: HomeViewModel = koinViewModel(),
    offsetAnimation: IntOffset,
) {
//    OnLifecycle(onLifecycle = homeViewModel::onCreate, lifecycleEvent = Lifecycle.Event.ON_CREATE)
    HomeScreen(
        modifier = modifier,
        state = homeViewModel.state,
        navToPictureScreen = navHostController::navigateToPictureScreen,
        navToSearchScreen = navHostController::navigateToSearchScreen,
        homeViewModel = homeViewModel,
        dispatch = homeViewModel::dispatch,
        offsetAnimation = offsetAnimation
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeState,
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
    val scaffoldState = rememberScaffoldState()
    val onUnBookmark = { id: Long ->
        scope.launch {
            val result = scaffoldState.snackbarHostState.showSnackbar(
                message = "撤销",
                actionLabel = HomeSnackbar.REVOKE_UNBOOKMARK.actionLabel,
                duration = SnackbarDuration.Long,
            )
            when (result) {
                SnackbarResult.Dismissed -> {}

                SnackbarResult.ActionPerformed -> {
                    homeViewModel.onBookmarkClick(id, true)
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        scope.launch {
            homeViewModel.exception.collect {
                scaffoldState.snackbarHostState.showSnackbar(it?.message ?: "未知错误")
            }
        }
    }

    BaseScreen(
        title = stringResource(R.string.app_name),
        scaffoldState = scaffoldState,
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
        snackbarHost = {
            SnackbarHost(it) {
                when (it.actionLabel) {
                    HomeSnackbar.REVOKE_UNBOOKMARK.actionLabel -> {
                        TextSnackbar(
                            text = it.message,
                            action = {
                                Row {
                                    IconButton(
                                        onClick = { scaffoldState.snackbarHostState.currentSnackbarData?.performAction() },
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Undo,
                                            contentDescription = null
                                        )
                                    }
                                    it.actionLabel?.let {
                                        Text(text = it)
                                    }
                                }
                            }
                        )
                    }

                    else -> {
                        TextSnackbar(text = it.message)
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
                    backgroundColor = MaterialTheme.colors.background,
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
        ) {
            HomeContent(
                navToPictureScreen = navToPictureScreen,
                scaffoldState = scaffoldState,
                state = state,
                lazyStaggeredGridState = lazyStaggeredGridState,
                onBookmarkClick = { id, bookmark ->
                    homeViewModel.onBookmarkClick(id, bookmark)
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