package com.mrl.pixiv.search

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.compose.OnLifecycle
import com.mrl.pixiv.common.middleware.bookmark.BookmarkAction
import com.mrl.pixiv.common.middleware.bookmark.BookmarkState
import com.mrl.pixiv.common.middleware.bookmark.BookmarkViewModel
import com.mrl.pixiv.common.ui.Screen
import com.mrl.pixiv.common.ui.components.TextField
import com.mrl.pixiv.common_ui.item.SquareIllustItem
import com.mrl.pixiv.common_ui.util.OnScrollToBottom
import com.mrl.pixiv.common_ui.util.navigateToMainScreen
import com.mrl.pixiv.common_ui.util.navigateToPictureScreen
import com.mrl.pixiv.common_ui.util.navigateToSearchResultScreen
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.search.SearchSort
import com.mrl.pixiv.data.search.SearchTarget
import com.mrl.pixiv.search.viewmodel.SearchAction
import com.mrl.pixiv.search.viewmodel.SearchState
import com.mrl.pixiv.search.viewmodel.SearchViewModel
import com.mrl.pixiv.util.DebounceUtil
import com.mrl.pixiv.util.throttleClick
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun SearchScreen1(
    modifier: Modifier = Modifier,
    searchNavHostController: NavHostController,
    searchViewModel: SearchViewModel = koinViewModel(),
    navHostController: NavHostController,
) {
    OnLifecycle(Lifecycle.Event.ON_CREATE) {
        searchViewModel.dispatch(SearchAction.LoadSearchHistory)
    }
    SearchScreen(
        modifier = modifier,
        state = searchViewModel.state,
        navigateToResult = searchNavHostController::navigateToSearchResultScreen,
        popBack = { navHostController.navigateToMainScreen() },
        dispatch = searchViewModel::dispatch,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class
)
@Preview
@Composable
internal fun SearchScreen(
    modifier: Modifier = Modifier,
    state: SearchState = SearchState.INITIAL,
    navigateToResult: () -> Unit = {},
    dispatch: (SearchAction) -> Unit = {},
    popBack: () -> Unit = {},
) {
    var textState by remember { mutableStateOf(TextFieldValue(state.searchWords)) }
    val focusRequester = remember { FocusRequester() }
    OnLifecycle(Lifecycle.Event.ON_RESUME) {
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {
        }
        textState = textState.copy(selection = TextRange(textState.text.length))
    }
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    Screen(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            focusManager.clearFocus()
            softwareKeyboardController?.hide()
        },
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    Row(
                        modifier = Modifier
                            .height(56.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            popBack()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            shape = MaterialTheme.shapes.small
                        ) {
                            TextField(
                                value = textState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester)
                                    .throttleClick {
                                        focusRequester.requestFocus()
                                    },
                                onValueChange = {
                                    textState = it
                                    dispatch(SearchAction.UpdateSearchWords(it.text))
                                    if (it.text.isNotBlank() && it.text != state.searchWords) {
                                        DebounceUtil.debounce {
                                            dispatch(SearchAction.SearchAutoComplete(it.text))
                                        }
                                    } else {
                                        dispatch(SearchAction.ClearAutoCompleteSearchWords)
                                    }
                                },
                                placeholder = { Text("输入关键字") },
                                minHeight = 40.dp,
                                contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                                    top = 2.dp,
                                    bottom = 2.dp,
                                ),
                                colors = TextFieldDefaults.colors(
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                ),
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        dispatch(SearchAction.ClearSearchResult)
                                        dispatch(
                                            SearchAction.SearchIllust(
                                                searchWords = textState.text,
                                            )
                                        )
                                        dispatch(SearchAction.AddSearchHistory(textState.text))
                                        focusRequester.freeFocus()
                                        navigateToResult()
                                    }
                                )
                            )
                        }
                    }
                }
            )
        }
    ) {
        // 用LazyColumn构造自动补全列表，点击跳转搜索结果页面
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
                .padding(WindowInsets.ime.asPaddingValues()),
            contentPadding = it,
        ) {
            stickyHeader {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = if (state.searchWords.isEmpty()) "搜索历史" else "你是不是要找",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(16.dp),
                    )

                }

            }
            if (state.searchWords.isEmpty()) {
                items(state.searchHistory) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp)
                            .padding(vertical = 4.dp)
                            .throttleClick {
                                dispatch(SearchAction.UpdateSearchWords(it.keyword))
                                dispatch(SearchAction.ClearSearchResult)
                                dispatch(SearchAction.SearchIllust(searchWords = it.keyword))
                                dispatch(SearchAction.AddSearchHistory(it.keyword))
                                focusRequester.freeFocus()
                                navigateToResult()
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = it.keyword,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        IconButton(onClick = { dispatch(SearchAction.DeleteSearchHistory(it.keyword)) }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "delete"
                            )
                        }
                    }
                }
            }
            items(state.autoCompleteSearchWords, key = { it.name }) { word ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp)
                        .padding(vertical = 4.dp)
                        .throttleClick {
                            dispatch(SearchAction.UpdateSearchWords(word.name))
                            dispatch(SearchAction.ClearSearchResult)
                            dispatch(SearchAction.SearchIllust(searchWords = word.name))
                            dispatch(SearchAction.AddSearchHistory(word.name))
                            focusRequester.freeFocus()
                            navigateToResult()
                        },
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = word.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                    Text(
                        text = word.translatedName,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultScreen1(
    modifier: Modifier = Modifier,
    searchNavHostController: NavHostController,
    bookmarkViewModel: BookmarkViewModel,
    searchViewModel: SearchViewModel = koinViewModel(),
    navHostController: NavHostController,
) {
    SearchResultScreen(
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
    navHostController: NavHostController,
) {
    val currentSearch by remember { mutableStateOf(searchWord) }
    LaunchedEffect(currentSearch) {
        searchViewModel.dispatch(SearchAction.UpdateSearchWords(currentSearch))
        searchViewModel.dispatch(SearchAction.SearchIllust(searchWords = currentSearch))
    }
    SearchResultScreen(
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
internal fun SearchResultScreen(
    modifier: Modifier = Modifier,
    state: SearchState = SearchState.INITIAL,
    popBack: () -> Unit = {},
    naviToPic: (Illust) -> Unit = {},
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
                    IconButton(onClick = {
                        popBack()
                    }) {
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
        val lazyListState = rememberLazyListState()
        var loadMore by remember { mutableStateOf(false) }
        LazyColumn(
            modifier = modifier,
            state = lazyListState,
            contentPadding = it,
        ) {
            items(
                state.searchResults.chunked(spanCount),
                key = {
                    it.joinToString("_") { illust ->
                        illust.id.toString()
                    }
                }
            ) { illusts ->
                Row {
                    illusts.forEach { illust ->
                        SquareIllustItem(
                            illust = illust,
                            bookmarkState = bookmarkState,
                            dispatch = bookmarkDispatch,
                            spanCount = spanCount,
                            paddingValues = PaddingValues(2.dp),
                            navToPictureScreen = naviToPic,
                        )
                    }
                }
            }
            if (loadMore) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
        lazyListState.OnScrollToBottom {
            loadMore = true
            dispatch(SearchAction.SearchIllustNext(state.nextUrl) {
                loadMore = false
            })
        }

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
                SearchTarget.PARTIAL_MATCH_FOR_TAGS to "标签部分一致",
                SearchTarget.EXACT_MATCH_FOR_TAGS to "标签完全一致",
                SearchTarget.TITLE_AND_CAPTION to "标题和简介",
            )
        }
        val searchSortMap = remember {
            mapOf(
                SearchSort.DATE_DESC to "日期降序",
                SearchSort.DATE_ASC to "日期升序",
                SearchSort.POPULAR_DESC to "人气降序",
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
            Text(text = "筛选")
            Text(
                text = "应用",
                modifier = Modifier.throttleClick {
                    dispatch(SearchAction.ClearSearchResult)
                    dispatch(
                        SearchAction.SearchIllust(searchWords = state.searchWords)
                    )
                    showBottomSheet.value = false
                    launch { bottomSheetState.hide() }
                })
        }
        val textHeight = 30.dp
        SelectedTabRow(selectedIndex = selectedTargetIndex, textHeight = textHeight) {
            searchTargetMap.forEach { (key, value) ->
                Tab(
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
        SelectedTabRow(selectedIndex = selectedSortIndex, textHeight = textHeight) {
            searchSortMap.forEach { (key, value) ->
                Tab(
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
    textHeight: Dp = 25.dp,
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
                        .height(textHeight),
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
