package com.mrl.pixiv.collection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
            CollectionTopAppBar(
                uid = uid,
                showFilterDialog = { showFilterDialog = true },
                onBack = { navHostController.popBackStack() }
            )
        },
        contentWindowInsets = WindowInsets.statusBars
    ) {
        val lazyGridState = rememberLazyGridState()
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
                userBookmarkTagsIllust = state.userBookmarkTagsIllust,
                privateBookmarkTagsIllust = state.privateBookmarkTagsIllust,
                restrict = state.restrict,
                filterTag = state.filterTag,
                onLoadUserBookmarksTags = {
                    dispatch(CollectionAction.LoadUserBookmarksTagsIllust(it))
                },
                onSelected = { restrict: String, tag: String? ->
                    viewModel.updateFilterTag(restrict, tag)
                    userBookmarksIllusts.refresh()
                }
            )
        }
    }
}

@Composable
private fun CollectionTopAppBar(
    uid: Long,
    showFilterDialog: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    TopAppBar(
        modifier = Modifier.shadow(4.dp),
        title = {
            Text(text = stringResource(RString.collection))
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
            }
        },
        actions = {
            if (uid.isSelf) {
                IconButton(onClick = showFilterDialog) {
                    Icon(Icons.Rounded.FilterList, contentDescription = null)
                }
            }
        }
    )
}