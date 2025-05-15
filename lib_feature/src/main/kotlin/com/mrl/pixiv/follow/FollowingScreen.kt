package com.mrl.pixiv.follow

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.mrl.pixiv.common.compose.LocalNavigator
import com.mrl.pixiv.common.compose.deepBlue
import com.mrl.pixiv.common.compose.rememberThrottleClick
import com.mrl.pixiv.common.compose.ui.illust.SquareIllustItem
import com.mrl.pixiv.common.compose.ui.image.UserAvatar
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.kts.spaceBy
import com.mrl.pixiv.common.util.NavigateToHorizontalPictureScreen
import com.mrl.pixiv.common.util.RString
import com.mrl.pixiv.common.util.throttleClick
import com.mrl.pixiv.common.viewmodel.asState
import com.mrl.pixiv.common.viewmodel.bookmark.BookmarkState
import com.mrl.pixiv.common.viewmodel.bookmark.requireBookmarkState
import com.mrl.pixiv.common.viewmodel.follow.FollowState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun FollowingScreen(
    uid: Long,
    modifier: Modifier = Modifier,
    viewModel: FollowingViewModel = koinViewModel { parametersOf(uid) }
) {
    val state = viewModel.asState()
    val followingUsers = viewModel.followingPageSource.collectAsLazyPagingItems()
    val navigator = LocalNavigator.current
    val pullRefreshState = rememberPullToRefreshState()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(RString.followed))
                },
                navigationIcon = {
                    IconButton(
                        onClick = rememberThrottleClick {
                            navigator.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
        PullToRefreshBox(
            isRefreshing = followingUsers.loadState.refresh is LoadState.Loading,
            onRefresh = { followingUsers.refresh() },
            modifier = Modifier.padding(it),
            state = pullRefreshState
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {

            }
        }
    }
}

@Composable
private fun FollowingUserCard(
    illusts: ImmutableList<Illust>,
    userName: String,
    userId: Long,
    userAvatar: String,
    isFollowed: Boolean,
    navToPictureScreen: NavigateToHorizontalPictureScreen,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Row {
            illusts.take(3).forEachIndexed { index, it ->
                val isBookmarked = requireBookmarkState[it.id] ?: it.isBookmarked
                SquareIllustItem(
                    illust = it,
                    isBookmarked = isBookmarked,
                    onBookmarkClick = { restrict: String, tags: List<String>? ->
                        if (isBookmarked) {
                            BookmarkState.deleteBookmarkIllust(it.id)
                        } else {
                            BookmarkState.bookmarkIllust(it.id, restrict, tags)
                        }
                    },
                    navToPictureScreen = { prefix ->
                        navToPictureScreen(illusts, index, prefix)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = 8f.spaceBy,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UserAvatar(
                url = userAvatar,
                onClick = { navToPictureScreen(illusts, 0, "user") },
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = userName,
                modifier = Modifier.weight(1f)
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .then(
                        if (isFollowed)
                            Modifier.background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.medium
                            )
                        else
                            Modifier.border(
                                width = 1.dp,
                                color = deepBlue,
                                shape = MaterialTheme.shapes.medium
                            )
                    )
                    .padding(horizontal = 10.dp, vertical = 8.dp)
                    .throttleClick {
                        if (isFollowed) {
                            FollowState.followUser(userId)
                        } else {
                            FollowState.unFollowUser(userId)
                        }
                    },
                text = stringResource(if (isFollowed) RString.followed else RString.follow),
                style = TextStyle(
                    color = if (isFollowed) Color.White else deepBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                ),
            )

        }
    }
}

@Preview
@Composable
private fun FollowingUserCardPreview() {
    FollowingUserCard(
        illusts = persistentListOf(),
        userName = "asdasd",
        userId = 0,
        userAvatar = "http://iph.href.lu/200x200",
        isFollowed = false,
        navToPictureScreen = { _, _, _ -> },
    )
}