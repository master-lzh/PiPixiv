package com.mrl.pixiv.common.util

import android.util.Log
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.router.Graph
import com.mrl.pixiv.common.viewmodel.illust.IllustAction
import com.mrl.pixiv.common.viewmodel.illust.IllustViewModel
import com.mrl.pixiv.data.Illust
import org.koin.core.context.GlobalContext
import kotlin.time.measureTime

fun NavHostController.navigateToPictureScreen(illust: Illust, prefix: String) {
    measureTime {
        val koin = GlobalContext.get()
        val illustViewModel = koin.getOrNull<IllustViewModel>()
        illustViewModel?.dispatch(IllustAction.SetIllust(illust.id, illust))
        navigate(
            route = "${Destination.PictureScreen.route}/${illust.id}?prefix=${prefix}"
        ) {
            restoreState = true
        }
    }.also {
        Log.i("TAG", "navigateToPictureScreen: $it")
    }
}

fun NavHostController.navigateToSearchScreen() {
    navigate(route = Destination.SearchScreen.route) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToSearchResultScreen() {
    navigate(route = Destination.SearchResultsScreen.route) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToOutsideSearchResultScreen(searchWord: String) {
    navigate(route = "${Destination.SearchResultsScreen.route}/$searchWord") {

    }
}

fun NavHostController.navigateToMainScreen() {
    navigate(route = Graph.MAIN) {
        launchSingleTop = true
    }
}

fun NavHostController.popBackToMainScreen() {
    popBackStack(route = Destination.HomeScreen.route, inclusive = false)
}

fun NavHostController.navigateToSelfProfileDetailScreen() {
    navigate(route = Destination.SelfProfileDetailScreen.route)
}

fun NavHostController.navigateToOtherProfileDetailScreen(userId: Long) {
    navigate(route = "${Destination.OtherProfileDetailScreen.route}/$userId")
}

fun NavHostController.navigateToSettingScreen() {
    navigate(route = Destination.SettingScreen.route)
}

fun NavHostController.navigateToNetworkSettingScreen() {
    navigate(route = Destination.NetworkSettingScreen.route)
}

fun NavHostController.navigateToHistoryScreen() {
    navigate(route = Destination.HistoryScreen.route)
}

fun NavHostController.navigateToSelfCollectionScreen() {
    navigate(route = Destination.SelfCollectionScreen.route)
}