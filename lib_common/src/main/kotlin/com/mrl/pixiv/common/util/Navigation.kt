package com.mrl.pixiv.common.util

import androidx.navigation.NavHostController
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.serialize.NavigationProtoBuf
import kotlinx.serialization.encodeToHexString

typealias NavigateToHorizontalPictureScreen = (illusts: List<Illust>, index: Int, prefix: String) -> Unit

fun NavHostController.navigateToPictureScreen(
    illusts: List<Illust>,
    index: Int,
    prefix: String
) {
    val encoded = NavigationProtoBuf.encodeToHexString(illusts)
    navigate(Destination.PictureScreen(encoded, index, prefix))
}

fun NavHostController.navigateToSearchScreen() {
    navigate(route = Destination.SearchScreen) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToSearchResultScreen(searchWord: String) {
    navigate(route = Destination.SearchResultsScreen(searchWord))
}

fun NavHostController.navigateToOutsideSearchResultScreen(searchWord: String) {
    navigate(route = Destination.SearchResultsScreen(searchWord)) {

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

fun NavHostController.navigateToSelfCollectionScreen() {
    navigate(route = Destination.SelfCollectionScreen)
}