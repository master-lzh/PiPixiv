package com.mrl.pixiv.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.ui.Screen
import com.mrl.pixiv.common.ui.components.TextField
import com.mrl.pixiv.common_ui.item.SquareIllustItem
import com.mrl.pixiv.common_ui.util.StatusBarSpacer
import com.mrl.pixiv.common_ui.util.navigateToPictureScreen
import com.mrl.pixiv.common_ui.util.navigateToSearchResultScreen
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.search.viewmodel.SearchAction
import com.mrl.pixiv.search.viewmodel.SearchState
import com.mrl.pixiv.search.viewmodel.SearchViewModel
import org.koin.androidx.compose.koinViewModel

private const val SPAN_COUNT = 2

@Composable
fun SearchScreen1(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    searchViewModel: SearchViewModel = koinViewModel(),
) {
    SearchScreen(
        modifier = modifier,
        state = searchViewModel.state,
        navigateToResult = navHostController::navigateToSearchResultScreen,
        popBack = { navHostController.popBackStack() },
        dispatch = searchViewModel::dispatch,
    )
}

@OptIn(ExperimentalMaterialApi::class)
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
    Screen(
        topBar = {
            Column {
                StatusBarSpacer()
                Row(
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = popBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        color = Color(0xFFE0E0E0),
                        shape = MaterialTheme.shapes.small
                    ) {
                        TextField(
                            value = textState,
                            onValueChange = {
                                textState = it
                                dispatch(SearchAction.UpdateSearchWords(it.text))
                                dispatch(SearchAction.SearchAutoComplete(it.text))
                            },
                            placeholder = { Text("输入关键字") },
                            minHeight = 40.dp,
                            contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
                                top = 2.dp,
                                bottom = 2.dp
                            ),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color(0xFFE0E0E0),
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                            ),
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    dispatch(
                                        SearchAction.SearchIllust(
                                            searchWords = textState.text,
                                            searchFilter = state.searchFilter,
                                        )
                                    )
                                    navigateToResult()
                                }
                            )
                        )
                    }
                }
            }
        }
    ) {
        // 用LazyColumn构造自动补全列表，点击跳转搜索结果页面
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
            contentPadding = it,
        ) {
            items(state.autoCompleteSearchWords) { word ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp)
                        .padding(vertical = 4.dp)
                        .clickable {
                            dispatch(SearchAction.UpdateSearchWords(word.name))
                            dispatch(
                                SearchAction.SearchIllust(
                                    searchWords = word.name,
                                    searchFilter = state.searchFilter,
                                )
                            )
                            navigateToResult()
                        }
                ) {
                    Text(
                        text = word.name,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                    Text(
                        text = word.translatedName,
                        style = MaterialTheme.typography.caption,
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
    state: SearchState = SearchState.INITIAL,
    navHostController: NavHostController,
    searchViewModel: SearchViewModel = koinViewModel(),
) {
    SearchResultScreen(
        modifier = modifier,
        state = searchViewModel.state,
        popBack = navHostController::popBackStack,
        naviToPic = navHostController::navigateToPictureScreen,
        dispatch = searchViewModel::dispatch,
    )
}

@Preview
@Composable
internal fun SearchResultScreen(
    modifier: Modifier = Modifier,
    state: SearchState = SearchState.INITIAL,
    popBack: () -> Unit = {},
    naviToPic: (Illust) -> Unit = {},
    dispatch: (SearchAction) -> Unit = {},
) {
    Screen(
        topBar = {
            Column {
                StatusBarSpacer()
                TopAppBar(
                    title = { Text(text = state.searchWords) },
                    backgroundColor = MaterialTheme.colors.background,
                    elevation = 0.dp,
                    navigationIcon = {
                        IconButton(onClick = popBack) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        }
    ) {
        LazyColumn(
            modifier = modifier,
            contentPadding = it,
        ) {
            items(state.searchResults.chunked(SPAN_COUNT)) { illusts ->
                Row {
                    illusts.forEach { illust ->
                        SquareIllustItem(
                            isBookmark = illust.isBookmarkedReal,
                            spanCount = SPAN_COUNT,
                            url = illust.imageUrls.squareMedium,
                            imageCount = illust.pageCount,
                            paddingValues = PaddingValues(2.dp),
                            onClick = { naviToPic(illust) },
                        )
                    }
                }
            }
        }
    }
}

