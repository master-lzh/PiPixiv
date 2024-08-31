package com.mrl.pixiv.collection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mrl.pixiv.collection.components.FilterDialog
import com.mrl.pixiv.collection.viewmodel.CollectionAction
import com.mrl.pixiv.collection.viewmodel.CollectionState
import com.mrl.pixiv.collection.viewmodel.CollectionViewModel
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.Screen
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common.ui.illust.IllustGrid
import com.mrl.pixiv.common.ui.lightBlue
import com.mrl.pixiv.common.util.navigateToPictureScreen
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.Restrict
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SelfCollectionScreen(
    modifier: Modifier = Modifier,
    collectionViewModel: CollectionViewModel = koinViewModel { parametersOf(Long.MIN_VALUE) },
    navHostController: NavHostController = LocalNavigator.currentOrThrow
) {
    CollectionScreen_(
        modifier = modifier,
        state = collectionViewModel.state,
        dispatch = collectionViewModel::dispatch,
        popBack = { navHostController.popBackStack() },
        navToPictureScreen = navHostController::navigateToPictureScreen
    )
}

@Preview(name = "Phone", device = Devices.PHONE, showSystemUi = true)
@Composable
fun CollectionScreen_(
    modifier: Modifier = Modifier,
    state: CollectionState = CollectionState.INITIAL,
    dispatch: (CollectionAction) -> Unit = {},
    popBack: () -> Unit = {},
    navToPictureScreen: (Illust, String) -> Unit = { _, _ -> }
) {
    Screen(
        modifier = modifier,
        topBar = {
            TopAppBar(
                modifier = Modifier.shadow(4.dp),
                title = {
                    Text(text = stringResource(R.string.collection))
                },
                navigationIcon = {
                    IconButton(onClick = popBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) {
        val scope = rememberCoroutineScope()
        var showFilterDialog by remember { mutableStateOf(false) }
        val lazyGridState = rememberLazyGridState()
        var selectedTab by remember(state.restrict) { mutableIntStateOf(if (state.restrict == Restrict.PUBLIC) 0 else 1) }
        val pagerState = rememberPagerState(initialPage = selectedTab, pageCount = { 2 })
        val pullRefreshState = rememberPullToRefreshState()

        PullToRefreshBox(
            isRefreshing = state.refreshing,
            onRefresh = {
                dispatch(
                    CollectionAction.LoadUserBookmarksIllusts(
                        state.restrict,
                        state.filterTag
                    )
                )
            },
            modifier = Modifier.padding(it),
            state = pullRefreshState
        ) {
            IllustGrid(
                illusts = state.userBookmarksIllusts,
                spanCount = 2,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                lazyGridState = lazyGridState,
                navToPictureScreen = navToPictureScreen,
                canLoadMore = state.illustNextUrl != null,
                onLoadMore = {
                    if (state.illustNextUrl != null) {
                        dispatch(CollectionAction.LoadMoreUserBookmarksIllusts(state.illustNextUrl))
                    }
                },
                loading = state.loading,
                leadingContent = {
                    item(key = "leading", span = { GridItemSpan(2) }) {
                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }
            )
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            if (state.restrict != Restrict.PUBLIC) {
                                scope.launch { pagerState.animateScrollToPage(0) }
                                dispatch(CollectionAction.UpdateRestrict(Restrict.PUBLIC))
                            }
                        },
                        colors = ButtonDefaults.filledTonalButtonColors().copy(
                            containerColor = if (state.restrict == Restrict.PUBLIC)
                                lightBlue
                            else
                                lightBlue.copy(alpha = 0.5f)
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.word_public),
                        )
                    }
                    TextButton(
                        onClick = {
                            if (state.restrict != Restrict.PRIVATE) {
                                scope.launch { pagerState.animateScrollToPage(1) }
                                dispatch(CollectionAction.UpdateRestrict(Restrict.PRIVATE))
                            }
                        },
                        colors = ButtonDefaults.filledTonalButtonColors().copy(
                            containerColor = if (state.restrict == Restrict.PRIVATE)
                                lightBlue
                            else
                                lightBlue.copy(alpha = 0.5f)
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.word_private),
                        )
                    }

                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Rounded.FilterList, contentDescription = null)
                    }
                }
            }
        }
        if (showFilterDialog) {
            FilterDialog(
                onDismissRequest = { showFilterDialog = false },
                selectedTab = selectedTab,
                switchTab = { selectedTab = it },
                pagerState = pagerState,
                userBookmarkTagsIllust = state.userBookmarkTagsIllust,
                restrict = state.restrict,
                filterTag = state.filterTag,
                dispatch = dispatch
            )
        }
    }
}