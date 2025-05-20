package com.mrl.pixiv.common.util

import androidx.navigation.NavHostController
import co.touchlab.kermit.Logger
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.repository.IllustCacheRepo
import com.mrl.pixiv.common.router.Destination
import kotlin.time.measureTime

typealias NavigateToHorizontalPictureScreen = (illusts: List<Illust>, index: Int, prefix: String) -> Unit

fun NavHostController.navigateToPictureScreen(
    illusts: List<Illust>,
    index: Int,
    prefix: String
) {
    measureTime {
        IllustCacheRepo[prefix] = illusts
        navigate(Destination.PictureScreen(index, prefix))
    }.let {
        Logger.i("Navigation") { "navigateToPictureScreen cost: $it" }
    }
}

fun NavHostController.navigateToSearchScreen() {
    navigate(route = Destination.SearchScreen) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToSearchResultScreen(searchWord: String) {
    navigate(route = Destination.SearchResultsScreen(searchWord))
}

fun NavHostController.loginToMainScreen() {
    navigate(Destination.HomeScreen) {
        popUpTo(Destination.LoginOptionScreen) { inclusive = true }
    }
}

fun NavHostController.popBackToMainScreen() {
    popBackStack(route = Destination.HomeScreen, inclusive = false)
}

fun NavHostController.navigateToProfileDetailScreen(userId: Long) {
    navigate(route = Destination.ProfileDetailScreen(userId))
}

fun NavHostController.navigateToSettingScreen() {
    navigate(route = Destination.SettingScreen)
}

fun NavHostController.navigateToNetworkSettingScreen() {
    navigate(route = Destination.NetworkSettingScreen)
}

fun NavHostController.navigateToHistoryScreen() {
    navigate(route = Destination.HistoryScreen)
}

fun NavHostController.navigateToCollectionScreen(userId: Long) {
    navigate(route = Destination.CollectionScreen(userId))
}

fun NavHostController.navigateToFollowingScreen(userId: Long) {
    navigate(route = Destination.FollowingScreen(userId))
}

fun NavHostController.navigateToLoginOptionScreen() {
    graph.setStartDestination(Destination.HomeScreen)
    navigate(route = Destination.LoginOptionScreen) {
        popUpTo(graph.startDestinationId) { inclusive = true }
        launchSingleTop = true
    }
}