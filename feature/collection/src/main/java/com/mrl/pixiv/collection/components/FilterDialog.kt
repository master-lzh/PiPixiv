package com.mrl.pixiv.collection.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mrl.pixiv.collection.R
import com.mrl.pixiv.collection.viewmodel.CollectionAction
import com.mrl.pixiv.common.ui.lightBlue
import com.mrl.pixiv.data.Restrict
import com.mrl.pixiv.data.user.BookmarkTag
import com.mrl.pixiv.util.AppUtil
import com.mrl.pixiv.util.throttleClick
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@Composable
fun FilterDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    selectedTab: Int,
    switchTab: (Int) -> Unit,
    pagerState: PagerState,
    userBookmarkTagsIllust: ImmutableList<BookmarkTag>,
    @Restrict restrict: String,
    filterTag: String,
    dispatch: (CollectionAction) -> Unit
) {
    val scope = rememberCoroutineScope()
    val onSelected = { tag: String ->
        when (selectedTab) {
            0 -> dispatch(CollectionAction.UpdateFilterTag(Restrict.PUBLIC, tag))
            1 -> dispatch(CollectionAction.UpdateFilterTag(Restrict.PRIVATE, tag))
        }
    }
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
                                text = stringResource(R.string.word_public),
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
                                text = stringResource(R.string.word_private),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.bookmark_tags),
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
                        item(key = "all") {
                            Text(
                                text = stringResource(R.string.all),
                                modifier = Modifier.itemModifier(
                                    ((restrict == Restrict.PUBLIC && currentPage == 0) || (restrict == Restrict.PRIVATE && currentPage == 1)) && filterTag.isEmpty(),
                                ) {
                                    onSelected("")
                                    onDismissRequest()
                                },
                            )
                        }
                        item(key = "uncategorized") {
                            Text(
                                text = stringResource(R.string.uncategorized),
                                modifier = Modifier.itemModifier(
                                    ((restrict == Restrict.PUBLIC && currentPage == 0) || (restrict == Restrict.PRIVATE && currentPage == 1)) && filterTag == stringResource(
                                        R.string.non_translate_uncategorized
                                    ),
                                ) {
                                    onSelected(AppUtil.getString(R.string.non_translate_uncategorized))
                                    onDismissRequest()
                                },
                            )
                        }
                        items(userBookmarkTagsIllust, key = { it.name }) {
                            Row(
                                modifier = Modifier.itemModifier(
                                    ((restrict == Restrict.PUBLIC && currentPage == 0) || (restrict == Restrict.PRIVATE && currentPage == 1)) && filterTag == it.name,
                                ) {
                                    onSelected(it.name)
                                    onDismissRequest()
                                },
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(text = it.name,)
                                Text(text = "${it.count}")
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
            .throttleClick(indication = rememberRipple()) {
                onClick()
            }
            .then(
                if (condition) Modifier.background(
                    lightBlue,
                    MaterialTheme.shapes.small
                ) else Modifier
            )
            .padding(8.dp)

    }
