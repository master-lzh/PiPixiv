package com.mrl.pixiv.navigation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.ui.LocalNavigator
import com.mrl.pixiv.common.ui.Screen
import com.mrl.pixiv.common.ui.components.HomeBottomBar

@Composable
fun MainScreen(
    navHostController: NavHostController = rememberNavController()
) {
    CompositionLocalProvider(LocalNavigator provides navHostController) {
        Screen(
            bottomBar = {
                HomeBottomBar(
                    navController = navHostController,
                    bottomBarState = bottomBarVisibility(navHostController)
                )
            },
        ) {
            MainGraph(
                modifier = Modifier.padding(bottom = it.calculateBottomPadding()),
                navHostController = navHostController
            )
        }
    }
}

@Composable
fun bottomBarVisibility(
    navController: NavController,
): MutableState<Boolean> {

    val bottomBarState = rememberSaveable { (mutableStateOf(false)) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    when (navBackStackEntry?.destination?.route) {
        Destination.HomeScreen.route -> bottomBarState.value = true
        Destination.SearchPreviewScreen.route -> bottomBarState.value = true
        Destination.ProfileScreen.route -> bottomBarState.value = true
        else -> bottomBarState.value = false
    }

    return bottomBarState
}