package com.mrl.pixiv.navigation

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
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
import com.mrl.pixiv.collection.CollectionScreen
import com.mrl.pixiv.common.compose.LocalAnimatedContentScope
import com.mrl.pixiv.common.compose.LocalNavigator
import com.mrl.pixiv.common.compose.LocalSharedKeyPrefix
import com.mrl.pixiv.common.compose.LocalSharedTransitionScope
import com.mrl.pixiv.common.compose.ui.bar.HomeBottomBar
import com.mrl.pixiv.common.repository.IllustCacheRepo
import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.router.DestinationsDeepLink
import com.mrl.pixiv.common.router.Graph
import com.mrl.pixiv.follow.FollowingScreen
import com.mrl.pixiv.history.HistoryScreen
import com.mrl.pixiv.home.HomeScreen
import com.mrl.pixiv.latest.LatestScreen
import com.mrl.pixiv.login.LoginOptionScreen
import com.mrl.pixiv.login.LoginScreen
import com.mrl.pixiv.login.oauth.OAuthLoginScreen
import com.mrl.pixiv.picture.HorizontalSwipePictureScreen
import com.mrl.pixiv.picture.PictureDeeplinkScreen
import com.mrl.pixiv.profile.ProfileScreen
import com.mrl.pixiv.profile.detail.ProfileDetailScreen
import com.mrl.pixiv.search.SearchScreen
import com.mrl.pixiv.search.preview.SearchPreviewScreen
import com.mrl.pixiv.search.result.SearchResultsScreen
import com.mrl.pixiv.setting.SettingScreen
import com.mrl.pixiv.setting.SettingViewModel
import com.mrl.pixiv.setting.network.NetworkSettingScreen
import com.mrl.pixiv.splash.SplashViewModel
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel
import kotlin.reflect.KClass

@Composable
fun MainGraph(
    startDestination: KClass<*>,
    navHostController: NavHostController = rememberNavController()
) {
    HandleDeeplink(navHostController)
    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalNavigator provides navHostController,
            LocalSharedTransitionScope provides this
        ) {
            Scaffold(
                bottomBar = {
                    HomeBottomBar(
                        navController = navHostController,
                        bottomBarState = bottomBarVisibility(navHostController),
                        modifier = Modifier.navigationBarsPadding()
                    )
                },
                contentWindowInsets = WindowInsets.navigationBars
            ) { innerPadding ->
                NavHost(
                    navController = navHostController,
                    route = Graph.Main::class,
                    startDestination = startDestination,
                ) {
                    composable<Destination.LoginOptionScreen> {
                        LoginOptionScreen()
                    }
                    // 登陆
                    composable<Destination.LoginScreen> {
                        val startUrl = it.toRoute<Destination.LoginScreen>().startUrl
                        LoginScreen(startUrl = startUrl)
                    }
                    // OAuth token登陆
                    composable<Destination.OAuthLoginScreen> {
                        OAuthLoginScreen()
                    }
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
                                modifier = Modifier.padding(innerPadding),
                            )
                        }
                    }

                    // 新作页面
                    composable<Destination.LatestScreen> {
                        CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                            LatestScreen(modifier = Modifier.padding(innerPadding))
                        }
                    }

                    // 搜索预览页
                    composable<Destination.SearchPreviewScreen> {
                        SearchPreviewScreen(
                            modifier = Modifier.padding(innerPadding),
                        )
                    }

                    // 个人主页
                    composable<Destination.ProfileScreen> {
                        CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                            ProfileScreen(
                                modifier = Modifier.padding(innerPadding),
                            )
                        }
                    }

                    // 详情页
                    composable<Destination.ProfileDetailScreen>(
                        deepLinks = DestinationsDeepLink.ProfileDetailPattern.map {
                            navDeepLink<Destination.ProfileDetailScreen>(
                                basePath = it
                            )
                        },
                    ) {
                        CompositionLocalProvider(LocalAnimatedContentScope provides this@composable) {
                            val args = it.toRoute<Destination.ProfileDetailScreen>()
                            ProfileDetailScreen(
                                uid = args.userId
                            )
                        }
                    }

                    // 作品详情页（深度链接）
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
                            PictureDeeplinkScreen(
                                illustId = illustId,
                            )
                        }
                    }

                    // 搜索页
                    composable<Destination.SearchScreen> {
                        SearchScreen()
                    }

                    // 搜索结果页
                    composable<Destination.SearchResultsScreen> {
                        val searchWord =
                            it.toRoute<Destination.SearchResultsScreen>().searchWords
                        CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                            SearchResultsScreen(
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
                    composable<Destination.CollectionScreen> {
                        CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                            val userId = it.toRoute<Destination.CollectionScreen>().userId
                            CollectionScreen(uid = userId)
                        }
                    }

                    composable<Destination.FollowingScreen> {
                        CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                            val uid = it.toRoute<Destination.FollowingScreen>().userId
                            FollowingScreen(uid = uid)
                        }
                    }

                    // 横向滑动作品详情页
                    composable<Destination.PictureScreen>(
                        enterTransition = { scaleIn(initialScale = 0.9f) + fadeIn() },
                        exitTransition = { scaleOut(targetScale = 1.1f) + fadeOut() },
                        popEnterTransition = { scaleIn(initialScale = 1.1f) + fadeIn() },
                        popExitTransition = { scaleOut(targetScale = 0.9f) + fadeOut() },
                    ) {
                        val params = it.toRoute<Destination.PictureScreen>()
                        val illusts = remember { IllustCacheRepo[params.prefix] }
                        CompositionLocalProvider(
                            LocalAnimatedContentScope provides this,
                            LocalSharedKeyPrefix provides params.prefix
                        ) {
                            HorizontalSwipePictureScreen(
                                illusts = illusts.toImmutableList(),
                                index = params.index,
                                prefix = params.prefix,
                            )
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
                        Destination.ProfileDetailScreen(
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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    navController.currentDestination?.hasRoute(Destination.HomeScreen::class)
    return listOf(
        Destination.HomeScreen::class,
        Destination.LatestScreen::class,
        Destination.SearchPreviewScreen::class,
        Destination.ProfileScreen::class
    ).any { route ->
        navBackStackEntry?.destination?.hasRoute(route) == true
    }
}