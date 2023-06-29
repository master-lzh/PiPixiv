package com.mrl.pixiv.navigation.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.router.DestinationsDeepLink
import com.mrl.pixiv.common.router.Graph
import com.mrl.pixiv.home.HomeScreen
import com.mrl.pixiv.profile.ProfileScreen
import com.mrl.pixiv.util.enterTransition
import com.mrl.pixiv.util.exitTransition
import com.mrl.pixiv.util.popEnterTransition

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainGraph(
    navHostController: NavHostController,
) {
    AnimatedNavHost(
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
            enterTransition = {
                when (initialState.destination.route) {
                    Destination.ProfileScreen.route -> {
                        enterTransition
                    }

                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Destination.ProfileScreen.route -> {
                        exitTransition
                    }

                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    Destination.ProfileScreen.route -> {
                        popEnterTransition
                    }

                    else -> null
                }
            }
        ) {
            HomeScreen(navHostController)
        }
        composable(
            route = Destination.ProfileScreen.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = DestinationsDeepLink.ProfilePattern
                }
            ),
            enterTransition = {
                when (initialState.destination.route) {
                    Destination.HomeScreen.route -> {
                        enterTransition
                    }

                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Destination.HomeScreen.route -> {
                        exitTransition
                    }

                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    Destination.HomeScreen.route -> {
                        popEnterTransition
                    }

                    else -> null
                }
            }
        ) {
            ProfileScreen(navHostController)
        }
    }
}