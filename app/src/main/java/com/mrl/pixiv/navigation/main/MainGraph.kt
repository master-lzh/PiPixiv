package com.mrl.pixiv.navigation.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.router.DestinationsDeepLink
import com.mrl.pixiv.common.router.Graph
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.di.JSON
import com.mrl.pixiv.home.HomeScreen
import com.mrl.pixiv.picture.PictureScreen
import com.mrl.pixiv.profile.ProfileScreen
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalAnimationApi::class, ExperimentalEncodingApi::class)
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

            ) {
            HomeScreen(navHostController = navHostController)
        }
        composable(
            route = Destination.ProfileScreen.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = DestinationsDeepLink.ProfilePattern
                }
            ),

            ) {
            ProfileScreen(navHostController = navHostController)
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
            PictureScreen(illust = illust, navHostController = navHostController)
        }
    }
}