package com.mrl.pixiv.common.util

import android.util.Log
import androidx.navigation.NavHostController
import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.router.Graph
import com.mrl.pixiv.common.viewmodel.illust.IllustState
import com.mrl.pixiv.data.Illust
import org.koin.core.context.GlobalContext
import kotlin.time.measureTime

fun NavHostController.navigateToPictureScreen(illust: Illust, prefix: String) {
    measureTime {
        val koin = GlobalContext.get()
        val illustState = koin.getOrNull<IllustState>()
        illustState?.setIllust(illust.id, illust)
        navigate(Destination.PictureScreen(illust.id, prefix)) {
            restoreState = true
        }
    }.also {
        Log.i("TAG", "navigateToPictureScreen: $it")
    }
}

fun NavHostController.navigateToSearchScreen() {
    navigate(route = Destination.SearchScreen) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToSearchResultScreen(searchWord: String) {
    navigate(route = Destination.SearchResultsScreen(searchWord)) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToOutsideSearchResultScreen(searchWord: String) {
    navigate(route = Destination.SearchResultsScreen(searchWord)) {

    }
}

fun NavHostController.navigateToMainScreen() {
    navigate(route = Graph.Main) {
        launchSingleTop = true
    }
}

fun NavHostController.popBackToMainScreen() {
    popBackStack(route = Destination.HomeScreen, inclusive = false)
}

fun NavHostController.navigateToSelfProfileDetailScreen() {
    navigate(route = Destination.SelfProfileDetailScreen)
}

fun NavHostController.navigateToOtherProfileDetailScreen(userId: Long) {
    navigate(route = Destination.OtherProfileDetailScreen(userId))
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

fun NavHostController.navigateToSelfCollectionScreen() {
    navigate(route = Destination.SelfCollectionScreen)
}