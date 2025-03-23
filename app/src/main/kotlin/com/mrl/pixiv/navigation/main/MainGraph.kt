package com.mrl.pixiv.navigation.main

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.mrl.pixiv.collection.SelfCollectionScreen
import com.mrl.pixiv.common.datasource.local.mmkv.requireUserInfoFlow
import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.router.DestinationsDeepLink
import com.mrl.pixiv.common.router.Graph
import com.mrl.pixiv.common.ui.LocalAnimatedContentScope
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.LocalSharedKeyPrefix
import com.mrl.pixiv.common.ui.LocalSharedTransitionScope
import com.mrl.pixiv.common.ui.components.HomeBottomBar
import com.mrl.pixiv.history.HistoryScreen
import com.mrl.pixiv.home.HomeScreen
import com.mrl.pixiv.home.HomeViewModel
import com.mrl.pixiv.picture.PictureScreen
import com.mrl.pixiv.profile.ProfileScreen
import com.mrl.pixiv.profile.detail.OtherProfileDetailScreen
import com.mrl.pixiv.profile.detail.SelfProfileDetailScreen
import com.mrl.pixiv.search.SearchScreen
import com.mrl.pixiv.search.SearchViewModel
import com.mrl.pixiv.search.preview.SearchPreviewScreen
import com.mrl.pixiv.search.result.OutsideSearchResultsScreen
import com.mrl.pixiv.search.result.SearchResultScreen
import com.mrl.pixiv.setting.SettingScreen
import com.mrl.pixiv.setting.SettingViewModel
import com.mrl.pixiv.setting.network.NetworkSettingScreen
import com.mrl.pixiv.splash.SplashViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainGraph(
    navHostController: NavHostController = rememberNavController()
) {
    val homeViewModel: HomeViewModel = koinViewModel()

    HandleDeeplink(navHostController)
    CompositionLocalProvider(
        LocalNavigator provides navHostController,
    ) {
        SharedTransitionLayout {
            Scaffold(
                bottomBar = {
                    HomeBottomBar(
                        navController = navHostController,
                        bottomBarState = bottomBarVisibility(navHostController)
                    )
                },
            ) { innerPadding ->
                val bottomPadding by remember(innerPadding) {
                    mutableStateOf(innerPadding.calculateBottomPadding())
                }
                CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                    NavHost(
                        navController = navHostController,
                        route = Graph.Main::class,
                        startDestination = Destination.HomeScreen,
                    ) {
                        // 首页
                        composable<Destination.HomeScreen>(
                            deepLinks = DestinationsDeepLink.HomePattern.map {
                                navDeepLink<Destination.HomeScreen>(
                                    basePath = it
                                )
                            },
                        ) {
                            CompositionLocalProvider(LocalAnimatedContentScope provides this@composable) {
                                HomeScreen(
                                    modifier = Modifier.padding(bottom = bottomPadding),
                                    homeViewModel = homeViewModel,
                                )
                            }
                        }

                        // 搜索预览页
                        composable<Destination.SearchPreviewScreen> {
                            SearchPreviewScreen(
                                modifier = Modifier.padding(bottom = bottomPadding),
                            )
                        }

                        // 个人主页
                        composable<Destination.ProfileScreen> {
                            CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                                ProfileScreen(
                                    modifier = Modifier.padding(bottom = bottomPadding),
                                )
                            }
                        }

                        // 个人详情页
                        composable<Destination.SelfProfileDetailScreen> {
                            CompositionLocalProvider(LocalAnimatedContentScope provides this@composable) {
                                SelfProfileDetailScreen()
                            }
                        }

                        // 他人详情页
                        composable<Destination.OtherProfileDetailScreen>(
                            deepLinks = DestinationsDeepLink.ProfileDetailPattern.map {
                                navDeepLink<Destination.OtherProfileDetailScreen>(
                                    basePath = it
                                )
                            },
                        ) {
                            CompositionLocalProvider(LocalAnimatedContentScope provides this@composable) {
                                val args = it.toRoute<Destination.OtherProfileDetailScreen>()
                                OtherProfileDetailScreen(
                                    uid = args.userId
                                )
                            }
                        }

                        // 作品详情页
                        composable<Destination.PictureScreen>(
                            enterTransition = { scaleIn(initialScale = 0.9f) + fadeIn() },
                            exitTransition = { scaleOut(targetScale = 1.1f) + fadeOut() },
                            popEnterTransition = { scaleIn(initialScale = 1.1f) + fadeIn() },
                            popExitTransition = { scaleOut(targetScale = 0.9f) + fadeOut() },
                        ) {
                            val args = it.toRoute<Destination.PictureScreen>()
                            CompositionLocalProvider(
                                LocalAnimatedContentScope provides this,
                                LocalSharedKeyPrefix provides args.prefix
                            ) {
                                PictureScreen(
                                    illustId = args.illustId,
                                )
                            }
                        }

                        composable<Destination.PictureDeeplinkScreen>(
                            deepLinks = DestinationsDeepLink.PicturePattern.map {
                                navDeepLink<Destination.PictureDeeplinkScreen>(
                                    basePath = it
                                )
                            },
                            enterTransition = { scaleIn(initialScale = 0.9f) + fadeIn() },
                            exitTransition = { scaleOut(targetScale = 1.1f) + fadeOut() },
                            popEnterTransition = { scaleIn(initialScale = 1.1f) + fadeIn() },
                            popExitTransition = { scaleOut(targetScale = 0.9f) + fadeOut() },
                        ) {
                            val illustId = it.toRoute<Destination.PictureDeeplinkScreen>().illustId
                            CompositionLocalProvider(
                                LocalAnimatedContentScope provides this,
                            ) {
                                PictureScreen(
                                    illustId = illustId,
                                )
                            }

                        }

                        // 搜索页
                        composable<Destination.SearchScreen> {
                            val searchViewModel: SearchViewModel =
                                koinViewModel(viewModelStoreOwner = it)
                            val searchNavHostController = rememberNavController()
                            CompositionLocalProvider(
                                LocalNavigator provides searchNavHostController,
                                LocalAnimatedContentScope provides this
                            ) {
                                NavHost(
                                    navController = searchNavHostController,
                                    route = Graph.Search::class,
                                    startDestination = Destination.SearchScreen
                                ) {
                                    composable<Destination.SearchScreen> {
                                        SearchScreen(
                                            navHostController = navHostController,
                                            searchViewModel = searchViewModel
                                        )
                                    }
                                    composable<Destination.SearchResultsScreen> {
                                        SearchResultScreen(
                                            searchWords = it.toRoute<Destination.SearchResultsScreen>().searchWords,
                                            navHostController = navHostController
                                        )
                                    }
                                }
                            }
                        }

                        // 外部搜索结果页
                        composable<Destination.SearchResultsScreen> {
                            val searchWord =
                                it.toRoute<Destination.SearchResultsScreen>().searchWords
                            CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                                OutsideSearchResultsScreen(
                                    searchWords = searchWord,
                                )
                            }

                        }

                        // 设置页
                        composable<Destination.SettingScreen> {
                            val settingViewModel: SettingViewModel =
                                koinViewModel(viewModelStoreOwner = LocalActivity.current as ComponentActivity)
                            val settingNavHostController = rememberNavController()
                            CompositionLocalProvider(LocalNavigator provides settingNavHostController) {
                                NavHost(
                                    navController = settingNavHostController,
                                    startDestination = Destination.SettingScreen
                                ) {
                                    composable<Destination.SettingScreen> {
                                        SettingScreen(
                                            mainNavHostController = navHostController
                                        )
                                    }

                                    // 网络设置页
                                    composable<Destination.NetworkSettingScreen> {
                                        NetworkSettingScreen(viewModel = settingViewModel)
                                    }
                                }
                            }
                        }

                        // 历史记录
                        composable<Destination.HistoryScreen> {
                            CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                                HistoryScreen()
                            }
                        }

                        // 本人收藏页
                        composable<Destination.SelfCollectionScreen> {
                            CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                                val userInfo by requireUserInfoFlow.collectAsStateWithLifecycle()
                                SelfCollectionScreen(userInfo.user.id)
                            }
                        }

                        composable<Destination.FollowingScreen>() {
                            CompositionLocalProvider(LocalAnimatedContentScope provides this) {

                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HandleDeeplink(
    navHostController: NavHostController
) {
    val splashViewModel: SplashViewModel =
        koinViewModel(viewModelStoreOwner = LocalActivity.current as ComponentActivity)
    val intent = splashViewModel.intent.collectAsStateWithLifecycle().value
    LaunchedEffect(intent) {
        if (intent != null) {
            val data = intent.data ?: return@LaunchedEffect
            when {
                DestinationsDeepLink.illustRegex.matches(data.toString()) -> {
                    navHostController.navigate(
                        Destination.PictureDeeplinkScreen(
                            data.lastPathSegment?.toLong() ?: 0
                        )
                    )
                }

                DestinationsDeepLink.userRegex.matches(data.toString()) -> {
                    navHostController.navigate(
                        Destination.OtherProfileDetailScreen(
                            data.lastPathSegment?.toLong() ?: 0
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun bottomBarVisibility(
    navController: NavController,
): Boolean {
    var bottomBarState by rememberSaveable { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    navController.currentDestination?.hasRoute(Destination.HomeScreen::class)
    bottomBarState = navBackStackEntry?.destination?.hasRoute<Destination.HomeScreen>() == true ||
            navBackStackEntry?.destination?.hasRoute<Destination.SearchPreviewScreen>() == true ||
            navBackStackEntry?.destination?.hasRoute<Destination.ProfileScreen>() == true
    return bottomBarState
}