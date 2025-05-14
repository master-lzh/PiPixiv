package com.mrl.pixiv.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.util.RString
import com.mrl.pixiv.common.util.navigateToPictureScreen
import com.mrl.pixiv.home.components.RecommendGrid
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = LocalNavigator.current,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val recommendImageList = viewModel.recommendImageList.collectAsLazyPagingItems()
    val lazyStaggeredGridState = rememberLazyStaggeredGridState()
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullToRefreshState()
    val onRefresh = recommendImageList::refresh

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(RString.app_name)) },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                lazyStaggeredGridState.scrollToItem(0)
                            }
                            onRefresh()
                        }
                    ) {
                        Icon(imageVector = Icons.Rounded.Refresh, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            if (recommendImageList.itemCount > 0) {
                FloatingActionButton(
                    modifier = Modifier,
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
                    navToPictureScreen = navHostController::navigateToPictureScreen,
                    lazyStaggeredGridState = lazyStaggeredGridState,
                )
            }
        }
    }
}