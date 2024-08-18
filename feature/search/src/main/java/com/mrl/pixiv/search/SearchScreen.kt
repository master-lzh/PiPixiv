package com.mrl.pixiv.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.lifecycle.OnLifecycle
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.Screen
import com.mrl.pixiv.common.ui.components.m3.TextField
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common.util.navigateToSearchResultScreen
import com.mrl.pixiv.search.viewmodel.SearchAction
import com.mrl.pixiv.search.viewmodel.SearchState
import com.mrl.pixiv.search.viewmodel.SearchViewModel
import com.mrl.pixiv.util.DebounceUtil
import com.mrl.pixiv.util.throttleClick
import org.koin.androidx.compose.koinViewModel


@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchNavHostController: NavHostController = LocalNavigator.currentOrThrow,
    searchViewModel: SearchViewModel = koinViewModel(),
    navHostController: NavHostController,
) {
    OnLifecycle(Lifecycle.Event.ON_CREATE) {
        searchViewModel.dispatch(SearchAction.LoadSearchHistory)
    }
    SearchScreen_(
        modifier = modifier,
        state = searchViewModel.state,
        navigateToResult = searchNavHostController::navigateToSearchResultScreen,
        popBack = { navHostController.popBackStack() },
        dispatch = searchViewModel::dispatch,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Preview
@Composable
internal fun SearchScreen_(
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
                            shape = MaterialTheme.shapes.extraLarge
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
                                placeholder = { Text(stringResource(R.string.enter_keywords)) },
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
                                shape = MaterialTheme.shapes.extraLarge,
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
                        text = if (state.searchWords.isEmpty())
                            stringResource(R.string.search_history)
                        else
                            stringResource(R.string.find_for),
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
                            }
                            .animateItem(),
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
