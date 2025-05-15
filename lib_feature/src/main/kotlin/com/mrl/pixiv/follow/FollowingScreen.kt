package com.mrl.pixiv.follow

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.mrl.pixiv.common.compose.LocalNavigator
import com.mrl.pixiv.common.compose.rememberThrottleClick
import com.mrl.pixiv.common.util.RString
import com.mrl.pixiv.common.viewmodel.asState
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

@Preview
@Composable
private fun FollowingUserCard(
    modifier: Modifier = Modifier
) {

}