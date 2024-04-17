package com.mrl.pixiv.search.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.Screen
import com.mrl.pixiv.common.ui.components.m3.TextField
import com.mrl.pixiv.common.ui.currentOrThrow
import com.mrl.pixiv.common_ui.util.navigateToOutsideSearchResultScreen
import com.mrl.pixiv.common_ui.util.navigateToSearchScreen
import com.mrl.pixiv.search.R
import com.mrl.pixiv.search.preview.components.TrendingItem
import com.mrl.pixiv.search.preview.viewmodel.SearchPreviewAction
import com.mrl.pixiv.search.preview.viewmodel.SearchPreviewState
import com.mrl.pixiv.search.preview.viewmodel.SearchPreviewViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchPreviewScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchPreviewViewModel = koinViewModel(),
    navHostController: NavHostController = LocalNavigator.currentOrThrow,
) {
    SearchPreviewScreen_(
        modifier = modifier,
        state = viewModel.state,
        dispatch = viewModel::dispatch,
        navToSearchScreen = navHostController::navigateToSearchScreen,
        navToSearchResultsScreen = navHostController::navigateToOutsideSearchResultScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
internal fun SearchPreviewScreen_(
    modifier: Modifier = Modifier,
    state: SearchPreviewState = SearchPreviewState.INITIAL,
    dispatch: (SearchPreviewAction) -> Unit = {},
    navToSearchScreen: () -> Unit = {},
    navToSearchResultsScreen: (String) -> Unit = {},
) {
    val textState by remember { mutableStateOf(TextFieldValue()) }
    Screen(
        modifier = modifier,
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
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            onClick = navToSearchScreen
                        ) {
                            TextField(
                                value = textState,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                onValueChange = {},
                                placeholder = { Text(stringResource(R.string.enter_keywords)) },
                                minHeight = 40.dp,
                                contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                                    top = 2.dp,
                                    bottom = 2.dp,
                                ),
                                colors = TextFieldDefaults.colors(
                                    disabledIndicatorColor = Color.Transparent,
                                ),
                                singleLine = true,
                                shape = MaterialTheme.shapes.extraLarge,
                                enabled = false,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Search,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            )
        }
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(
                span = { GridItemSpan(3) },
            ) {
                Text(
                    text = stringResource(R.string.popular_tags),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            items(state.trendingTags) { tag ->
                TrendingItem(
                    trendingTag = tag,
                    onSearch = {
                        navToSearchResultsScreen(it)
                        dispatch(SearchPreviewAction.AddSearchHistory(it))
                    }
                )
            }
        }
    }
}