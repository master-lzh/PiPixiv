package com.mrl.pixiv.navigation.root

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.router.Graph
import com.mrl.pixiv.login.LoginScreen
import com.mrl.pixiv.navigation.main.MainGraph
import kotlin.reflect.KClass

@Composable
fun RootNavigationGraph(
    navHostController: NavHostController,
    startDestination: KClass<*>,
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        route = Graph.Root::class
    ) {
        composable<Destination.LoginScreen> {
            LoginScreen(navHostController = navHostController)
        }
        composable<Graph.Main> {
            MainGraph()
        }
    }
}