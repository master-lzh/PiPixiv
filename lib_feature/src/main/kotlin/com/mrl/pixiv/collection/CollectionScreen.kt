package com.mrl.pixiv.collection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.mrl.pixiv.collection.components.FilterDialog
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.data.Restrict
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common.ui.illust.IllustGrid
import com.mrl.pixiv.common.ui.lightBlue
import com.mrl.pixiv.common.util.NavigateToHorizontalPictureScreen
import com.mrl.pixiv.common.util.RString
import com.mrl.pixiv.common.util.navigateToPictureScreen
import com.mrl.pixiv.common.viewmodel.asState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SelfCollectionScreen(
    uid: Long,
    modifier: Modifier = Modifier,
    collectionViewModel: CollectionViewModel = koinViewModel { parametersOf(uid) },
    navHostController: NavHostController = LocalNavigator.currentOrThrow
) {
    CollectionScreen_(
        modifier = modifier,
        state = collectionViewModel.asState(),
        userBookmarksIllusts = collectionViewModel.userBookmarksIllusts.collectAsLazyPagingItems(),
        dispatch = collectionViewModel::dispatch,
        popBack = navHostController::popBackStack,
        navToPictureScreen = navHostController::navigateToPictureScreen
    )
}

@Composable
fun CollectionScreen_(
    userBookmarksIllusts: LazyPagingItems<Illust>,
    modifier: Modifier = Modifier,
    state: CollectionState = CollectionState(),
    dispatch: (CollectionAction) -> Unit = {},
    popBack: () -> Unit = {},
    navToPictureScreen: NavigateToHorizontalPictureScreen = { _, _, _ -> }
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                modifier = Modifier.shadow(4.dp),
                title = {
                    Text(text = stringResource(RString.collection))
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
            isRefreshing = userBookmarksIllusts.loadState.refresh is LoadState.Loading,
            onRefresh = { userBookmarksIllusts.refresh() },
            modifier = Modifier.padding(it),
            state = pullRefreshState
        ) {
            IllustGrid(
                illusts = userBookmarksIllusts,
                spanCount = 2,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                lazyGridState = lazyGridState,
                navToPictureScreen = navToPictureScreen
            ) {
                item(key = "leading", span = { GridItemSpan(2) }) {
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
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
                                userBookmarksIllusts.refresh()
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
                            text = stringResource(RString.word_public),
                        )
                    }
                    TextButton(
                        onClick = {
                            if (state.restrict != Restrict.PRIVATE) {
                                scope.launch { pagerState.animateScrollToPage(1) }
                                dispatch(CollectionAction.UpdateRestrict(Restrict.PRIVATE))
                                userBookmarksIllusts.refresh()
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
                            text = stringResource(RString.word_private),
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
                dispatch = dispatch,
                onSelected = { tag: String ->
                    when (selectedTab) {
                        0 -> dispatch(CollectionAction.UpdateFilterTag(Restrict.PUBLIC, tag))
                        1 -> dispatch(CollectionAction.UpdateFilterTag(Restrict.PRIVATE, tag))
                    }
                    userBookmarksIllusts.refresh()
                }
            )
        }
    }
}