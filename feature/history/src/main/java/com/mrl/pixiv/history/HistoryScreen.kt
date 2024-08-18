package com.mrl.pixiv.history

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.Screen
import com.mrl.pixiv.common.ui.components.m3.TextField
import com.mrl.pixiv.common.ui.components.m3.transparentIndicatorColors
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common.ui.illust.IllustGrid
import com.mrl.pixiv.common.util.navigateToPictureScreen
import com.mrl.pixiv.common.viewmodel.bookmark.BookmarkAction
import com.mrl.pixiv.common.viewmodel.bookmark.BookmarkState
import com.mrl.pixiv.common.viewmodel.bookmark.BookmarkViewModel
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.history.viewmodel.HistoryAction
import com.mrl.pixiv.history.viewmodel.HistoryState
import com.mrl.pixiv.history.viewmodel.HistoryViewModel
import com.mrl.pixiv.util.queryParams
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    bookmarkViewModel: BookmarkViewModel = koinViewModel(),
    viewModel: HistoryViewModel = koinViewModel(),
    navHostController: NavHostController = LocalNavigator.currentOrThrow,
) {
    HistoryScreen_(
        modifier = modifier,
        state = viewModel.state,
        dispatch = viewModel::dispatch,
        bookmarkState = bookmarkViewModel.state,
        bookmarkDispatch = bookmarkViewModel::dispatch,
        popBack = { navHostController.popBackStack() },
        navToPictureScreen = navHostController::navigateToPictureScreen
    )
}

@Preview
@Composable
internal fun HistoryScreen_(
    modifier: Modifier = Modifier,
    state: HistoryState = HistoryState.INITIAL,
    dispatch: (HistoryAction) -> Unit = {},
    bookmarkState: BookmarkState = BookmarkState.INITIAL,
    bookmarkDispatch: (BookmarkAction) -> Unit = {},
    popBack: () -> Unit = {},
    navToPictureScreen: (Illust, String) -> Unit = { _, _ -> },
) {
    var searchValue by remember { mutableStateOf(TextFieldValue(state.currentSearch)) }
    val focusManager = LocalFocusManager.current
    Screen(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = searchValue,
                        onValueChange = {
                            searchValue = it
                            dispatch(HistoryAction.UpdateSearch(it.text))
                        },
                        colors = transparentIndicatorColors.copy(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        placeholder = {
                            Text(text = stringResource(R.string.search_by_title_author))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { searchValue = TextFieldValue() }) {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = "Clear"
                                )
                            }
                        },
                        singleLine = true,
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = popBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
        val lazyGridState = rememberLazyGridState()
        IllustGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 8.dp),
            lazyGridState = lazyGridState,
            illusts = state.illusts.filter {
                it.title.contains(searchValue.text, ignoreCase = true) || it.user.name.contains(
                    searchValue.text,
                    ignoreCase = true
                )
            }.toImmutableList(),
            bookmarkState = bookmarkState,
            dispatch = bookmarkDispatch,
            spanCount = 2,
            navToPictureScreen = navToPictureScreen,
            canLoadMore = state.illustNextUrl != null,
            onLoadMore = {
                if (state.illustNextUrl != null) {
                    dispatch(HistoryAction.LoadHistory(state.illustNextUrl.queryParams))
                }
            },
            loading = state.loading,
        )
    }
}