package com.mrl.pixiv.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.components.TextSnackbar
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common.util.*
import com.mrl.pixiv.common.viewmodel.bookmark.BookmarkState
import com.mrl.pixiv.home.components.HomeTopBar
import com.mrl.pixiv.home.components.RecommendGrid
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


internal enum class HomeSnackbar(val actionLabel: String) {
    REVOKE_UNBOOKMARK(AppUtil.getString(RString.revoke_cancel_like)),
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = LocalNavigator.currentOrThrow,
    homeViewModel: HomeViewModel = koinViewModel(),
) {
    val recommendImageList = homeViewModel.recommendImageList.collectAsLazyPagingItems()
    HomeScreen(
        modifier = modifier,
        navToPictureScreen = navHostController::navigateToPictureScreen,
        onRefresh = recommendImageList::refresh,
        recommendImageList = recommendImageList,
        dispatch = homeViewModel::dispatch,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    navToPictureScreen: NavigateToHorizontalPictureScreen,
    onRefresh: () -> Unit,
    recommendImageList: LazyPagingItems<Illust>,
    dispatch: (HomeAction) -> Unit = {},
) {
    val context = LocalContext.current
    val lazyStaggeredGridState = rememberLazyStaggeredGridState()
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullToRefreshState()
    val snackBarHostState = remember { SnackbarHostState() }
    val onUnBookmark = { id: Long ->
        scope.launch {
            val result = snackBarHostState.showSnackbar(
                message = context.getString(RString.revoke),
                actionLabel = HomeSnackbar.REVOKE_UNBOOKMARK.actionLabel,
                duration = SnackbarDuration.Short,
            )
            when (result) {
                SnackbarResult.Dismissed -> {}

                SnackbarResult.ActionPerformed -> {
                    BookmarkState.bookmarkIllust(id)
                }
            }
        }
    }
    LaunchedEffect(recommendImageList.loadState.refresh) {
        when (recommendImageList.loadState.refresh) {
            is LoadState.Loading -> pullRefreshState.animateToThreshold()
            else -> pullRefreshState.animateToHidden()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(RString.app_name)) },
                actions = {
                    HomeTopBar(
                        onRefreshToken = { dispatch(HomeAction.RefreshTokenIntent) },
                        onRefresh = onRefresh
                    )
                }
            )
        },
        snackbarHost = {
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
            if (recommendImageList.itemCount > 0) {
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
        PullToRefreshBox(
            isRefreshing = recommendImageList.loadState.refresh is LoadState.Loading,
            onRefresh = onRefresh,
            modifier = Modifier.padding(it),
            state = pullRefreshState
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                RecommendGrid(
                    recommendImageList = recommendImageList,
                    navToPictureScreen = navToPictureScreen,
                    lazyStaggeredGridState = lazyStaggeredGridState,
                )
            }
        }
    }
}