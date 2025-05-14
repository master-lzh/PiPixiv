package com.mrl.pixiv.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.kts.spaceBy
import com.mrl.pixiv.common.repository.SearchRepository
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.components.m3.TextField
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common.util.DebounceUtil
import com.mrl.pixiv.common.util.RString
import com.mrl.pixiv.common.util.navigateToSearchResultScreen
import com.mrl.pixiv.common.util.throttleClick
import com.mrl.pixiv.common.viewmodel.asState
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel(),
    navHostController: NavHostController = LocalNavigator.currentOrThrow,
) {
    val dispatch = viewModel::dispatch
    val state = viewModel.asState()
    val searchHistory by SearchRepository.searchHistoryFlow.collectAsStateWithLifecycle()
    var textState by remember { mutableStateOf(TextFieldValue(state.searchWords)) }
    val focusRequester = remember { FocusRequester() }
    LifecycleResumeEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {
        }
        textState = textState.copy(selection = TextRange(textState.text.length))
        onPauseOrDispose { }
    }
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    Scaffold(
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
                        IconButton(
                            onClick = {
                                navHostController.popBackStack()
                            }
                        ) {
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
                                placeholder = { Text(stringResource(RString.enter_keywords)) },
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
                                        dispatch(SearchAction.AddSearchHistory(textState.text))
                                        focusRequester.freeFocus()
                                        navHostController.navigateToSearchResultScreen(textState.text)
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
                .padding(it)
                .padding(top = 8.dp)
                .imePadding(),
            contentPadding = PaddingValues(horizontal = 8.dp),
            verticalArrangement = 16f.spaceBy
        ) {
            stickyHeader {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background),
                ) {
                    Text(
                        text = if (state.searchWords.isEmpty())
                            stringResource(RString.search_history)
                        else
                            stringResource(RString.find_for),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                    )
                }
            }
            if (state.searchWords.isEmpty()) {
                items(searchHistory.searchHistoryList) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .throttleClick(indication = ripple()) {
                                dispatch(SearchAction.AddSearchHistory(it.keyword))
                                focusRequester.freeFocus()
                                navHostController.navigateToSearchResultScreen(it.keyword)
                            }
                            .padding(8.dp)
                            .animateItem(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = it.keyword,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Icon(
                            modifier = Modifier
                                .size(24.dp)
                                .throttleClick(indication = ripple()) {
                                    dispatch(SearchAction.DeleteSearchHistory(it.keyword))
                                },
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "delete"
                        )
                    }
                }
            }
            items(state.autoCompleteSearchWords, key = { it.name }) { word ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .throttleClick(indication = ripple()) {
                            dispatch(SearchAction.AddSearchHistory(word.name))
                            focusRequester.freeFocus()
                            navHostController.navigateToSearchResultScreen(word.name)
                        }
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = word.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                    if (word.translatedName.isNotBlank()) {
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
}
