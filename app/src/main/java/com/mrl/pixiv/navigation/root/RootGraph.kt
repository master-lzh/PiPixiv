package com.mrl.pixiv.navigation.root

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.router.Graph
import com.mrl.pixiv.login.LoginScreen
import com.mrl.pixiv.navigation.main.MainScreen

@Composable
fun RootNavigationGraph(
    navHostController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navHostController,
        route = Graph.ROOT,
        startDestination = startDestination
    ) {
        composable(Destination.LoginScreen.route) {
            LoginScreen(navHostController = navHostController)
        }
        composable(route = Graph.MAIN) {
            MainScreen()
        }
    }
}