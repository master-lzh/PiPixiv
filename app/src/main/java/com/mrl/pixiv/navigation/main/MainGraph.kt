package com.mrl.pixiv.navigation.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.mrl.pixiv.common.router.Graph
import com.mrl.pixiv.common.router.Destination

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainGraph(
    navHostController: NavHostController,
) {
    AnimatedNavHost(
        navController = navHostController,
        route = Graph.MAIN,
        startDestination = Destination.HomeScreen.route
    ) {
        composable(Destination.HomeScreen.route) {
//            HomeScreen()
        }
        composable(Destination.ProfileScreen.route) {
//            ProfileScreen()
        }
    }
}