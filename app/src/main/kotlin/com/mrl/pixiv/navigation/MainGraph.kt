package com.mrl.pixiv.navigation

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
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.datasource.local.mmkv.requireUserInfoFlow
import com.mrl.pixiv.common.network.JSON
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
import com.mrl.pixiv.login.LoginScreen
import com.mrl.pixiv.picture.HorizontalSwipePictureScreen
import com.mrl.pixiv.picture.PictureScreen
import com.mrl.pixiv.profile.ProfileScreen
import com.mrl.pixiv.profile.detail.OtherProfileDetailScreen
import com.mrl.pixiv.profile.detail.SelfProfileDetailScreen
import com.mrl.pixiv.search.SearchScreen
import com.mrl.pixiv.search.preview.SearchPreviewScreen
import com.mrl.pixiv.search.result.OutsideSearchResultsScreen
import com.mrl.pixiv.setting.SettingScreen
import com.mrl.pixiv.setting.SettingViewModel
import com.mrl.pixiv.setting.network.NetworkSettingScreen
import com.mrl.pixiv.splash.SplashViewModel
import io.ktor.util.decodeBase64String
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel
import kotlin.reflect.KClass

@Composable
fun MainGraph(
    startDestination: KClass<*>,
    navHostController: NavHostController = rememberNavController()
) {
    val homeViewModel: HomeViewModel = koinViewModel()

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
                        bottomBarState = bottomBarVisibility(navHostController)
                    )
                },
            ) { innerPadding ->
                val bottomPadding by remember(innerPadding) {
                    mutableStateOf(innerPadding.calculateBottomPadding())
                }
                NavHost(
                    navController = navHostController,
                    route = Graph.Main::class,
                    startDestination = startDestination,
                ) {
                    // 登陆
                    composable<Destination.LoginScreen> {
                        LoginScreen(navHostController = navHostController)
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
                        SearchScreen()
                    }

                    // 搜索结果页
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

                    composable<Destination.FollowingScreen> {
                        CompositionLocalProvider(LocalAnimatedContentScope provides this) {

                        }
                    }

                    composable<Destination.HorizontalPictureScreen> {
                        val params = it.toRoute<Destination.HorizontalPictureScreen>()
                        val illusts =
                            JSON.decodeFromString<List<Illust>>(params.illusts.decodeBase64String())
                        CompositionLocalProvider(
                            LocalAnimatedContentScope provides this,
                            LocalSharedKeyPrefix provides params.prefix
                        ) {
                            HorizontalSwipePictureScreen(
                                illusts = illusts.toImmutableList(),
                                index = params.index,
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