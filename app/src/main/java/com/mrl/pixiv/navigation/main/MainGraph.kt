package com.mrl.pixiv.navigation.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
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
import com.mrl.pixiv.search.SearchResultScreen1
import com.mrl.pixiv.search.SearchScreen1
import com.mrl.pixiv.search.viewmodel.SearchViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun MainGraph(
    navHostController: NavHostController,
    offsetAnimation: IntOffset,
) {
    val homeViewModel: HomeViewModel = koinViewModel()
    val followViewModel: FollowViewModel = koinViewModel()
    val bookmarkViewModel: BookmarkViewModel = koinViewModel()
    NavHost(
        navController = navHostController,
        route = Graph.MAIN,
        startDestination = Destination.HomeScreen.route,
    ) {
        composable(
            route = Destination.HomeScreen.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = DestinationsDeepLink.HomePattern
                }
            ),

            ) {
            HomeScreen(
                navHostController = navHostController,
                homeViewModel = homeViewModel,
                bookmarkViewModel = bookmarkViewModel,
                offsetAnimation = offsetAnimation
            )
        }
        composable(
            route = Destination.ProfileScreen.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = DestinationsDeepLink.ProfilePattern
                }
            ),

            ) {
            ProfileScreen(
                navHostController = navHostController,
                bookmarkViewModel = bookmarkViewModel
            )
        }
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
        composable(
            route = Graph.SEARCH,
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
                    SearchScreen1(
                        searchNavHostController = searchNavHostController,
                        navHostController = navHostController,
                        searchViewModel = searchViewModel
                    )
                }
                composable(
                    route = Destination.SearchResultsScreen.route,
                ) {
                    SearchResultScreen1(
                        searchNavHostController = searchNavHostController,
                        navHostController = navHostController,
                        searchViewModel = searchViewModel,
                        bookmarkViewModel = bookmarkViewModel
                    )
                }
            }
        }
    }
}