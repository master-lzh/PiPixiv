package com.mrl.pixiv.search

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.middleware.bookmark.BookmarkAction
import com.mrl.pixiv.common.middleware.bookmark.BookmarkState
import com.mrl.pixiv.common.middleware.bookmark.BookmarkViewModel
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.Screen
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common_ui.illust.IllustGrid
import com.mrl.pixiv.common_ui.util.navigateToPictureScreen
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.search.SearchSort
import com.mrl.pixiv.data.search.SearchTarget
import com.mrl.pixiv.search.viewmodel.SearchAction
import com.mrl.pixiv.search.viewmodel.SearchState
import com.mrl.pixiv.search.viewmodel.SearchViewModel
import com.mrl.pixiv.util.throttleClick
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchResultScreen(
    modifier: Modifier = Modifier,
    searchNavHostController: NavHostController = LocalNavigator.currentOrThrow,
    bookmarkViewModel: BookmarkViewModel,
    searchViewModel: SearchViewModel = koinViewModel(),
    navHostController: NavHostController,
) {
    SearchResultScreen_(
        modifier = modifier,
        state = searchViewModel.state,
        bookmarkState = bookmarkViewModel.state,
        popBack = searchNavHostController::popBackStack,
        naviToPic = navHostController::navigateToPictureScreen,
        dispatch = searchViewModel::dispatch,
        bookmarkDispatch = bookmarkViewModel::dispatch,
    )
}

@Composable
fun OutsideSearchResultsScreen(
    modifier: Modifier = Modifier,
    searchWord: String,
    bookmarkViewModel: BookmarkViewModel,
    searchViewModel: SearchViewModel = koinViewModel(),
    navHostController: NavHostController = LocalNavigator.currentOrThrow,
) {
    val currentSearch by remember { mutableStateOf(searchWord) }
    LaunchedEffect(currentSearch) {
        searchViewModel.dispatch(SearchAction.UpdateSearchWords(currentSearch))
        searchViewModel.dispatch(SearchAction.SearchIllust(searchWords = currentSearch))
    }
    SearchResultScreen_(
        modifier = modifier,
        state = searchViewModel.state,
        bookmarkState = bookmarkViewModel.state,
        popBack = navHostController::popBackStack,
        naviToPic = navHostController::navigateToPictureScreen,
        dispatch = searchViewModel::dispatch,
        bookmarkDispatch = bookmarkViewModel::dispatch,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showSystemUi = false, showBackground = false,
    device = "id:pixel_7_pro", wallpaper = Wallpapers.NONE,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
internal fun SearchResultScreen_(
    modifier: Modifier = Modifier,
    state: SearchState = SearchState.INITIAL,
    popBack: () -> Unit = {},
    naviToPic: (Illust, String) -> Unit = { _, _ -> },
    dispatch: (SearchAction) -> Unit = {},
    bookmarkDispatch: (BookmarkAction) -> Unit = {},
    bookmarkState: BookmarkState = BookmarkState.INITIAL,
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
    Screen(
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
        IllustGrid(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 8.dp),
            illusts = state.searchResults,
            bookmarkState = bookmarkState,
            dispatch = bookmarkDispatch,
            spanCount = spanCount,
            navToPictureScreen = naviToPic,
            loading = state.loading,
            canLoadMore = state.nextUrl != null,
            onLoadMore = {
                if (state.nextUrl != null) {
                    dispatch(SearchAction.SearchIllustNext(state.nextUrl))
                }
            }
        )

        if (showBottomSheet.value) {
            FilterBottomSheet(showBottomSheet, launchDefault, bottomSheetState, state, dispatch)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun FilterBottomSheet(
    showBottomSheet: MutableState<Boolean>,
    launch: (suspend CoroutineScope.() -> Unit) -> Job,
    bottomSheetState: SheetState,
    state: SearchState,
    dispatch: (SearchAction) -> Unit,
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
                SearchTarget.PARTIAL_MATCH_FOR_TAGS to context.getString(R.string.tags_partially_match),
                SearchTarget.EXACT_MATCH_FOR_TAGS to context.getString(R.string.tags_exact_match),
                SearchTarget.TITLE_AND_CAPTION to context.getString(R.string.title_and_description),
            )
        }
        val searchSortMap = remember {
            mapOf(
                SearchSort.DATE_DESC to context.getString(R.string.date_desc),
                SearchSort.DATE_ASC to context.getString(R.string.date_asc),
                SearchSort.POPULAR_DESC to context.getString(R.string.popular_desc),
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
            Text(text = stringResource(R.string.filter))
            Text(
                text = stringResource(R.string.apply),
                modifier = Modifier.throttleClick {
                    dispatch(SearchAction.ClearSearchResult)
                    dispatch(
                        SearchAction.SearchIllust(searchWords = state.searchWords)
                    )
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
                            SearchAction.UpdateFilter(
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
                            SearchAction.UpdateFilter(
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