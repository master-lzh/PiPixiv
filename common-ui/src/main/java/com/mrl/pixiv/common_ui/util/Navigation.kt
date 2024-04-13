package com.mrl.pixiv.common_ui.util

import androidx.navigation.NavHostController
import com.mrl.pixiv.common.router.Destination
import com.mrl.pixiv.common.router.Graph
import com.mrl.pixiv.data.Illust
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun NavHostController.navigateToPictureScreen(illust: Illust) {
    navigate(
        route = "${Destination.PictureScreen.route}/${
            Base64.UrlSafe.encode(
                Json.encodeToString(illust)
                    .encodeToByteArray()
            )
        }"
    ) {
        restoreState = true
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

fun NavHostController.navigateToProfileDetailScreen() {
    navigate(route = Destination.ProfileDetailScreen.route)
}