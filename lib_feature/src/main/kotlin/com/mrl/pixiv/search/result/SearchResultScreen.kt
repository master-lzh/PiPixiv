package com.mrl.pixiv.search.result

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.data.search.SearchSort
import com.mrl.pixiv.common.data.search.SearchTarget
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common.ui.illust.IllustGrid
import com.mrl.pixiv.common.util.NavigateToHorizontalPictureScreen
import com.mrl.pixiv.common.util.RString
import com.mrl.pixiv.common.util.navigateToPictureScreen
import com.mrl.pixiv.common.util.throttleClick
import com.mrl.pixiv.common.viewmodel.asState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SearchResultsScreen(
    searchWords: String,
    modifier: Modifier = Modifier,
    searchResultViewModel: SearchResultViewModel = koinViewModel { parametersOf(searchWords) },
    navHostController: NavHostController = LocalNavigator.currentOrThrow,
) {
    val state = searchResultViewModel.asState()
    SearchResultScreen_(
        modifier = modifier,
        state = state,
        searchResults = searchResultViewModel.searchResults.collectAsLazyPagingItems(),
        popBack = navHostController::popBackStack,
        naviToPic = navHostController::navigateToPictureScreen,
        dispatch = searchResultViewModel::dispatch,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchResultScreen_(
    modifier: Modifier = Modifier,
    state: SearchResultState = SearchResultState(),
    searchResults: LazyPagingItems<Illust>,
    popBack: () -> Unit = {},
    naviToPic: NavigateToHorizontalPictureScreen = { _, _, _ -> },
    dispatch: (SearchResultAction) -> Unit = {},
) {
    val showBottomSheet = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val launchDefault = { block: suspend CoroutineScope.() -> Unit ->
        scope.launch { block() }
    }
    val bottomSheetState = rememberModalBottomSheetState()
    val spanCount = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> 2
        Configuration.ORIENTATION_LANDSCAPE -> 4
        else -> 2
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .throttleClick { popBack() },
                        text = state.searchWords
                    )
                },
                navigationIcon = {
                    IconButton(onClick = popBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    //筛选按钮
                    IconButton(onClick = {
                        showBottomSheet.value = true
                        scope.launch { bottomSheetState.show() }
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.FilterAlt,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
        PullToRefreshBox(
            isRefreshing = searchResults.loadState.refresh is LoadState.Loading,
            onRefresh = { searchResults.refresh() },
            modifier = modifier.padding(it),
        ) {
            IllustGrid(
                illusts = searchResults,
                spanCount = spanCount,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                navToPictureScreen = naviToPic
            )
        }

        if (showBottomSheet.value) {
            FilterBottomSheet(
                showBottomSheet,
                launchDefault,
                bottomSheetState,
                state,
                searchResults,
                dispatch
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun FilterBottomSheet(
    showBottomSheet: MutableState<Boolean>,
    launch: (suspend CoroutineScope.() -> Unit) -> Job,
    bottomSheetState: SheetState,
    state: SearchResultState,
    searchResults: LazyPagingItems<Illust>,
    dispatch: (SearchResultAction) -> Unit,
) {
    val context = LocalContext.current
    ModalBottomSheet(
        onDismissRequest = {
            showBottomSheet.value = false
            launch { bottomSheetState.hide() }
        },
        sheetState = bottomSheetState,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        val filter = state.searchFilter
        val searchTargetMap = remember {
            mapOf(
                SearchTarget.PARTIAL_MATCH_FOR_TAGS to context.getString(RString.tags_partially_match),
                SearchTarget.EXACT_MATCH_FOR_TAGS to context.getString(RString.tags_exact_match),
                SearchTarget.TITLE_AND_CAPTION to context.getString(RString.title_and_description),
            )
        }
        val searchSortMap = remember {
            mapOf(
                SearchSort.DATE_DESC to context.getString(RString.date_desc),
                SearchSort.DATE_ASC to context.getString(RString.date_asc),
                SearchSort.POPULAR_DESC to context.getString(RString.popular_desc),
            )
        }
        var selectedTargetIndex by remember { mutableIntStateOf(searchTargetMap.keys.indexOf(filter.searchTarget)) }
        var selectedSortIndex by remember { mutableIntStateOf(searchSortMap.keys.indexOf(filter.sort)) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(RString.filter))
            Text(
                text = stringResource(RString.apply),
                modifier = Modifier.throttleClick {
//                    dispatch(SearchResultAction.ClearSearchResult)
                    searchResults.refresh()
                    showBottomSheet.value = false
                    launch { bottomSheetState.hide() }
                })
        }
        SelectedTabRow(selectedIndex = selectedTargetIndex) {
            searchTargetMap.forEach { (key, value) ->
                Tab(
                    modifier = Modifier.clip(MaterialTheme.shapes.medium),
                    selected = filter.searchTarget == key,
                    onClick = {
                        selectedTargetIndex = searchTargetMap.keys.indexOf(key)
                        dispatch(
                            SearchResultAction.UpdateFilter(
                                filter.copy(searchTarget = key)
                            )
                        )
                    }
                ) {
                    Text(
                        text = value,
                        modifier = Modifier.padding(vertical = 4.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        SelectedTabRow(selectedIndex = selectedSortIndex) {
            searchSortMap.forEach { (key, value) ->
                Tab(
                    modifier = Modifier.clip(MaterialTheme.shapes.medium),
                    selected = filter.sort == key,
                    onClick = {
                        selectedSortIndex = searchSortMap.keys.indexOf(key)
                        dispatch(
                            SearchResultAction.UpdateFilter(
                                filter.copy(sort = key)
                            )
                        )
                    }
                ) {
                    Text(
                        text = value,
                        modifier = Modifier.padding(vertical = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
private fun SelectedTabRow(
    selectedIndex: Int,
    tabs: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        TabRow(
            selectedTabIndex = selectedIndex,
            containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            contentColor = MaterialTheme.colorScheme.onSurface,
            indicator = {
                Surface(
                    modifier = Modifier
                        .tabIndicatorOffset(it[selectedIndex])
                        .fillMaxHeight(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                ) {}
            },
            divider = {}
        ) {
            tabs()
        }
    }
}