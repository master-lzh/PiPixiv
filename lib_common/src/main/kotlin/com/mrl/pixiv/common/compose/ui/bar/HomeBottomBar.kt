package com.mrl.pixiv.common.compose.ui.bar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mrl.pixiv.common.router.Destination

@Composable
fun HomeBottomBar(
    navController: NavController,
    bottomBarState: Boolean,
    modifier: Modifier = Modifier,
) {
    val screens = listOf(
        Destination.HomeScreen,
        Destination.LatestScreen,
        Destination.SearchPreviewScreen,
        Destination.ProfileScreen,
    )
    AnimatedVisibility(
        visible = bottomBarState,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
    ) {
        Row(
            modifier = modifier,
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination
            screens.forEach { screen ->
                screen.icon
                NavigationBarItem(
                    icon = screen.icon!!,
                    label = {
                        Text(text = screen.title())
                    },
                    selected = currentRoute?.hasRoute(screen::class) == true,
                    onClick = {
                        if (currentRoute?.hasRoute(screen::class) == false) {
                            navController.navigate(screen) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    modifier = Modifier.requiredHeightIn(max = 56.dp)
                )
            }
        }
    }
}

