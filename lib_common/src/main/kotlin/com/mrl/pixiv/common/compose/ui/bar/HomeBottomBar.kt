package com.mrl.pixiv.common.compose.ui.bar

import androidx.compose.animation.*
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.mrl.pixiv.common.router.Destination

@Composable
fun HomeBottomBar(
    navController: NavController,
    bottomBarVisibility: Boolean,
    layoutType: NavigationSuiteType,
    currentRoute: NavDestination?,
) {
    val screens = listOf(
        Destination.HomeScreen,
        Destination.LatestScreen,
        Destination.SearchPreviewScreen,
        Destination.ProfileScreen,
    )
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    AnimatedVisibility(
        visible = bottomBarVisibility,
        enter = if (layoutType == NavigationSuiteType.NavigationBar) slideInVertically { it } else slideInHorizontally { if (isRtl) it else -it },
        exit = if (layoutType == NavigationSuiteType.NavigationBar) slideOutVertically { it } else slideOutHorizontally { if (isRtl) it else -it },
    ) {
        NavigationSuite(
            layoutType = layoutType,
            colors = NavigationSuiteDefaults.colors(),
            content = {
                screens.forEach { screen ->
                    item(
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
                        icon = screen.icon,
                        modifier = Modifier.requiredHeightIn(max = 56.dp),
                        label = {
                            Text(text = stringResource(screen.title))
                        }
                    )
                }
            }
        )
    }
}

