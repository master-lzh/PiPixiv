package com.mrl.pixiv.collection.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mrl.pixiv.collection.CollectionAction
import com.mrl.pixiv.collection.RestrictBookmarkTag
import com.mrl.pixiv.common.data.Restrict
import com.mrl.pixiv.common.ui.lightBlue
import com.mrl.pixiv.common.util.RString
import com.mrl.pixiv.common.util.conditionally
import com.mrl.pixiv.common.util.throttleClick
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@Composable
fun FilterDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    selectedTab: Int,
    switchTab: (Int) -> Unit,
    pagerState: PagerState,
    userBookmarkTagsIllust: ImmutableList<RestrictBookmarkTag>,
    privateBookmarkTagsIllust: ImmutableList<RestrictBookmarkTag>,
    @Restrict restrict: String,
    filterTag: String?,
    dispatch: (CollectionAction) -> Unit,
    onSelected: (String?) -> Unit
) {
    val scope = rememberCoroutineScope()
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
        ) {
            Column(modifier = Modifier) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(MaterialTheme.shapes.small)
                ) {
                    LaunchedEffect(pagerState.currentPage) {
                        when (pagerState.currentPage) {
                            0 -> switchTab(0)
                            1 -> switchTab(1)
                        }
                    }
                    TabRow(
                        selectedTabIndex = selectedTab,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small),
                        containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        indicator = {
                            Surface(
                                modifier = Modifier
                                    .tabIndicatorOffset(it[selectedTab])
                                    .fillMaxHeight(),
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            ) {}
                        },
                        divider = {},
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = {
                                scope.launch { pagerState.animateScrollToPage(0) }
                            },
                            modifier = Modifier.clip(MaterialTheme.shapes.small)
                        ) {
                            Text(
                                text = stringResource(RString.word_public),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        Tab(
                            selected = selectedTab == 1,
                            onClick = {
                                scope.launch { pagerState.animateScrollToPage(1) }
                            },
                            modifier = Modifier.clip(MaterialTheme.shapes.small)
                        ) {
                            Text(
                                text = stringResource(RString.word_private),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
                Text(
                    text = stringResource(RString.bookmark_tags),
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                        .padding(vertical = 8.dp)
                        .padding(start = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                )
                HorizontalPager(state = pagerState) { currentPage ->
                    LaunchedEffect(Unit) {
                        dispatch(CollectionAction.LoadUserBookmarksTagsIllust(if (currentPage == 0) Restrict.PUBLIC else Restrict.PRIVATE))
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .height(300.dp)
                    ) {
                        items(
                            if (currentPage == 0) userBookmarkTagsIllust else privateBookmarkTagsIllust,
                            key = { it.name.toString() }
                        ) {
                            Row(
                                modifier = Modifier.itemModifier(
                                    ((restrict == Restrict.PUBLIC && it.isPublic) || (restrict == Restrict.PRIVATE && !it.isPublic)) && filterTag == it.name,
                                ) {
                                    onSelected(it.name)
                                    onDismissRequest()
                                },
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(text = it.displayName)
                                if (it.count != null) {
                                    Text(text = it.count.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun Modifier.itemModifier(
    condition: Boolean,
    onClick: () -> Unit
): Modifier =
    composed {
        fillMaxWidth()
            .throttleClick(indication = ripple()) {
                onClick()
            }
            .conditionally(condition) {
                Modifier.background(lightBlue, MaterialTheme.shapes.small)
            }
            .padding(8.dp)
    }
