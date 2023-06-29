package com.mrl.pixiv.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.ui.BaseScreen
import com.mrl.pixiv.data.Filter
import com.mrl.pixiv.data.auth.GrantType
import com.mrl.pixiv.data.illust.IllustBookmarkAddReq
import com.mrl.pixiv.data.illust.IllustBookmarkDeleteReq
import com.mrl.pixiv.data.illust.IllustRecommendedQuery
import com.mrl.pixiv.home.components.HomeContent
import com.mrl.pixiv.home.components.HomeTopBar
import com.mrl.pixiv.util.queryParams
import com.mrl.pixiv.util.second
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

val initRecommendedQuery = IllustRecommendedQuery(
    filter = Filter.ANDROID.filter,
    includeRankingIllusts = true,
    includePrivacyPolicy = true
)
const val TAG = "HomeScreen"
fun HomeViewModel.onRefresh() {
    dispatch(
        HomeUiIntent.RefreshIllustRecommendedIntent(initRecommendedQuery)
            .apply { forceHandle = true })
}

fun HomeViewModel.onRefreshToken() {
    dispatch(HomeUiIntent.RefreshTokenIntent(GrantType.REFRESH_TOKEN).apply { forceHandle = true })
}

fun HomeViewModel.onScrollToBottom() {
    dispatch(
        HomeUiIntent.LoadMoreIllustRecommendedIntent(
            queryMap = nextUrl?.queryParams
        )
    )
}

fun HomeViewModel.onBookmarkClick(id: Long, bookmark: Boolean) {
    if (bookmark) {
        dispatch(HomeUiIntent.IllustBookmarkAddIntent(IllustBookmarkAddReq(id)))
    } else {
        dispatch(HomeUiIntent.IllustBookmarkDeleteIntent(IllustBookmarkDeleteReq(id)))
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController,
    homeViewModel: HomeViewModel = koinViewModel()
) {
    var isRefreshing by rememberSaveable { mutableStateOf(true) }
    val lazyStaggeredGridState = rememberLazyStaggeredGridState()
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = {
        isRefreshing = true
        homeViewModel.onRefresh()
    })

    BaseScreen(
        title = stringResource(R.string.app_name),
        actions = {
            HomeTopBar(onRefresh = {
                isRefreshing = true
                homeViewModel.onRefresh()
            }, onRefreshToken = { homeViewModel.onRefreshToken() })
        }
    ) {
        Box(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .fillMaxSize()
        ) {
            HomeContent(
                viewModel = homeViewModel,
                lazyStaggeredGridState,
                onBookmarkClick = { id, bookmark ->
                    homeViewModel.onBookmarkClick(id, bookmark)
                },
                dismissRefresh = {
                    scope.launch {
                        lazyStaggeredGridState.scrollToItem(0)
                        delay(1.second)
                        isRefreshing = false
                    }
                }
            ) {
                homeViewModel.onScrollToBottom()
            }
            PullRefreshIndicator(
                modifier = Modifier.align(Alignment.TopCenter)
                    .apply {
                        if (homeViewModel.uiStateFlow.value.recommendImageList.isEmpty()) {
                            fillMaxSize()
                        }
                    },
                refreshing = isRefreshing,
                state = pullRefreshState,
            )
        }
    }
}