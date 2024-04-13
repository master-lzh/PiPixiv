package com.mrl.pixiv.navigation.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.mrl.pixiv.common.middleware.bookmark.BookmarkViewModel
import com.mrl.pixiv.common.middleware.follow.FollowViewModel
import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.router.DestinationsDeepLink
import com.mrl.pixiv.common.router.Graph
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.di.JSON
import com.mrl.pixiv.home.HomeScreen
import com.mrl.pixiv.home.viewmodel.HomeViewModel
import com.mrl.pixiv.picture.PictureScreen
import com.mrl.pixiv.profile.ProfileScreen
import com.mrl.pixiv.profile.detail.ProfileDetailScreen
import com.mrl.pixiv.search.OutsideSearchResultsScreen
import com.mrl.pixiv.search.SearchResultScreen
import com.mrl.pixiv.search.SearchScreen
import com.mrl.pixiv.search.preview.SearchPreviewScreen
import com.mrl.pixiv.search.viewmodel.SearchViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun MainGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
) {
    val homeViewModel: HomeViewModel = koinViewModel()
    val followViewModel: FollowViewModel = koinViewModel()
    val bookmarkViewModel: BookmarkViewModel = koinViewModel()
    NavHost(
        navController = navHostController,
        route = Graph.MAIN,
        startDestination = Destination.HomeScreen.route,
    ) {
        // 首页
        composable(
            route = Destination.HomeScreen.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = DestinationsDeepLink.HomePattern
                }
            ),
        ) {
            HomeScreen(
                modifier = modifier,
                navHostController = navHostController,
                homeViewModel = homeViewModel,
                bookmarkViewModel = bookmarkViewModel
            )
        }

        // 搜索预览页
        composable(
            route = Destination.SearchPreviewScreen.route,
        ) {
            SearchPreviewScreen(
                modifier = modifier,
                navHostController = navHostController,
            )
        }

        // 个人主页
        composable(
            route = Destination.ProfileScreen.route,
        ) {
            ProfileScreen(
                modifier = modifier,
                navHostController = navHostController,
            )
        }

        composable(
            route = Destination.ProfileDetailScreen.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = DestinationsDeepLink.ProfileDetailPattern
                }
            ),
        ) {
            ProfileDetailScreen(
                navHostController = navHostController,
                bookmarkViewModel = bookmarkViewModel
            )
        }

        // 作品详情页
        composable(
            route = "${Destination.PictureScreen.route}/{${Destination.PictureScreen.illustParams}}",
            arguments = listOf(
                navArgument(Destination.PictureScreen.illustParams) {
                    defaultValue = ""
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = DestinationsDeepLink.PicturePattern
                }
            ),
        ) {
            val illustParams =
                (it.arguments?.getString(Destination.PictureScreen.illustParams)) ?: ""
            val illustDecode = Base64.UrlSafe.decode(illustParams).decodeToString()
            val illust = JSON.decodeFromString<Illust>(illustDecode)
            PictureScreen(
                illust = illust,
                navHostController = navHostController,
                bookmarkViewModel = bookmarkViewModel,
                followViewModel = followViewModel,
            )
        }

        // 搜索页
        composable(
            route = Destination.SearchScreen.route,
        ) {
            val searchViewModel: SearchViewModel = koinViewModel(viewModelStoreOwner = it)
            val searchNavHostController = rememberNavController()
            NavHost(
                navController = searchNavHostController,
                route = Graph.SEARCH,
                startDestination = Destination.SearchScreen.route
            ) {
                composable(
                    route = Destination.SearchScreen.route,
                ) {
                    SearchScreen(
                        searchNavHostController = searchNavHostController,
                        navHostController = navHostController,
                        searchViewModel = searchViewModel
                    )
                }
                composable(
                    route = Destination.SearchResultsScreen.route,
                ) {
                    SearchResultScreen(
                        searchNavHostController = searchNavHostController,
                        bookmarkViewModel = bookmarkViewModel,
                        searchViewModel = searchViewModel,
                        navHostController = navHostController
                    )
                }
            }
        }

        // 外部搜索结果页
        composable(
            route = "${Destination.SearchResultsScreen.route}/{${Destination.SearchResultsScreen.searchWord}}",
            arguments = listOf(
                navArgument(Destination.SearchResultsScreen.searchWord) {
                    defaultValue = ""
                }
            ),
        ) {
            val searchWord =
                (it.arguments?.getString(Destination.SearchResultsScreen.searchWord))
                    ?: ""
            OutsideSearchResultsScreen(
                searchWord = searchWord,
                bookmarkViewModel = bookmarkViewModel,
                navHostController = navHostController,
            )
        }
    }
}