package com.mrl.pixiv.navigation.main

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.ui.BaseScreen
import kotlin.math.roundToInt

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    navHostController: NavHostController = rememberAnimatedNavController()
) {
    val bottomBarHeight = 56.dp
    val bottomBarOffsetHeightPx = remember { mutableStateOf(0f) }
    val offsetAnimation by animateIntOffsetAsState(
        targetValue = IntOffset(
            x = 0,
            y = -bottomBarOffsetHeightPx.value.roundToInt()
        ), label = ""
    )
    BaseScreen(
        modifier = Modifier.bottomBarAnimatedScroll(
            bottomBarHeight,
            bottomBarOffsetHeightPx,
            navHostController
        ),
        bottomBar = {
            HomeBottomBar(
                navController = navHostController,
                bottomBarState = bottomBarVisibility(navHostController),
                modifier = Modifier
                    .height(bottomBarHeight)
                    .offset { offsetAnimation }
            )
        }
    ) {
        MainGraph(navHostController)
    }
}

@Composable
fun HomeBottomBar(
    navController: NavController,
    bottomBarState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    val screens = listOf(
        Destination.HomeScreen,
        Destination.ProfileScreen,
    )
    AnimatedVisibility(
        visible = bottomBarState.value,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
    ) {
        BottomNavigation(
            modifier = modifier,
            backgroundColor = MaterialTheme.colors.background,
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            screens.forEach { screen ->
                screen.icon
                BottomNavigationItem(
                    icon = screen.icon!!,
                    label = {
                        Text(text = screen.title!!)
                    },
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

fun Modifier.bottomBarAnimatedScroll(
    height: Dp = 56.dp,
    offsetHeightPx: MutableState<Float>,
    navHostController: NavHostController
): Modifier = composed {
    val bottomBarHeightPx = with(LocalDensity.current) {
        height.roundToPx().toFloat()
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (navHostController.currentDestination?.route != Destination.HomeScreen.route) {
                    return Offset.Zero
                }
                val delta = available.y
                val newOffset = if (delta <= 0) {
                    offsetHeightPx.value - height.value
                } else {
                    offsetHeightPx.value + height.value
                }
                offsetHeightPx.value = newOffset.coerceIn(-bottomBarHeightPx, 0f)
                Log.d("TAG", "onPreScroll: ${available.y} ${offsetHeightPx.value}")

                return Offset.Zero
            }
        }
    }

    this.nestedScroll(nestedScrollConnection)
}

@Composable
fun bottomBarVisibility(
    navController: NavController,
): MutableState<Boolean> {

    val bottomBarState = rememberSaveable { (mutableStateOf(false)) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    when (navBackStackEntry?.destination?.route) {
        Destination.HomeScreen.route -> bottomBarState.value = true
        Destination.ProfileScreen.route -> bottomBarState.value = true
        else -> bottomBarState.value = false
    }

    return bottomBarState
}