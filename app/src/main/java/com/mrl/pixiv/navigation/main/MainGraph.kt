package com.mrl.pixiv.navigation.main

import androidx.activity.ComponentActivity
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.currentOrThrow
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
import com.mrl.pixiv.setting.SettingScreen
import com.mrl.pixiv.setting.network.NetworkSettingScreen
import com.mrl.pixiv.setting.viewmodel.SettingViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun MainGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = LocalNavigator.currentOrThrow
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
            )
        }

        // 个人主页
        composable(
            route = Destination.ProfileScreen.route,
        ) {
            ProfileScreen(
                modifier = modifier,
            )
        }

        // 个人详情页
        composable(
            route = Destination.ProfileDetailScreen.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = DestinationsDeepLink.ProfileDetailPattern
                }
            ),
        ) {
            ProfileDetailScreen(
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
            enterTransition = { scaleIn(initialScale = 0.9f) + fadeIn() },
            exitTransition = { scaleOut(targetScale = 1.1f) + fadeOut() },
            popEnterTransition = { scaleIn(initialScale = 1.1f) + fadeIn() },
            popExitTransition = { scaleOut(targetScale = 0.9f) + fadeOut() },
        ) {
            val illustParams =
                (it.arguments?.getString(Destination.PictureScreen.illustParams)) ?: ""
            val illustDecode = Base64.UrlSafe.decode(illustParams).decodeToString()
            val illust = JSON.decodeFromString<Illust>(illustDecode)
            PictureScreen(
                illust = illust,
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
            CompositionLocalProvider(LocalNavigator provides searchNavHostController) {
                NavHost(
                    navController = searchNavHostController,
                    route = Graph.SEARCH,
                    startDestination = Destination.SearchScreen.route
                ) {
                    composable(
                        route = Destination.SearchScreen.route,
                    ) {
                        SearchScreen(
                            navHostController = navHostController,
                            searchViewModel = searchViewModel
                        )
                    }
                    composable(
                        route = Destination.SearchResultsScreen.route,
                    ) {
                        SearchResultScreen(
                            bookmarkViewModel = bookmarkViewModel,
                            searchViewModel = searchViewModel,
                            navHostController = navHostController
                        )
                    }
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
            )
        }

        // 设置页
        composable(
            route = Destination.SettingScreen.route,
        ) {
            val settingViewModel: SettingViewModel =
                koinViewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
            val settingNavHostController = rememberNavController()
            CompositionLocalProvider(LocalNavigator provides settingNavHostController) {
                NavHost(
                    navController = settingNavHostController,
                    startDestination = Destination.SettingScreen.route
                ) {
                    composable(
                        route = Destination.SettingScreen.route,
                    ) {
                        SettingScreen(
                            viewModel = settingViewModel,
                            mainNavHostController = navHostController
                        )
                    }

                    // 网络设置页
                    composable(
                        route = Destination.NetworkSettingScreen.route,
                    ) {
                        NetworkSettingScreen(viewModel = settingViewModel)
                    }
                }
            }
        }
    }
}