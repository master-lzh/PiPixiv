package com.mrl.pixiv.navigation.main

import androidx.activity.ComponentActivity
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.mrl.pixiv.collection.SelfCollectionScreen
import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.router.DestinationsDeepLink
import com.mrl.pixiv.common.router.Graph
import com.mrl.pixiv.common.ui.LocalAnimatedContentScope
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.LocalSharedKeyPrefix
import com.mrl.pixiv.common.ui.LocalSharedTransitionScope
import com.mrl.pixiv.common.ui.components.HomeBottomBar
import com.mrl.pixiv.common.viewmodel.bookmark.BookmarkViewModel
import com.mrl.pixiv.history.HistoryScreen
import com.mrl.pixiv.home.HomeScreen
import com.mrl.pixiv.home.viewmodel.HomeViewModel
import com.mrl.pixiv.picture.PictureScreen
import com.mrl.pixiv.profile.ProfileScreen
import com.mrl.pixiv.profile.detail.OtherProfileDetailScreen
import com.mrl.pixiv.profile.detail.SelfProfileDetailScreen
import com.mrl.pixiv.search.OutsideSearchResultsScreen
import com.mrl.pixiv.search.SearchResultScreen
import com.mrl.pixiv.search.SearchScreen
import com.mrl.pixiv.search.preview.SearchPreviewScreen
import com.mrl.pixiv.search.viewmodel.SearchViewModel
import com.mrl.pixiv.setting.SettingScreen
import com.mrl.pixiv.setting.network.NetworkSettingScreen
import com.mrl.pixiv.setting.viewmodel.SettingViewModel
import com.mrl.pixiv.splash.viewmodel.SplashViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainGraph(
    navHostController: NavHostController = rememberNavController()
) {
    val homeViewModel: HomeViewModel = koinViewModel()
    val bookmarkViewModel: BookmarkViewModel = koinViewModel()

    HandleDeeplink(navHostController)
    CompositionLocalProvider(LocalNavigator provides navHostController) {
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
                        route = Graph.MAIN,
                        startDestination = Destination.HomeScreen.route,
                    ) {
                        // 首页
                        composable(
                            route = Destination.HomeScreen.route,
                            deepLinks = DestinationsDeepLink.HomePattern.map {
                                navDeepLink {
                                    uriPattern = it
                                }
                            },
                        ) {
                            CompositionLocalProvider(LocalAnimatedContentScope provides this@composable) {
                                HomeScreen(
                                    modifier = Modifier.padding(bottom = bottomPadding),
                                    homeViewModel = homeViewModel,
                                    bookmarkViewModel = bookmarkViewModel,
                                )
                            }
                        }

                        // 搜索预览页
                        composable(
                            route = Destination.SearchPreviewScreen.route,
                        ) {
                            SearchPreviewScreen(
                                modifier = Modifier.padding(bottom = bottomPadding),
                            )
                        }

                        // 个人主页
                        composable(
                            route = Destination.ProfileScreen.route,
                        ) {
                            CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                                ProfileScreen(
                                    modifier = Modifier.padding(bottom = bottomPadding),
                                )
                            }
                        }

                        // 个人详情页
                        composable(
                            route = Destination.SelfProfileDetailScreen.route,
                        ) {
                            CompositionLocalProvider(LocalAnimatedContentScope provides this@composable) {
                                SelfProfileDetailScreen(
                                    bookmarkViewModel = bookmarkViewModel
                                )
                            }
                        }

                        // 他人详情页
                        composable(
                            route = "${Destination.OtherProfileDetailScreen.route}/{${Destination.OtherProfileDetailScreen.userId}}",
                            arguments = listOf(
                                navArgument(Destination.OtherProfileDetailScreen.userId) {
                                    defaultValue = 0L
                                }
                            ),
                            deepLinks = DestinationsDeepLink.ProfileDetailPattern.map {
                                navDeepLink {
                                    uriPattern = it
                                }
                            },
                        ) {
                            CompositionLocalProvider(LocalAnimatedContentScope provides this@composable) {
                                OtherProfileDetailScreen(
                                    uid = it.arguments?.getLong(Destination.OtherProfileDetailScreen.userId)
                                        ?: 0L,
                                    bookmarkViewModel = bookmarkViewModel
                                )
                            }
                        }

                        // 作品详情页
                        composable(
                            route = "${Destination.PictureScreen.route}/{${Destination.PictureScreen.illustId}}?prefix={${Destination.PictureScreen.prefix}}",
                            arguments = listOf(
                                navArgument(Destination.PictureScreen.illustId) {
                                    defaultValue = 0L
                                },
                                navArgument(Destination.PictureScreen.prefix) {
                                    defaultValue = ""
                                }
                            ),
                            enterTransition = { scaleIn(initialScale = 0.9f) + fadeIn() },
                            exitTransition = { scaleOut(targetScale = 1.1f) + fadeOut() },
                            popEnterTransition = { scaleIn(initialScale = 1.1f) + fadeIn() },
                            popExitTransition = { scaleOut(targetScale = 0.9f) + fadeOut() },
                        ) {
                            val illustId =
                                (it.arguments?.getLong(Destination.PictureScreen.illustId)) ?: 0L
                            val prefix =
                                it.arguments?.getString(Destination.PictureScreen.prefix) ?: ""
                            CompositionLocalProvider(
                                LocalAnimatedContentScope provides this,
                                LocalSharedKeyPrefix provides prefix
                            ) {
                                PictureScreen(
                                    illustId = illustId,
                                    bookmarkViewModel = bookmarkViewModel,
                                )
                            }
                        }

                        composable(
                            route = "${Destination.PictureDeeplinkScreen.route}/{${Destination.PictureDeeplinkScreen.illustId}}",
                            arguments = listOf(
                                navArgument(Destination.PictureDeeplinkScreen.illustId) {
                                    defaultValue = 0L
                                }
                            ),
                            deepLinks = DestinationsDeepLink.PicturePattern.map {
                                navDeepLink {
                                    uriPattern = it
                                }
                            },
                        ) {
                            val illustId =
                                it.arguments?.getLong(Destination.PictureDeeplinkScreen.illustId)
                                    ?: 0L
                            PictureScreen(
                                illustId = illustId,
                                bookmarkViewModel = bookmarkViewModel,
                            )
                        }

                        // 搜索页
                        composable(
                            route = Destination.SearchScreen.route,
                        ) {
                            val searchViewModel: SearchViewModel =
                                koinViewModel(viewModelStoreOwner = it)
                            val searchNavHostController = rememberNavController()
                            CompositionLocalProvider(
                                LocalNavigator provides searchNavHostController,
                                LocalAnimatedContentScope provides this
                            ) {
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
                            CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                                OutsideSearchResultsScreen(
                                    searchWord = searchWord,
                                    bookmarkViewModel = bookmarkViewModel,
                                )
                            }

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

                        // 历史记录
                        composable(
                            route = Destination.HistoryScreen.route,
                        ) {
                            CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                                HistoryScreen()
                            }
                        }

                        // 本人收藏页
                        composable(
                            route = Destination.SelfCollectionScreen.route,
                        ) {
                            CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                                SelfCollectionScreen(
                                    bookmarkViewModel = bookmarkViewModel
                                )
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
        koinViewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
    val intent = splashViewModel.intent.collectAsStateWithLifecycle().value
    LaunchedEffect(intent) {
        if (intent != null) {
            val data = intent.data ?: return@LaunchedEffect
            when {
                DestinationsDeepLink.illustRegex.matches(data.toString()) -> {
                    navHostController.navigate("${Destination.PictureDeeplinkScreen.route}/${data.lastPathSegment}")
                }

                DestinationsDeepLink.userRegex.matches(data.toString()) -> {
                    navHostController.navigate("${Destination.OtherProfileDetailScreen.route}/${data.lastPathSegment}")
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
    bottomBarState = when (navBackStackEntry?.destination?.route) {
        Destination.HomeScreen.route -> true
        Destination.SearchPreviewScreen.route -> true
        Destination.ProfileScreen.route -> true
        else -> false
    }
    return bottomBarState
}