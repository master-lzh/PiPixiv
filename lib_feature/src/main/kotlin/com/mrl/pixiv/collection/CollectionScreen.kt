package com.mrl.pixiv.collection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.mrl.pixiv.collection.components.FilterDialog
import com.mrl.pixiv.common.data.Restrict
import com.mrl.pixiv.common.datasource.local.mmkv.isSelf
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common.ui.illust.illustGrid
import com.mrl.pixiv.common.util.RString
import com.mrl.pixiv.common.util.navigateToPictureScreen
import com.mrl.pixiv.common.viewmodel.asState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SelfCollectionScreen(
    uid: Long,
    modifier: Modifier = Modifier,
    viewModel: CollectionViewModel = koinViewModel { parametersOf(uid) },
    navHostController: NavHostController = LocalNavigator.currentOrThrow
) {
    val state = viewModel.asState()
    val userBookmarksIllusts = viewModel.userBookmarksIllusts.collectAsLazyPagingItems()
    val dispatch = viewModel::dispatch
    var showFilterDialog by remember { mutableStateOf(false) }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                modifier = Modifier.shadow(4.dp),
                title = {
                    Text(text = stringResource(RString.collection))
                },
                navigationIcon = {
                    IconButton(onClick = navHostController::popBackStack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (uid.isSelf) {
                        IconButton(onClick = { showFilterDialog = true }) {
                            Icon(Icons.Rounded.FilterList, contentDescription = null)
                        }
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets.statusBars
    ) {
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
            LazyVerticalGrid(
                state = lazyGridState,
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                contentPadding = PaddingValues(
                    start = 8.dp,
                    top = 10.dp,
                    end = 8.dp,
                    bottom = 20.dp
                ),
            ) {
                illustGrid(
                    illusts = userBookmarksIllusts,
                    navToPictureScreen = navHostController::navigateToPictureScreen,
                )
            }
        }
        if (showFilterDialog) {
            FilterDialog(
                onDismissRequest = { showFilterDialog = false },
                selectedTab = selectedTab,
                switchTab = { selectedTab = it },
                pagerState = pagerState,
                userBookmarkTagsIllust = state.userBookmarkTagsIllust,
                privateBookmarkTagsIllust = state.privateBookmarkTagsIllust,
                restrict = state.restrict,
                filterTag = state.filterTag,
                dispatch = dispatch,
                onSelected = { tag: String? ->
                    when (selectedTab) {
                        0 -> viewModel.updateFilterTag(Restrict.PUBLIC, tag)
                        1 -> viewModel.updateFilterTag(Restrict.PRIVATE, tag)
                    }
                    userBookmarksIllusts.refresh()
                }
            )
        }
    }
}